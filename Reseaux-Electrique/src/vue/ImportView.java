package vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;

/*
 * Vue pour importer || drag&drop un fihcier
 * 
 * @author Danil Guidjou
 */
public class ImportView {

    private VBox root = new VBox(20);
    private MainApp app;

    private File loadedFile = null;

    public ImportView(MainApp app) {
        this.app = app;
        
        // setup icon fichier
        ImageView fileIcon = new ImageView();
        fileIcon.setFitWidth(40);
        fileIcon.setFitHeight(40);

        Label fileLabel = new Label("Aucun fichier sélectionné");

        HBox fileInfo = new HBox(10, fileIcon, fileLabel);
        fileInfo.setAlignment(Pos.CENTER);

        // zone drag & drop
        Label dragLabel = new Label("Déposez un fichier .txt ici");
        dragLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #555;");

        StackPane dropZone = new StackPane(dragLabel);
        dropZone.setPrefSize(350, 150);
        dropZone.setStyle("-fx-border-color: #999; -fx-border-width: 2; -fx-background-color: #EEE;");
        dropZone.setPadding(new Insets(20));

        // fichier autoriser dans la zone de drag & drop
        dropZone.setOnDragOver(event -> {
            if (event.getGestureSource() != dropZone &&
                event.getDragboard().hasFiles() &&
                event.getDragboard().getFiles().get(0).getName().toLowerCase().endsWith(".txt")) {

                event.acceptTransferModes(TransferMode.COPY);
                dropZone.setStyle("-fx-border-color: #3A6EA5; -fx-background-color: #DDEEFF;");
            }
            event.consume();
        });

        dropZone.setOnDragExited(event -> {
            dropZone.setStyle("-fx-border-color: #999; -fx-background-color: #EEE;");
        });


        dropZone.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasFiles()) {
                File file = db.getFiles().get(0);

                if (file.getName().toLowerCase().endsWith(".txt")) {
                    setFileLoaded(file, fileLabel, fileIcon);
                    success = true;
                } else {
                    fileLabel.setText("Erreur : uniquement les fichiers .txt sont acceptés !");
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });

        // bouton choix du fichier
        Button chooseBtn = new Button("Choisir un fichier");

        chooseBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choisir un fichier texte");

            chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers texte (*.txt)", "*.txt")
            );

            File file = chooser.showOpenDialog(null);
            if (file != null) {
                setFileLoaded(file, fileLabel, fileIcon);
            }
        });

        // bouton suivant pour passer a la scene suivante 
        Button nextBtn = new Button("Suivant");

        nextBtn.setOnAction(e -> {
            if (loadedFile != null) {
                app.showDisplayView(loadedFile);
            } else {
                fileLabel.setText("Veuillez sélectionner un fichier d'abord !");
            }
        });

        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(dropZone, fileInfo, chooseBtn, nextBtn);
    }

    // Méthode pour afficher le fichier sélectionné et stocker loadedFile
    private void setFileLoaded(File file, Label label, ImageView icon) {
        loadedFile = file;
        label.setText("Fichier chargé : " + file.getName());

        // Icône
        icon.setImage(new Image(getClass().getResourceAsStream("/assets/file.png")));

        System.out.println("Fichier importé : " + file.getAbsolutePath());
    }

    public VBox getView() {
        return root;
    }
}
