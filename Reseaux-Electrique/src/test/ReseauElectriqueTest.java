package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.*;
import java.util.ArrayList;

class ReseauElectriqueTest {

    private ReseauElectrique rxe;
    private Generateur g1;
    private Maison m1;

    @BeforeEach
    void setUp() {
        // Initialisation avant chaque test
        rxe = new ReseauElectrique();
        g1 = new Generateur("Gen1", 100); // Capacité 100
        m1 = new Maison("M1", Consomation.FORTE); // Conso 40

        rxe.ajoutGenerateur(g1);
        rxe.ajoutMaison(m1);
    }

    @Test
    void testAjoutConnexionMetAJourCharge() {
        // Test du bug corrigé : l'ajout via connexion doit impacter la charge
        Connexion c = new Connexion(m1, g1);

        // Avant l'ajout, charge = 0
        assertEquals(0, g1.getChargeActu(), "La charge initiale doit être 0");

        rxe.ajoutConnexion(c);

        // Après l'ajout, charge = 40
        assertEquals(40, g1.getChargeActu(), "Le générateur devrait avoir une charge de 40 après connexion");
    }

    @Test
    void testValidationReseau() {
        // Cas invalide : Maison sans connexion
        assertFalse(rxe.validerReseau(), "Le réseau ne devrait pas être valide (Maison isolée)");

        // Cas valide : On connecte
        rxe.ajoutConnexion(m1, g1);
        assertTrue(rxe.validerReseau(), "Le réseau devrait être valide");
    }

    @Test
    void testSuppressionConnexion() {
        rxe.ajoutConnexion(m1, g1);
        assertEquals(40, g1.getChargeActu());

        rxe.supprimerConnexion(m1, g1);

        assertEquals(0, g1.getChargeActu(), "La charge doit revenir à 0 après suppression");
        assertTrue(rxe.getConnexions().isEmpty(), "La liste des connexions doit être vide");
    }
}