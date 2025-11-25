package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import model.Generateur;

class GenerateurTest {

    @Test
    void testMiseAJourCharge() {
        Generateur g = new Generateur("G1", 100);
        g.setChargeActu(20);
        assertEquals(20, g.getChargeActu(), "La charge devrait être de 20");

        g.setChargeActu(30); // Attention, votre code fait un += dans setChargeActu
        assertEquals(50, g.getChargeActu(), "La charge devrait s'additionner (20+30=50)");
    }

    @Test
    void testSoustraireCharge() {
        Generateur g = new Generateur("G1", 100);
        g.setChargeActu(50); // On initialise à 50

        g.soustraireCharge(20);
        assertEquals(30, g.getChargeActu(), "50 - 20 devrait faire 30");
    }

    @Test
    void testChargeNegativeImpossible() {
        Generateur g = new Generateur("G1", 100);
        g.setChargeActu(10);

        // On essaie de retirer plus que ce qu'il y a
        g.soustraireCharge(50);

        assertEquals(0, g.getChargeActu(), "La charge ne doit pas être négative, elle doit bloquer à 0");
    }
}