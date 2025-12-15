package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/**
 * Classe de test unitaire pour la classe ReseauElectrique.
 * Elle assure l'intégrité de la structure du réseau, la gestion des connexions
 * et la cohérence des charges électriques entre les composants.
 * * @author Anis Hammouche
 */
class ReseauElectriqueTest {

    private ReseauElectrique rxe;
    private Generateur g1;
    private Maison m1;

    /**
     * Initialisation de l'environnement de test.
     * Crée un nouveau réseau avec un générateur de 100kW et une maison à forte consommation (40kW).
     */
    @BeforeEach
    void setUp() {
        rxe = new ReseauElectrique();
        g1 = new Generateur("Gen1", 100);
        m1 = new Maison("M1", Consomation.FORTE);

        rxe.ajoutGenerateur(g1);
        rxe.ajoutMaison(m1);
    }

    /**
     * Vérifie que l'ajout d'une connexion met bien à jour la charge du générateur associé.
     * Ce test valide la synchronisation entre le modèle de connexion et l'état du générateur.
     */
    @Test
    void testAjoutConnexionMetAJourCharge() {
        // Préparation de la connexion
        Connexion c = new Connexion(m1, g1);

        // Avant l'ajout, la charge doit être nulle
        assertEquals(0, g1.getChargeActu(), "La charge initiale doit être 0");

        rxe.ajoutConnexion(c);

        // Après l'ajout, la charge doit correspondre à la consommation de la maison (40)
        assertEquals(40, g1.getChargeActu(), "Le générateur devrait avoir une charge de 40 après connexion");
    }

    /**
     * Teste la logique de validation du réseau électrique.
     * Vérifie qu'une maison ne peut pas rester isolée (sans connexion) dans un réseau valide.
     */
    @Test
    void testValidationReseau() {
        // Cas invalide : La maison m1 n'est reliée à aucun générateur
        assertFalse(rxe.validerReseau(), "Le réseau ne devrait pas être valide (Maison isolée)");

        // Cas valide : On établit la connexion
        rxe.ajoutConnexion(m1, g1);
        assertTrue(rxe.validerReseau(), "Le réseau devrait être valide après connexion de tous les éléments");
    }

    /**
     * Vérifie que la suppression d'une connexion libère correctement la charge du générateur.
     * S'assure également que l'objet connexion est bien retiré de la liste globale.
     */
    @Test
    void testSuppressionConnexion() {
        // Installation d'une connexion initiale
        rxe.ajoutConnexion(m1, g1);
        assertEquals(40, g1.getChargeActu());

        // Action de suppression
        rxe.supprimerConnexion(m1, g1);

        // Vérifications
        assertEquals(0, g1.getChargeActu(), "La charge doit revenir à 0 après suppression de la maison");
        assertTrue(rxe.getConnexions().isEmpty(), "La liste des connexions doit être vide après suppression");
    }
}