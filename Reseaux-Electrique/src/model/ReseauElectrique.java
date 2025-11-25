package model;

import java.util.ArrayList;
import java.util.Iterator;
// StringBuffer est dans java.lang, pas besoin d'import explicite, mais c'est bien de le savoir.

public class ReseauElectrique {
    // Renommés pour correspondre aux getters (getMaisons, getGens, getConnexions)
    private ArrayList<Maison> maisons;
    private ArrayList<Generateur> gens;
    private ArrayList<Connexion> connexions;

    public ReseauElectrique(ArrayList<Maison> m, ArrayList<Generateur> g) {
        maisons = m;
        gens = g;
        connexions = new ArrayList<>();
    }

    public ReseauElectrique() {
        this(new ArrayList<Maison>(), new ArrayList<Generateur>());
    }

    // --- Getters ---
    public ArrayList<Maison> getMaisons(){
        return maisons;
    }

    public ArrayList<Connexion> getConnexions(){
        return connexions;
    }

    public ArrayList<Generateur> getGens(){
        return gens;
    }
    
    //Ajout d'une Méthode utilitaire : retourne la connexion associée à une maison (ou null si aucune)
    // Permet d'éviter de répéter des boucles partout dans le code.
    private Connexion findConnexion(Maison m) {
        for (Connexion c : connexions) {
            if (c.getMs().equals(m)) {
                return c;
            }
        }
        return null;
    }


    

    // --- Méthodes de recherche (Nécessaires pour Utilisateur.java) ---
    public Maison findMaisonByName(String nom) {
        for (Maison m : maisons) {
            if (m.getNomM().equals(nom)) {
                return m;
            }
        }
        return null;
    }

    public Generateur findGenByName(String nom) {
        for (Generateur g : gens) {
            if (g.getNomG().equals(nom)) {
                return g;
            }
        }
        return null;
    }


    /**
     * VOTRE VERSION (CORRIGÉE) DE ajoutMaison
     * C'est la bonne logique.
     */
    public void ajoutMaison(Maison m) {
        if (maisons.isEmpty()) {
            maisons.add(m);
            System.out.println("  -> Maison '" + m.getNomM() + "' creee.");
        } else {
            for (Maison t : maisons) {
                if (t.equals(m)) {
                    // --- VOTRE BLOC DE CORRECTION (IL EST PARFAIT) ---

                    // 1. Sauvegarder les anciennes et nouvelles consommations
                    Consomation oldConso = t.getConso();
                    Consomation newConso = m.getConso();

                    // 2. Mettre à jour la maison
                    t.setConso(newConso);
                    System.out.println("  -> AVERTISSEMENT: Maison '" + t.getNomM() + "' mise a jour (Consommation: " + t.getConso() + ").");

                    // 3. Mettre à jour la charge du générateur connecté (s'il y en a un)
                    
                    //Recherche directe de la connexion de cette maison (évite la boucle for)
                    Connexion c = findConnexion(t);
                    if (c != null) {
                        Generateur g = c.getGen();
                        g.soustraireCharge(oldConso.getConso());
                        g.setChargeActu(newConso.getConso());
                        System.out.println("     -> Mise a jour charge " + g.getNomG() + ": " + g.getChargeActu() + "kwh");
                    }
                    // --- FIN DE VOTRE BLOC ---
                    return;
                }
            }
            maisons.add(m);
            System.out.println("  -> Maison '" + m.getNomM() + "' creee.");
        }
    }

    /**
     * Ajoute un générateur au réseau. 
     * Si un générateur avec le même nom existe déjà,
     * met à jour sa capacité maximale.
     *
     * @param g Le générateur à ajouter ou à mettre à jour.
     */
    public void ajoutGenerateur(Generateur g) {
        if (gens.isEmpty()) {
            gens.add(g);
            System.out.println("  -> Generateur '" + g.getNomG() + "' cree.");
        } else {
            for (Generateur t : gens) {
                if (t.equals(g)) {
                    t.setCapaMax(g.getCapaciteMax());
                    System.out.println("  -> AVERTISSEMENT: Generateur '" + t.getNomG() + "' mis a jour (Capacite: " + t.getCapaciteMax() + "kW).");
                    return;
                }
            }
            gens.add(g);
            System.out.println("  -> Generateur '" + g.getNomG() + "' cree.");
        }
    }


    /**
     * Ajoute une connexion entre une maison et un générateur.
     * Vérifie si les objets maison et générateur existent (non null) et 
     * si la connexion exacte n'existe pas déjà.
     * Met à jour la charge actuelle du générateur en lui ajoutant la consommation de la maison.
     * Note : Cette méthode peut créer des connexions multiples pour une même maison
     * (vers différents générateurs), qui seront détectées comme des erreurs
     * par la suite.
     *
     * @param m La maison à connecter (doit être non null).
     * @param g Le générateur auquel se connecter (doit être non null).
     */
    public void ajoutConnexion(Maison m, Generateur g) {
        // On vérifie juste si les objets existent
        if(g == null || m == null) {
            System.err.println("Maison OU Generateur non existant.");
            return;
        }

        Connexion temp = new Connexion(m, g);

        // On vérifie si EXACTEMENT la même connexion existe déjà
        if (connexions.contains(temp)) {
            System.err.println("  -> Erreur: Connexion deja existante.");
            return;
        }

        // On ajoute la connexion (même si la maison est déjà connectée à un AUTRE gen)
        connexions.add(temp);
        g.setChargeActu(m.getConso().getConso());
        System.out.println("  -> Connexion ajoutee entre '" + m.getNomM() + "' et '" + g.getNomG() + "'.");
    }
    
    /*
     * Ajouter une connexion 
     * 
     * @param co une connexion
     */
    public void ajoutConnexion(Connexion co) {
        connexions.add(co);

        // --- CORRECTION : Mettre à jour la charge du générateur ! ---
        Generateur g = co.getGen();
        Maison m = co.getMs();
        g.setChargeActu(m.getConso().getConso());
        // -----------------------------------------------------------
    }
    /**
     * Supprime une connexion spécifique entre une maison et un générateur.
     * Si la connexion est trouvée, elle est retirée de la liste et
     * la charge du générateur est mise à jour.
     *
     * @param m La maison faisant partie de la connexion à supprimer.
     * @param g Le générateur faisant partie de la connexion à supprimer.
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
                it.remove(); // Supprime la connexion de la liste
                g.soustraireCharge(m.getConso().getConso()); // Met à jour la charge
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
     * Valide la conformité du réseau.
     * Un réseau est considéré comme conforme si chaque maison enregistrée
     * possède exactement une connexion.
     * Les maisons sans connexion ou avec des connexions multiples sont
     * signalées comme des erreurs sur la sortie d'erreur standard.
     *
     * @return true si le réseau est conforme (chaque maison a 1 connexion), false sinon.
     */
    public boolean validerReseau() {
        ArrayList<String> problemes = new ArrayList<>();
        if (maisons.isEmpty() ) {
            System.out.println("  -> Reseau vide, validation necessaire.");
            problemes.add("Liste connexion vide");
            return false; // Un réseau vide est "conforme"
        }

        for (Maison m : maisons) {
            int count = 0;
            for (Connexion c : connexions) {
                if (c.getMs().equals(m)) {
                    count++;
                }
            }
            // Si une maison n'a pas 1 connexion (0 ou 2+)
            if (count != 1) {
                String typePb = (count == 0) ? "pas de connexion" : "trop de connexions (" + count + ")";
                problemes.add(m.getNomM() + " (probleme: " + typePb + ")");
            }
        }

        if (problemes.isEmpty()) {
            return true;
        } else {
            System.err.println("  -> ERREUR: Reseau non conforme.");
            System.err.println("    Le nom des maisons pour lesquelles il y a un probleme :");
            for (String p : problemes) {
                System.err.println("    - " + p);
            }
            return false;
        }
    }

    public String afficherConnexion() {
        StringBuffer t = new StringBuffer("\n--- ETAT ACTUEL DU RESEAU ---\n");

        if (gens.isEmpty() && maisons.isEmpty()) {
            return "Le reseau est vide.";
        }

        t.append("\n=== GENERATEURS ===\n");
        if(gens.isEmpty()) t.append("  (Aucun)\n");
        for(Generateur g : gens) {
            t.append(String.format("  -> %s (Capacite: %dkW, Charge actuelle: %dkW)\n",
                    g.getNomG(), g.getCapaciteMax(), g.getChargeActu()));
        }

        t.append("\n=== MAISONS (non connectées) ===\n");
        int maisonsNonConnectees = 0;
        for (Maison m : maisons) {
            
            //Vérifie si la maison est connectée en utilisant la méthode utilitaire findConnexion()
            boolean connectee = (findConnexion(m) != null);

            if (!connectee) {
                t.append(String.format("  -> %s (Consommation: %dkW)\n", m.getNomM(), m.getConso().getConso()));
                maisonsNonConnectees++;
            }
        }
        if (maisons.isEmpty()) t.append("  (Aucune)\n");
        else if (maisonsNonConnectees == 0) t.append("  (Toutes les maisons sont connectées)\n");


        t.append("\n=== TOPOLOGIE DES CONNEXIONS ===\n");
        if (connexions.isEmpty()) t.append("  (Aucune connexion)\n");
        for (Generateur g : gens) {
            t.append("+----------------+\n");
            t.append(String.format("| %-14s |\n", g.getNomG())); // Formatage pour aligner
            t.append("+----------------+\n");

            int maisonsConnectees = 0;
            for (Connexion c : connexions) {
                if(c.getGen().equals(g)) {
                    if (maisonsConnectees == 0) {
                        t.append("        | alimente ↓\n");
                    }
                    t.append("        +--> " + c.getMs().getNomM() + " (" + c.getMs().getConso().getConso() + "kW)\n" );
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