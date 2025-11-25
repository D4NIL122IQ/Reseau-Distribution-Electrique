package vue;

import java.util.InputMismatchException;
import java.util.Scanner;

import fileOperation.ParserFile;
import fileOperation.WriterFile;
import model.CoutRxElct;
import model.Optimiseur; // Import de l'algo
import model.ReseauElectrique;

public class Utilisateur {

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
     * Mode Fichier (Phase 2)
     */
    private static void lancerModeFichier(String filepath, double lambda) {
        ReseauElectrique rxe = null;

        System.out.println("Lecture du fichier : " + filepath + " ...");

        // --- 1. PARSING & VÉRIFICATION SYNTAXE (Source PDF: Vérifier format, erreurs lignes) ---
        try {
            // ParserFile a été modifié précédemment pour renvoyer des exceptions précises avec n° de ligne
            rxe = ParserFile.parser(filepath);
        } catch (Exception e) {
            System.err.println("\n[ERREUR CRITIQUE] Le fichier contient des erreurs de format :");
            System.err.println(" -> " + e.getMessage()); // Affiche l'erreur et la ligne
            System.err.println("Veuillez corriger le fichier avant de relancer.");
            return; // Arrêt du programme
        }

        // --- 2. VÉRIFICATION CONTRAINTES MÉTIER (Source PDF: contraintes phase 1) ---
        System.out.println("Fichier lu. Validation de la conformité du réseau...");
        if (!rxe.validerReseau()) {
            // validerReseau() affiche déjà les erreurs (maisons sans connexion, etc.) sur la sortie d'erreur
            System.err.println("Le réseau ne respecte pas les contraintes métier (voir ci-dessus).");
            return; // Arrêt du programme
        }

        System.out.println("Réseau valide et chargé avec succès !");

        // --- 3. MENU 3 OPTIONS (Source PDF: 1.Auto, 2.Save, 3.Fin) ---
        Scanner clavier = new Scanner(System.in);
        int choix = 0;

        // On prépare le calculateur avec le bon lambda pour les affichages
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
                String input = clavier.nextLine(); // Lecture en String pour gérer "abc", "1.5" etc.

                // Validation stricte de l'entier
                try {
                    choix = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    throw new InputMismatchException(); // On relance pour aller dans le catch commun
                }

                switch (choix) {
                    case 1:
                        System.out.println("\n--- Lancement de l'algorithme d'optimisation ---");
                        System.out.printf("Coût AVANT : %.2f\n", calcul.calculeCoutRxE());

                        // Appel de l'algorithme (Recuit Simulé ou Naïf selon votre choix dans Optimiseur)
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
                            // Vérifier que ce n'est pas le même fichier (optionnel mais conseillé)
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
                        // Gestion erreur "nombre hors limite" (Source PDF: "incorrecte s'il essaye de taper 3" ou autre)
                        System.err.println("Choix incorrect. Veuillez entrer 1, 2 ou 3.");
                }

            } catch (InputMismatchException e) {
                // Gestion erreur "type incorrect" (Source PDF: "1.5 ou abc")
                System.err.println("Erreur de saisie : Veuillez entrer un nombre entier (1, 2 ou 3).");
                choix = 0; // Reset
            }

        } while (choix != 3);

        clavier.close();
    }

    // --- MODE MANUEL (Phase 1 - Inchangé ou presque) ---
    private static void lancerModeManuel() {
        // ... (Copiez ici le contenu de votre ancien Main pour la création manuelle) ...
        // Pour que ça compile, j'ajoute juste un stub :
        System.out.println("(Le mode manuel s'exécute ici - Code de la phase 1)");
        // Vous pouvez remettre votre switch case de création manuelle ici.
        // Si vous voulez, je peux recoller le code complet de la phase 1 ici aussi.
    }
}