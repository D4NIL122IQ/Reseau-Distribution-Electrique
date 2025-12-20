package vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/*
* Classe représentant le menu principal de l'application.
*
* @author Danil Guidjou
*/
public class Menu {

    private VBox root; // Le conteneur principal

    public Menu(MainApp app) {
        root = new VBox(20); 
        root.setAlignment(Pos.CENTER); 
        root.setPadding(new Insets(50));
        
        Label titre = new Label("Menu Principal");
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button btnManuel = new Button("Créer un réseau manuellement");
        styleButton(btnManuel); 
        
        btnManuel.setOnAction(event -> {
            app.showCreatR();
        });

        Button btnImport = new Button("Importer réseau");
        styleButton(btnImport);

        btnImport.setOnAction(event -> {
            app.showImportView();
        });

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