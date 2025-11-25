package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import model.*;

class OptimiseurTest {

    @Test
    void testOptimisationReduitCout() {
        // Création d'un scénario "mauvais" (Surcharge)
        ReseauElectrique rxe = new ReseauElectrique();
        Generateur g1 = new Generateur("G1", 10); // Trop petit
        Generateur g2 = new Generateur("G2", 100); // Grand et vide
        Maison m1 = new Maison("M1", Consomation.FORTE); // 40kW

        rxe.ajoutGenerateur(g1);
        rxe.ajoutGenerateur(g2);
        rxe.ajoutMaison(m1);

        // Mauvaise connexion initiale : M1 sur G1 (Surcharge énorme)
        Connexion c = new Connexion(m1, g1);
        rxe.ajoutConnexion(c); // Charge G1 = 40 (Surcharge)

        CoutRxElct calc = new CoutRxElct(rxe);
        double coutAvant = calc.calculeCoutRxE();

        // Lancement de l'optimisation
        // L'algo DOIT déplacer M1 vers G2
        Optimiseur.resolutionAutomatique(rxe, 10.0);

        double coutApres = calc.calculeCoutRxE();

        assertTrue(coutApres < coutAvant, "L'optimisation aurait dû réduire le coût");
        assertEquals(0, calc.getSurcharge(), 0.01, "La surcharge devrait être éliminée");
    }
}