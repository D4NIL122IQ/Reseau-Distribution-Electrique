package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;

class CoutRxElctTest {

    private ReseauElectrique rxe;
    private CoutRxElct calculateur;
    private Generateur g1;
    private Maison m1;

    @BeforeEach
    void setUp() {
        rxe = new ReseauElectrique();
        g1 = new Generateur("G1", 10); // Petite capacité pour forcer la surcharge
        m1 = new Maison("M1", Consomation.NORMAL); // Consomme 20

        rxe.ajoutGenerateur(g1);
        rxe.ajoutMaison(m1);
        calculateur = new CoutRxElct(rxe);
    }

    @Test
    void testCalculCoutSurcharge() {
        // On connecte : Charge 20 sur Capa 10 -> Surcharge
        Connexion c = new Connexion(m1, g1);
        rxe.ajoutConnexion(c); // Utilise votre méthode corrigée qui met à jour la charge

        // Surcharge brute = (20 - 10) / 10 = 1.0
        // On fixe lambda à 10
        calculateur.setSeverite(10.0);

        double cout = calculateur.calculeCoutRxE();

        // Comme il n'y a qu'un générateur, la dispersion est 0.
        // Cout = 0 + 10 * 1.0 = 10.0
        assertEquals(10.0, cout, 0.01, "Le calcul du coût de surcharge est incorrect");
    }

    @Test
    void testReseauVide() {
        ReseauElectrique vide = new ReseauElectrique();
        CoutRxElct calcVide = new CoutRxElct(vide);
        assertEquals(0, calcVide.calculeCoutRxE(), "Un réseau vide doit coûter 0");
    }
}