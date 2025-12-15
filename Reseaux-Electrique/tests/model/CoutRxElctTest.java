package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Classe de test unitaire pour le calcul du coût d'un réseau électrique.
 * Vérifie que les algorithmes de surcharge et de dispersion fonctionnent
 * selon les règles métier établies.
 * * @author Anis Hammouche
 */
class CoutRxElctTest {

    private ReseauElectrique rxe;
    private CoutRxElct calculateur;
    private Generateur g1;
    private Maison m1;

    /**
     * Initialisation de l'environnement de test avant chaque méthode.
     * Prépare un réseau minimal composé d'un générateur et d'une maison.
     */
    @BeforeEach
    void setUp() {
        rxe = new ReseauElectrique();
        g1 = new Generateur("G1", 10); // Petite capacité pour forcer la surcharge
        m1 = new Maison("M1", Consomation.NORMAL); // Consomme 20 (Hypothèse selon le test)

        rxe.ajoutGenerateur(g1);
        rxe.ajoutMaison(m1);
        calculateur = new CoutRxElct(rxe);
    }

    /**
     * Teste le calcul du coût lorsque le réseau est en surcharge.
     * Vérifie l'application correcte du coefficient de sévérité sur la surcharge brute.
     */
    @Test
    void testCalculCoutSurcharge() {
        // On connecte : Charge 20 sur Capa 10 -> Surcharge
        Connexion c = new Connexion(m1, g1);
        rxe.ajoutConnexion(c); // Utilise la méthode qui met à jour la charge du générateur

        // Surcharge brute = (Conso - Capacité) / Capacité = (20 - 10) / 10 = 1.0
        // On fixe lambda (sévérité) à 10
        calculateur.setSeverite(10.0);

        double cout = calculateur.calculeCoutRxE();

        // Comme il n'y a qu'un seul générateur, la dispersion est de 0.
        // Cout final = Dispersion + (Severite * Surcharge) = 0 + (10 * 1.0) = 10.0
        assertEquals(10.0, cout, 0.01, "Le calcul du coût de surcharge est incorrect");
    }

    /**
     * Teste le comportement du calculateur avec un réseau ne contenant aucun élément.
     * Doit retourner un coût nul sans lever d'exception.
     */
    @Test
    void testReseauVide() {
        ReseauElectrique vide = new ReseauElectrique();
        CoutRxElct calcVide = new CoutRxElct(vide);
        assertEquals(0, calcVide.calculeCoutRxE(), "Un réseau vide doit coûter 0");
    }
}