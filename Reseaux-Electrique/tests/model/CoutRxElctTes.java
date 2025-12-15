package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Classe de test pour la classe CoutRxElct.
 * Vérifie le calcul correct des coûts liés à la surcharge et à la dispersion
 * ainsi que la gestion des cas limites (réseau vide).
 * * @author Anis Hammouche
 */
class CoutRxElctTes {

    private ReseauElectrique rxe;
    private CoutRxElct calculateur;
    private Generateur g1;
    private Maison m1;

    /**
     * Initialise l'environnement de test avant chaque méthode de test.
     * Crée un réseau de base avec un générateur et une maison pour simuler des scénarios.
     */
    @BeforeEach
    void setUp() {
        rxe = new ReseauElectrique();
        g1 = new Generateur("G1", 10); // Petite capacité pour forcer la surcharge
        m1 = new Maison("M1", Consomation.NORMAL); // Consomme 20

        rxe.ajoutGenerateur(g1);
        rxe.ajoutMaison(m1);
        calculateur = new CoutRxElct(rxe);
    }

    /**
     * Teste le calcul du coût en situation de surcharge.
     * Vérifie que la formule (Surcharge * Sévérité) est correctement appliquée
     * lorsque la consommation dépasse la capacité.
     */
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

    /**
     * Teste le comportement du calculateur sur un réseau vide.
     * Vérifie que le coût retourné est bien de 0 sans lever d'erreur.
     */
    @Test
    void testReseauVide() {
        ReseauElectrique vide = new ReseauElectrique();
        CoutRxElct calcVide = new CoutRxElct(vide);
        assertEquals(0, calcVide.calculeCoutRxE(), "Un réseau vide doit coûter 0");
    }
}