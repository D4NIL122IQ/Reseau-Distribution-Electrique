package vue;

import java.util.InputMismatchException;
import java.util.Scanner;

import fileOperation.ParserFile;
import fileOperation.WriterFile;
import model.CoutRxElct;
import model.Optimiseur;
import model.ReseauElectrique;

/**
 * Classe principale pilotant l'interface utilisateur en mode console (CLI).
 * Permet de gérer le cycle de vie du programme : chargement de fichier,
 * validation des données, optimisation du réseau et export des résultats.
 * * @author Anis Hammouche
 */
public class Utilisateur {

    /**
     * Point d'entrée du programme.
     * Analyse les arguments de la ligne de commande pour basculer entre le mode manuel
     * et le mode automatique basé sur un fichier de configuration.
     * * @param args Arguments de la ligne de commande (facultatifs : chemin du fichier, valeur lambda)
     */
    public static void main(String[] args) {

        // --- GESTION DES ARGUMENTS (Source PDF Section III) ---
        if (args.length == 0) {
            System.out.println("Aucun argument détecté. Lancement du mode manuel (Phase 1)...");
            lancerModeManuel();
        } else {
            String filepath = args[0];
            double lambda = 10.0; // Valeur par défaut si non précisée

            if (args.length >= 2) {
                try {
                    lambda = Double.parseDouble(args[1]);
                    System.out.println("Paramètre de sévérité (lambda) défini sur : " + lambda);
                } catch (NumberFormatException e) {
                    System.err.println("Attention : Le deuxième argument (lambda) n'est pas un nombre valide.");
                    System.err.println("La valeur par défaut (10.0) sera utilisée.");
                }
            }

            lancerModeFichier(filepath, lambda);
        }
    }

    /**
     * Gère le mode de fonctionnement basé sur un fichier (Phase 2).
     * S'occupe de la lecture, de la validation métier et propose un menu interactif
     * pour l'optimisation et la sauvegarde du réseau.
     * * @param filepath Le chemin vers le fichier de configuration du réseau
     * @param lambda Le coefficient de sévérité pour le calcul des surcharges
     */
    private static void lancerModeFichier(String filepath, double lambda) {
        ReseauElectrique rxe = null;

        System.out.println("Lecture du fichier : " + filepath + " ...");

        // --- 1. PARSING & VÉRIFICATION SYNTAXE ---
        try {
            rxe = ParserFile.parser(filepath);
        } catch (Exception e) {
            System.err.println("\n[ERREUR CRITIQUE] Le fichier contient des erreurs de format :");
            System.err.println(" -> " + e.getMessage());
            System.err.println("Veuillez corriger le fichier avant de relancer.");
            return;
        }

        // --- 2. VÉRIFICATION CONTRAINTES MÉTIER ---
        System.out.println("Fichier lu. Validation de la conformité du réseau...");
        if (!rxe.validerReseau()) {
            System.err.println("Le réseau ne respecte pas les contraintes métier (voir ci-dessus).");
            return;
        }

        System.out.println("Réseau valide et chargé avec succès !");

        // --- 3. MENU INTERACTIF ---
        Scanner clavier = new Scanner(System.in);
        int choix = 0;

        CoutRxElct calcul = new CoutRxElct(rxe);
        calcul.setSeverite(lambda);

        do {
            System.out.println("\n###################################");
            System.out.println("###   MENU PRINCIPAL (PROJET)   ###");
            System.out.println("###################################");
            System.out.println("1 - Résolution automatique (Optimisation)");
            System.out.println("2 - Sauvegarder la solution actuelle");
            System.out.println("3 - Fin");
            System.out.print("Votre choix : ");

            try {
                String input = clavier.nextLine();

                try {
                    choix = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    throw new InputMismatchException();
                }

                switch (choix) {
                    case 1:
                        System.out.println("\n--- Lancement de l'algorithme d'optimisation ---");
                        System.out.printf("Coût AVANT : %.2f\n", calcul.calculeCoutRxE());

                        // Appel de l'algorithme de résolution
                        Optimiseur.resolutionAutomatique(rxe, lambda);

                        System.out.printf("Coût APRÈS : %.2f\n", calcul.calculeCoutRxE());
                        break;

                    case 2:
                        System.out.println("\n--- Sauvegarde du réseau ---");
                        System.out.print("Entrez le nom du fichier de destination (ex: solution.txt) : ");
                        String saveFile = clavier.nextLine().trim();
                        if (saveFile.isEmpty()) {
                            System.err.println("Erreur : Nom de fichier vide.");
                        } else {
                            if (saveFile.equals(filepath)) {
                                System.out.println("Attention : Vous écrasez le fichier source.");
                            }
                            try {
                                WriterFile.saveReseau(rxe, saveFile);
                                System.out.println(" -> Sauvegarde réussie dans '" + saveFile + "'.");
                            } catch (Exception e) {
                                System.err.println(" -> Erreur sauvegarde : " + e.getMessage());
                            }
                        }
                        break;

                    case 3:
                        System.out.println("Fermeture du programme. Au revoir !");
                        break;

                    default:
                        System.err.println("Choix incorrect. Veuillez entrer 1, 2 ou 3.");
                }

            } catch (InputMismatchException e) {
                System.err.println("Erreur de saisie : Veuillez entrer un nombre entier (1, 2 ou 3).");
                choix = 0;
            }

        } while (choix != 3);

        clavier.close();
    }


}