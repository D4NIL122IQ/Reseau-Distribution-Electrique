package model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Classe représentant un réseau électrique complet.
 * Elle gère les listes de maisons, de générateurs et les connexions qui les lient.
 * Cette classe permet de manipuler la structure du réseau (ajout/suppression)
 * tout en assurant la cohérence des charges électriques.
 * * @author Anis et Danil
 */
public class ReseauElectrique {
    // Renommés pour correspondre aux getters (getMaisons, getGens, getConnexions)
    private ArrayList<Maison> maisons;
    private ArrayList<Generateur> gens;
    private ArrayList<Connexion> connexions;

    /**
     * Constructeur complet initialisant le réseau avec des listes prédéfinies.
     * @param m Liste initiale des maisons
     * @param g Liste initiale des générateurs
     * @param co Liste initiale des connexions
     */
    public ReseauElectrique(ArrayList<Maison> m, ArrayList<Generateur> g, ArrayList<Connexion> co) {
        maisons = m;
        gens = g;
        connexions = co;
    }

    /**
     * Constructeur initialisant le réseau sans connexions de départ.
     * @param m Liste initiale des maisons
     * @param g Liste initiale des générateurs
     */
    public ReseauElectrique(ArrayList<Maison> m, ArrayList<Generateur> g) {
        this(m, g, new ArrayList<>());
    }

    /**
     * Constructeur par défaut créant un réseau vide.
     */
    public ReseauElectrique() {
        this(new ArrayList<Maison>(), new ArrayList<Generateur>(), new ArrayList<>());
    }

    // --- Getters ---

    /**
     * Retourne la liste de toutes les maisons du réseau.
     * @return ArrayList de Maison
     */
    public ArrayList<Maison> getMaisons() {
        return maisons;
    }

    /**
     * Retourne la liste de toutes les connexions actives.
     * @return ArrayList de Connexion
     */
    public ArrayList<Connexion> getConnexions() {
        return connexions;
    }

    /**
     * Retourne la liste de tous les générateurs du réseau.
     * @return ArrayList de Generateur
     */
    public ArrayList<Generateur> getGens() {
        return gens;
    }

    /**
     * Méthode interne pour trouver la connexion associée à une maison.
     * @param m La maison à rechercher
     * @return La connexion trouvée ou null
     */
    private Connexion findConnexion(Maison m) {
        for (Connexion c : connexions) {
            if (c.getMs().equals(m)) {
                return c;
            }
        }
        return null;
    }

    // --- Méthodes de recherche ---

    /**
     * Recherche une maison par son nom unique.
     * @param nom Nom de la maison à chercher
     * @return L'objet Maison trouvé ou null
     */
    public Maison findMaisonByName(String nom) {
        for (Maison m : maisons) {
            if (m.getNomM().equals(nom)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Recherche un générateur par son nom unique.
     * @param nom Nom du générateur à chercher
     * @return L'objet Generateur trouvé ou null
     */
    public Generateur findGenByName(String nom) {
        for (Generateur g : gens) {
            if (g.getNomG().equals(nom)) {
                return g;
            }
        }
        return null;
    }

    /**
     * Ajoute une maison au réseau ou met à jour sa consommation si elle existe déjà.
     * En cas de mise à jour, la charge du générateur connecté est recalculée automatiquement.
     * @param m La maison à ajouter ou mettre à jour
     */
    public void ajoutMaison(Maison m) {
        if (maisons.isEmpty()) {
            maisons.add(m);
            System.out.println("  -> Maison '" + m.getNomM() + "' creee.");
        } else {
            for (Maison t : maisons) {
                if (t.equals(m)) {
                    // --- BLOC DE MISE À JOUR ---
                    Consomation oldConso = t.getConso();
                    Consomation newConso = m.getConso();

                    t.setConso(newConso);
                    System.out.println("  -> AVERTISSEMENT: Maison '" + t.getNomM() + "' mise a jour (Consommation: "
                            + t.getConso() + ").");

                    // Mise à jour de la charge du générateur lié
                    Connexion c = findConnexion(t);
                    if (c != null) {
                        Generateur g = c.getGen();
                        g.soustraireCharge(oldConso.getConso());
                        g.setChargeActu(newConso.getConso());
                        System.out.println(
                                "     -> Mise a jour charge " + g.getNomG() + ": " + g.getChargeActu() + "kwh");
                    }
                    return;
                }
            }
            maisons.add(m);
            System.out.println("  -> Maison '" + m.getNomM() + "' creee.");
        }
    }

    /**
     * Ajoute un générateur au réseau ou met à jour sa capacité maximale s'il existe déjà.
     * @param g Le générateur à ajouter ou mettre à jour
     */
    public void ajoutGenerateur(Generateur g) {
        if (gens.isEmpty()) {
            gens.add(g);
            System.out.println("  -> Generateur '" + g.getNomG() + "' cree.");
        } else {
            for (Generateur t : gens) {
                if (t.equals(g)) {
                    t.setCapaMax(g.getCapaciteMax());
                    System.out.println("  -> AVERTISSEMENT: Generateur '" + t.getNomG() + "' mis a jour (Capacite: "
                            + t.getCapaciteMax() + "kW).");
                    return;
                }
            }
            gens.add(g);
            System.out.println("  -> Generateur '" + g.getNomG() + "' cree.");
        }
    }

    /**
     * Crée une connexion entre une maison et un générateur existants.
     * Met à jour la charge actuelle du générateur.
     * @param m La maison à connecter
     * @param g Le générateur fournissant l'énergie
     */
    public void ajoutConnexion(Maison m, Generateur g) {
        if (g == null || m == null) {
            System.err.println("Maison OU Generateur non existant.");
            return;
        }

        Connexion temp = new Connexion(m, g);

        if (connexions.contains(temp)) {
            System.err.println("  -> Erreur: Connexion deja existante.");
            return;
        }

        connexions.add(temp);
        g.setChargeActu(m.getConso().getConso());
        System.out.println("  -> Connexion ajoutee entre '" + m.getNomM() + "' et '" + g.getNomG() + "'.");
    }

    /**
     * Ajoute un objet connexion directement au réseau et met à jour la charge du générateur.
     * @param co L'objet Connexion à ajouter
     */
    public void ajoutConnexion(Connexion co) {
        connexions.add(co);
        Generateur g = co.getGen();
        Maison m = co.getMs();
        g.setChargeActu(m.getConso().getConso());
    }

    /**
     * Supprime une connexion spécifique et déduit la consommation de la maison de la charge du générateur.
     * @param m La maison à déconnecter
     * @param g Le générateur concerné
     */
    public void supprimerConnexion(Maison m, Generateur g) {
        if (m == null || g == null) {
            System.err.println("Maison ou Generateur introuvable.");
            return;
        }

        Iterator<Connexion> it = connexions.iterator();
        boolean found = false;
        while (it.hasNext()) {
            Connexion c = it.next();
            if (c.getMs().equals(m) && c.getGen().equals(g)) {
                it.remove();
                g.soustraireCharge(m.getConso().getConso());
                System.out.println("  -> Connexion supprimee entre '" + m.getNomM() + "' et '" + g.getNomG() + "'.");
                found = true;
                break;
            }
        }
        if (!found) {
            System.err.println("  -> Erreur: Connexion non trouvee.");
        }
    }

    /**
     * Valide la conformité du réseau selon les règles métier.
     * Un réseau est valide si chaque maison possède exactement une connexion.
     * @return Un message décrivant les problèmes détectés, ou null si le réseau est conforme.
     */
    public String validerReseau() {
        StringBuffer problemes = new StringBuffer();

        if (maisons.isEmpty()) {
            System.out.println("  -> Reseau vide, validation necessaire.");
            problemes.append("Liste connexion vide\n");
            return null;
        }

        for (Maison m : maisons) {
            int count = 0;
            for (Connexion c : connexions) {
                if (c.getMs().equals(m)) {
                    count++;
                }
            }

            if (count != 1) {
                String typePb = (count == 0)
                        ? "pas de connexion"
                        : "trop de connexions (" + count + ")";

                problemes
                        .append(m.getNomM())
                        .append(" (probleme: ")
                        .append(typePb)
                        .append(")\n");
            }
        }

        if (problemes.length() == 0) {
            return null;
        }

        return problemes.toString();
    }

    /**
     * Génère une représentation textuelle complète de l'état actuel du réseau.
     * Affiche les générateurs, les maisons isolées et la topologie des connexions.
     * @return Une chaîne de caractères formatée pour l'affichage console.
     */
    public String afficherConnexion() {
        StringBuffer t = new StringBuffer("\n--- ETAT ACTUEL DU RESEAU ---\n");

        if (gens.isEmpty() && maisons.isEmpty()) {
            return "Le reseau est vide.";
        }

        t.append("\n=== GENERATEURS ===\n");
        if (gens.isEmpty())
            t.append("  (Aucun)\n");
        for (Generateur g : gens) {
            t.append(String.format("  -> %s (Capacite: %dkW, Charge actuelle: %dkW)\n", g.getNomG(), g.getCapaciteMax(),
                    g.getChargeActu()));
        }

        t.append("\n=== MAISONS (non connectées) ===\n");
        int maisonsNonConnectees = 0;
        for (Maison m : maisons) {
            boolean connectee = (findConnexion(m) != null);

            if (!connectee) {
                t.append(String.format("  -> %s (Consommation: %dkW)\n", m.getNomM(), m.getConso().getConso()));
                maisonsNonConnectees++;
            }
        }
        if (maisons.isEmpty())
            t.append("  (Aucune)\n");
        else if (maisonsNonConnectees == 0)
            t.append("  (Toutes les maisons sont connectées)\n");

        t.append("\n=== TOPOLOGIE DES CONNEXIONS ===\n");
        if (connexions.isEmpty())
            t.append("  (Aucune connexion)\n");
        for (Generateur g : gens) {
            t.append("+----------------+\n");
            t.append(String.format("| %-14s |\n", g.getNomG()));
            t.append("+----------------+\n");

            int maisonsConnectees = 0;
            for (Connexion c : connexions) {
                if (c.getGen().equals(g)) {
                    if (maisonsConnectees == 0) {
                        t.append("        | alimente ↓\n");
                    }
                    t.append("        +--> " + c.getMs().getNomM() + " (" + c.getMs().getConso().getConso() + "kW)\n");
                    maisonsConnectees++;
                }
            }
            if (maisonsConnectees == 0) {
                t.append("        (N'alimente aucune maison)\n");
            }
            t.append("\n");
        }
        t.append("-------------------------------\n");
        return t.toString();
    }
}