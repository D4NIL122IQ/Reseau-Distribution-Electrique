package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import model.Generateur;

/**
 * Classe de test unitaire pour la classe Generateur.
 * Cette classe vérifie le bon fonctionnement de la gestion de la charge électrique,
 * notamment l'accumulation, la soustraction et la sécurité contre les charges négatives.
 * * @author Anis Hammouche
 */
class GenerateurTest {

    /**
     * Vérifie que la mise à jour de la charge fonctionne correctement.
     * Teste si la méthode setChargeActu accumule correctement les valeurs
     * ajoutées successivement.
     */
    @Test
    void testMiseAJourCharge() {
        Generateur g = new Generateur("G1", 100);
        g.setChargeActu(20);
        assertEquals(20, g.getChargeActu(), "La charge devrait être de 20");

        // Test de l'effet cumulatif (+=)
        g.setChargeActu(30);
        assertEquals(50, g.getChargeActu(), "La charge devrait s'additionner (20+30=50)");
    }

    /**
     * Vérifie que la soustraction de charge diminue correctement la charge actuelle.
     */
    @Test
    void testSoustraireCharge() {
        Generateur g = new Generateur("G1", 100);
        g.setChargeActu(50); // Initialisation à 50 (via accumulation)

        g.soustraireCharge(20);
        assertEquals(30, g.getChargeActu(), "50 - 20 devrait faire 30");
    }

    /**
     * Vérifie la règle de gestion des limites inférieures.
     * La charge d'un générateur ne doit jamais devenir négative,
     * elle doit être ramenée à 0 si la soustraction est trop importante.
     */
    @Test
    void testChargeNegativeImpossible() {
        Generateur g = new Generateur("G1", 100);
        g.setChargeActu(10);

        // Tentative de retrait supérieur à la charge actuelle
        g.soustraireCharge(50);

        assertEquals(0, g.getChargeActu(), "La charge ne doit pas être négative, elle doit bloquer à 0");
    }
}