package vue;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import model.ReseauElectrique;
import java.io.File;

/*
 * Stage pricipale de l'interface graphe 
 * 
 * @author Danil Guidjou
 */
public class MainApp extends Application {

    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
            
        showMenu();

        stage.setTitle("Reseau de destribution electrique");

        stage.setResizable(false);  // bloque la possibilité de redimensionner

        stage.show();
    }

    
    /*
     * Permet de charger une scene ou on peut importer un fichier
     */
    public void showImportView() {
        ImportView view = new ImportView(this);
        stage.setScene(new Scene(view.getView(), 900, 650));
    }
    
    /*
     * Permet de charger une scene ou on peut afficher toute les infos du reseau importer
     */
    public void showDisplayView(File f ) {
        DisplayViewFromFile view = new DisplayViewFromFile(this, f);
        stage.setScene(new Scene(view.getView(), 900, 650));
    }
    
    public void showDisplayView(ReseauElectrique rxe ) {
    	DisplayViewFromRxe view = new DisplayViewFromRxe(this, rxe);
        stage.setScene(new Scene(view.getView(), 900, 650));
    }
    
    /*
     * Permet de lancer le menu
     */
    public void showMenu() {
    	Menu view = new Menu(this);
    	stage.setScene(new Scene(view.getView(), 900, 650));
    }
    
    /*
     * Permet de charger une scene ou on peut cree un reseau 
     */
    public void showCreatR() {
    	CreatReseau view = new CreatReseau(this);
    	stage.setScene(new Scene(view.getView(), 900, 650));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
