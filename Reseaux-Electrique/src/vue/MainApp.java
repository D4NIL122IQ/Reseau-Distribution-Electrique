package vue;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class MainApp extends Application {

    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
            
        showImportView();

        stage.setTitle("Reseau de destribution electrique");

        stage.setResizable(false);  // ← bloque la possibilité de redimensionner

        stage.show();
    }

    
    /*
     * Permet de charger une scene ou on peut importer un fichier
     */
    public void showImportView() {
        ImportView view = new ImportView(this);
        stage.setScene(new Scene(view.getView(), 900, 650));
    }

    public void showDisplayView(File f ) {
        DisplayView view = new DisplayView(this, f);
        stage.setScene(new Scene(view.getView(), 1000, 650));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
