package model;

import java.util.ArrayList;
import java.util.Random;

public class Optimiseur {

    /**
     * Algorithme de Recuit Simulé prenant en compte lambda.
     * @param rxe Le réseau
     * @param lambda La sévérité de la pénalisation
     */
    public static CoutRxElct resolutionAutomatique(ReseauElectrique rxe, double lambda) {
        if (rxe.getConnexions().isEmpty() || rxe.getGens().isEmpty()) return null;

        CoutRxElct calculateur = new CoutRxElct(rxe);
        calculateur.setSeverite(lambda); // IMPORTANT : On configure lambda

        Random rand = new Random();
        double temperature = 1000.0;
        double refroidissement = 0.9995;
        double temperatureMin = 0.01;

        double coutActuel = calculateur.calculeCoutRxE();
        System.out.println("  -> [Auto] Coût initial : " + String.format("%.3f", coutActuel));
        calculateur.afficherrxe();
        double meilleurCoutConnu = coutActuel;
        int iteration = 0;

        while (temperature > temperatureMin) {
            // 1. Sélection aléatoire
            ArrayList<Connexion> connexions = rxe.getConnexions();
            Connexion c = connexions.get(rand.nextInt(connexions.size()));
            Generateur ancienGen = c.getGen();

            ArrayList<Generateur> gens = rxe.getGens();
            Generateur nouveauGen = gens.get(rand.nextInt(gens.size()));

            if (ancienGen.equals(nouveauGen)) continue;

            // 2. Simulation
            int conso = c.getMs().getConso().getConso();
            effectuerDeplacement(c, ancienGen, nouveauGen, conso);

            double nouveauCout = calculateur.calculeCoutRxE();
            double delta = nouveauCout - coutActuel;

            // 3. Acceptation (Metropolis)
            boolean accepter = false;
            if (delta < 0) {
                accepter = true;
            } else {
                if (rand.nextDouble() < Math.exp(-delta / (temperature * 0.1))) {
                    accepter = true;
                }
            }

            if (accepter) {
                coutActuel = nouveauCout;
                if (coutActuel < meilleurCoutConnu) meilleurCoutConnu = coutActuel;
            } else {
                // Rollback
                effectuerDeplacement(c, nouveauGen, ancienGen, conso);
            }

            temperature *= refroidissement;
            iteration++;
        }

        System.out.println("  -> Optimisation terminée (" + iteration + " itérations).");
        System.out.println("  -> [Auto] Coût final : " + String.format("%.2f", coutActuel));
        calculateur.afficherrxe();
        
        return calculateur;
    }

    private static void effectuerDeplacement(Connexion c, Generateur source, Generateur dest, int conso) {
        source.soustraireCharge(conso);
        dest.setChargeActu(conso);
        c.setGen(dest);
    }
}