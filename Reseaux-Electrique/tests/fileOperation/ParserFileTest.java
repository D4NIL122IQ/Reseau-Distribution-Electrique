package fileOperation;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
//suppression de import fileOperation.ParserFile car on y est déjà
import exceptions.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * Classe de test unitaire pour le parseur de fichiers de configuration du réseau.
 * Vérifie la robustesse de la lecture des fichiers, le respect de la syntaxe
 * et la validation des contraintes d'ordre des instances.
 * * @author Anis Hammouche
 */
class ParserFileTest {

    /**
     * Méthode utilitaire pour générer dynamiquement un fichier texte de test.
     * * @param nom Le nom du fichier à créer
     * @param contenu Le texte à écrire à l'intérieur
     * @return Le chemin absolu du fichier créé
     * @throws IOException Si une erreur d'écriture survient
     */
    private String creerFichierTest(String nom, String contenu) throws IOException {
        File f = new File(nom);
        FileWriter fw = new FileWriter(f);
        fw.write(contenu);
        fw.close();
        return f.getAbsolutePath();
    }

    /**
     * Vérifie que le système réagit correctement lorsqu'un chemin de fichier invalide est fourni.
     * Le parseur doit propager une FileNotFoundException.
     */
    @Test
    void testFichierIntrouvable() {
        assertThrows(FileNotFoundException.class, () -> {
            ParserFile.parser("fichier_qui_n_existe_pas_12345.txt");
        });
    }

    /**
     * Teste la validation syntaxique stricte (présence du point final en fin de ligne).
     * Si une instruction ne se termine pas par un point, une exception FiniPointException est attendue.
     * * @throws IOException En cas de problème de création du fichier temporaire
     */
    @Test
    void testErreurSyntaxePoint() throws IOException {
        String path = creerFichierTest("test_syntaxe.txt",
                "generateur(G1, 100).\n" +
                        "generateur(G2, 100)\n" + // Erreur : Point manquant ici
                        "maison(M1, NORMAL)."
        );

        assertThrows(FiniPointException.class, () -> {
            ParserFile.parser(path);
        }, "Le parser devrait lever une exception si un point manque en fin de ligne");

        new File(path).delete(); // Nettoyage du fichier temporaire
    }

    /**
     * Vérifie que l'ordre logique de déclaration des instances est respecté.
     * Le format impose : Générateurs -> Maisons -> Connexions.
     * Toute connexion déclarée avant ses composants doit lever une OrdreInstanceException.
     * * @throws IOException En cas de problème de création du fichier temporaire
     */
    @Test
    void testErreurOrdre() throws IOException {
        String path = creerFichierTest("test_ordre.txt",
                "generateur(G1, 100).\n" +
                        "connexion(G1, M1).\n" + // Erreur : La maison M1 n'a pas encore été déclarée
                        "maison(M1, NORMAL)."
        );

        assertThrows(OrdreInstanceException.class, () -> {
            ParserFile.parser(path);
        }, "Le parser devrait lever une exception si l'ordre (Generateur -> Maison -> Connexion) n'est pas respecté");

        new File(path).delete(); // Nettoyage du fichier temporaire
    }
    
    /**
     * Vérifie qu'une maison ne peut pas être connectée plusieurs fois.
     * Une MaisonDejaConnecteeException doit être levée si une deuxième
     * connexion implique la même maison.
     */
    @Test
    void testMaisonDejaConnectee() throws IOException {
        String path = creerFichierTest("test_maison_deja_connectee.txt",
                "generateur(G1, 100).\n" +
                "generateur(G2, 100).\n" +
                "maison(M1, NORMAL).\n" +
                "connexion(G1, M1).\n" +
                "connexion(G2, M1).\n"
        );

        assertThrows(MaisonDejaConnecteeException.class, () -> {
            ParserFile.parser(path);
        });

        new File(path).delete();
    }

}