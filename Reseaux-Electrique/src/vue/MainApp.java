package vue;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();

        // Exemple d'icônes
        Image genImg = new Image("./assets/generateur-electrique.png");
        Image houseImg = new Image("./assets/maison.png");

        Map<String, ImageView> generateurs = new HashMap<>();
        Map<String, ImageView> maisons = new HashMap<>();

        // Ajout de deux générateurs
        ImageView g1 = new ImageView(genImg);
        g1.setFitWidth(50);
        g1.setFitHeight(50);
        g1.setLayoutX(100);
        g1.setLayoutY(100);
        generateurs.put("G1", g1);

        ImageView g2 = new ImageView(genImg);
        g2.setFitWidth(50);
        g2.setFitHeight(50);
        g2.setLayoutX(100);
        g2.setLayoutY(250);
        generateurs.put("G2", g2);

        // Ajout de maisons
        ImageView m1 = new ImageView(houseImg);
        m1.setFitWidth(50);
        m1.setFitHeight(50);
        m1.setLayoutX(400);
        m1.setLayoutY(100);
        maisons.put("M1", m1);

        ImageView m2 = new ImageView(houseImg);
        m2.setFitWidth(50);
        m2.setFitHeight(50);
        m2.setLayoutX(400);
        m2.setLayoutY(250);
        maisons.put("M2", m2);

        // Connexions (exemple)
        Line link1 = new Line(
                g1.getLayoutX() + 25,
                g1.getLayoutY() + 25,
                m1.getLayoutX() + 25,
                m1.getLayoutY() + 25
        );

        Line link2 = new Line(
                g2.getLayoutX() + 25,
                g2.getLayoutY() + 25,
                m2.getLayoutX() + 25,
                m2.getLayoutY() + 25
        );

        root.getChildren().addAll(link1, link2, g1, g2, m1, m2);

        Scene scene = new Scene(root, 700, 400);
        stage.setScene(scene);
        stage.setTitle("Réseau Électrique - Visualisation");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
