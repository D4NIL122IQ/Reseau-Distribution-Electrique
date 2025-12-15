package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Classe de test unitaire pour l'algorithme d'optimisation du réseau.
 * Cette classe vérifie que l'Optimiseur est capable de réorganiser les connexions
 * entre maisons et générateurs afin de minimiser le coût global et supprimer les surcharges.
 * * @author Anis Hammouche
 */
class OptimiseurTest {

    /**
     * Teste si l'algorithme d'optimisation réduit effectivement le coût du réseau.
     * Le scénario crée une surcharge volontaire sur un petit générateur alors qu'un
     * générateur plus puissant est disponible. On vérifie que l'optimiseur
     * déplace la connexion pour stabiliser le réseau.
     */
    @Test
    void testOptimisationReduitCout() {
        // Création d'un scénario "mauvais" (Surcharge volontaire)
        ReseauElectrique rxe = new ReseauElectrique();
        Generateur g1 = new Generateur("G1", 10);   // Capacité insuffisante
        Generateur g2 = new Generateur("G2", 100);  // Capacité largement suffisante
        Maison m1 = new Maison("M1", Consomation.FORTE); // Consomme 40kW

        rxe.ajoutGenerateur(g1);
        rxe.ajoutGenerateur(g2);
        rxe.ajoutMaison(m1);

        // Configuration initiale sous-optimale : M1 est branchée sur G1
        // Cela génère une surcharge car 40kW > 10kW
        Connexion c = new Connexion(m1, g1);
        rxe.ajoutConnexion(c);

        CoutRxElct calc = new CoutRxElct(rxe);
        double coutAvant = calc.calculeCoutRxE();

        // Lancement de l'algorithme de résolution automatique
        // L'algorithme doit détecter que déplacer M1 vers G2 est plus avantageux
        Optimiseur.resolutionAutomatique(rxe, 10.0);

        double coutApres = calc.calculeCoutRxE();

        // Vérifications des résultats
        assertTrue(coutApres < coutAvant, "L'optimisation aurait dû réduire le coût global du réseau");
        assertEquals(0, calc.getSurcharge(), 0.01, "La surcharge totale devrait être éliminée après optimisation");
    }
}