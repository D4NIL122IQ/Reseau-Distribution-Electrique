package vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;

public class Menu {

    private VBox root; // Le conteneur principal

    public Menu(MainApp app) {
        // 1. Initialisation du conteneur VBox
        // Espacement de 20px entre les éléments
        root = new VBox(20); 
        root.setAlignment(Pos.CENTER); // Centrer le contenu
        root.setPadding(new Insets(50)); // Marge autour du menu
        
        // (Optionnel) Ajout d'un titre ou d'un logo
        Label titre = new Label("Menu Principal");
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // 2. Création du bouton "Créer un réseau manuellement"
        Button btnManuel = new Button("Créer un réseau manuellement");
        styleButton(btnManuel); // Appliquer un style
        
        btnManuel.setOnAction(event -> {
            app.showCreatR();
        });

        // 3. Création du bouton "Importer réseau"
        Button btnImport = new Button("Importer réseau");
        styleButton(btnImport); // Appliquer un style

        btnImport.setOnAction(event -> {
            app.showImportView();
        });

        // 4. Ajout des éléments au VBox
        root.getChildren().addAll(titre, btnManuel, btnImport);
    }

    /**
     * Méthode utilitaire pour donner une taille et un style aux boutons
     */
    private void styleButton(Button btn) {
        btn.setMinWidth(250);
        btn.setMinHeight(40);
        btn.setStyle("-fx-font-size: 14px; -fx-base: #4a90e2;");
    }

    public VBox getView() {
        return root;
    }
}