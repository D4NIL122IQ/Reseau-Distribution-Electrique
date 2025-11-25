package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import fileOperation.ParserFile;
import exceptions.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

class ParserFileTest {

    // Méthode utilitaire pour créer un fichier temporaire pour le test
    private String creerFichierTest(String nom, String contenu) throws IOException {
        File f = new File(nom);
        FileWriter fw = new FileWriter(f);
        fw.write(contenu);
        fw.close();
        return f.getAbsolutePath();
    }

    @Test
    void testFichierIntrouvable() {
        // Vérifie que le parser laisse bien remonter l'exception si le fichier n'existe pas
        assertThrows(FileNotFoundException.class, () -> {
            ParserFile.parser("fichier_qui_n_existe_pas_12345.txt");
        });
    }

    @Test
    void testErreurSyntaxePoint() throws IOException {
        // Fichier sans le point final à la ligne 2
        String path = creerFichierTest("test_syntaxe.txt",
                "generateur(G1, 100).\n" +
                        "generateur(G2, 100)\n" + // Pas de point ici !
                        "maison(M1, NORMAL)."
        );

        // On s'attend à une exception FiniPointException
        assertThrows(FiniPointException.class, () -> {
            ParserFile.parser(path);
        }, "Le parser devrait lever une exception si un point manque en fin de ligne");

        new File(path).delete(); // Nettoyage du fichier temporaire
    }

    @Test
    void testErreurOrdre() throws IOException {
        // Connexion définie avant la Maison -> Interdit selon le sujet
        String path = creerFichierTest("test_ordre.txt",
                "generateur(G1, 100).\n" +
                        "connexion(G1, M1).\n" + // ERREUR : Connexion trop tôt !
                        "maison(M1, NORMAL)."
        );

        // On s'attend à OrdreInstanceException (et non MGException)
        assertThrows(OrdreInstanceException.class, () -> {
            ParserFile.parser(path);
        }, "Le parser devrait lever une exception si l'ordre (Generateur -> Maison -> Connexion) n'est pas respecté");

        new File(path).delete(); // Nettoyage
    }
}