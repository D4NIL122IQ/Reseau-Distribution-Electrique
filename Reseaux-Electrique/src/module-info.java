module ReseauElectrique {
    // 1. Les modules dont vous avez besoin (Dépendances)
    requires java.base; // Implicite, mais bon à savoir
    requires org.junit.jupiter.api;
    requires org.junit.platform.commons; // Nécessaire pour lancer les tests

    // 2. Les paquets de votre code que vous rendez publics
    exports model;
    exports vue;
    exports fileOperation;
    exports exceptions;

    // 3. IMPORTANT : Ouvrir les tests à JUnit
    // JUnit utilise la "réflexion" pour lancer les tests, il faut lui donner la permission
    opens test to org.junit.platform.commons, org.junit.jupiter.api;

    // Si vous utilisez JavaFX pour le bonus plus tard, il faudra ajouter :
    // requires javafx.controls;
    // exports vue to javafx.graphics;
}