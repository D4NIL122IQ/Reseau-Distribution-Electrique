package vue;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.lang.StringBuffer;

import model.*;


/*
 * Vue qui affiche graphiquement un reseau créé manuellement
 * Affiche le coût du reseau
 * Permet de lancer une optimisation
 * Exporter le reseau optimisé
 * 
 * @author Danil Guidjou
 */
public class DisplayViewFromRxe {

    private BorderPane root = new BorderPane();
    private Pane graphPane = new Pane();

    private Label statusLabel = new Label();
    
    // Champ pour sélectionner la sévérité
    private TextArea severityInput = new TextArea("10"); 
    
    private MainApp app;
    private ReseauElectrique rxe;

    private final double ESPACE_X = 120;
    private final double MARGE_X = 50;
    private final double Y_GEN = 50;
    private final double Y_MAISON = 400;

    /**
     * Constructeur prenant une instance de ReseauElectrique directement
     */
    public DisplayViewFromRxe(MainApp app, ReseauElectrique reseauInput) {
        this.app = app;
        this.rxe = reseauInput; // On récupère l'instance passée en paramètre

        try {
            // Un panel scroll qui contiendra le réseau
            ScrollPane scrollPane = new ScrollPane(graphPane);
            scrollPane.setPannable(true);
            scrollPane.setFitToHeight(true);

            VBox bottomContainer = new VBox(10); 
            bottomContainer.setAlignment(Pos.CENTER);
            bottomContainer.setPadding(new javafx.geometry.Insets(15));
            bottomContainer.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-width: 1 0 0 0;");

            Label lblSeverite = new Label("Sévérité :");
            lblSeverite.setStyle("-fx-font-weight: bold;");

            severityInput.setPrefRowCount(1);      
            severityInput.setPrefColumnCount(3); 
            severityInput.setMaxHeight(30);        
            
            // Validation pour n'accepter que des chiffres
            severityInput.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    severityInput.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });

            statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333; -fx-font-weight: bold;");

            HBox infoBar = new HBox(10); 
            infoBar.setAlignment(Pos.CENTER);
            
            infoBar.getChildren().addAll(lblSeverite, severityInput, new Label("   |   "), statusLabel);

            // Boutons
            HBox buttonBar = new HBox(15);
            buttonBar.setAlignment(Pos.CENTER);

            Button exportBtn = new Button("Exporter");
            // On peut activer l'export dès le début si le réseau manuel est valide, 
            exportBtn.setDisable(true); 

            exportBtn.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Sauvegarder le réseau");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier Texte", "*.txt"));
                File dest = fileChooser.showSaveDialog(root.getScene().getWindow());

                if (dest != null) {
                    try {
                        fileOperation.WriterFile.saveReseau(rxe, dest.getAbsolutePath());
                    } catch (IOException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            });

            Button optimizeBtn = new Button("Optimiser");
            optimizeBtn.setOnAction(e -> {
                try {
                    System.out.println("Optimisation en cours...");

                    int severite = 10; // Valeur par défaut
                    if (!severityInput.getText().isEmpty()) {
                        try {
                            severite = Integer.parseInt(severityInput.getText());
                        } catch (NumberFormatException nfe) {
                            severite = 10; 
                        }
                    }
                    
                    // Appel à l'optimiseur
                    rxe = Optimiseur.resolutionAutomatique(rxe, severite).getRxe();

                    dessinerReseau(); // Redessiner sans paramètre Fichier

                    exportBtn.setDisable(false);
                } catch (Exception ex) {
                    System.err.println("Erreur optimisation : " + ex.getMessage());
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur Optimisation");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                }
            });

            Button rtrnMenu = new Button("Retour au Menu");
            // On suppose que MainApp a une méthode showMenu(), sinon mettre showImportView()
            rtrnMenu.setOnAction(e -> app.showMenu()); 
            
            buttonBar.getChildren().addAll(rtrnMenu, optimizeBtn, exportBtn);
         
            bottomContainer.getChildren().addAll(infoBar, buttonBar); 

            dessinerReseau(); // Appel sans fichier

            root.setCenter(scrollPane);
            root.setBottom(bottomContainer); 

        } catch(Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur d'affichage");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    // Méthode modifiée : plus de paramètre File f
    private void dessinerReseau() {
        graphPane.getChildren().clear();

        Map<Maison, VBox> nodeMaison = new HashMap<>();
        Map<Generateur, VBox> nodeGen = new HashMap<>();

        int indexMaison = 0;
        int indexGen = 0;

        // Dessin des Maisons
        for (Maison m : rxe.getMaisons()) {
            VBox box = createNode("/assets/maison.png", m.getNomM() + "\nConso " + m.getConso().getConso() + "Kwh");
            box.setLayoutX(MARGE_X + (indexMaison * ESPACE_X));
            box.setLayoutY(Y_MAISON);
            nodeMaison.put(m, box);
            graphPane.getChildren().add(box);
            indexMaison++;
        }

        // Dessin des Générateurs
        for (Generateur g : rxe.getGens()) {
            VBox box = createNode("/assets/generateur-electrique.png",
                    g.getNomG() + "\nCapa " + g.getCapaciteMax() + "Kwh\n Charge " + g.getChargeActu() + "Kwh");
            box.setLayoutX(MARGE_X + (indexGen * ESPACE_X));
            box.setLayoutY(Y_GEN);
            nodeGen.put(g, box);
            graphPane.getChildren().add(box);
            indexGen++;
        }

        // Dessin des Connexions
        for (Connexion c : rxe.getConnexions()) {
            VBox maisonNode = nodeMaison.get(c.getMs());
            VBox genNode = nodeGen.get(c.getGen());

            if (maisonNode != null && genNode != null) {
                Line link = createLink(maisonNode, genNode);
                graphPane.getChildren().add(0, link);
            }
        }

        // Mise à jour du Label d'information
        updateStatusLabel();

        // Ajustement taille ScrollPane
        int maxItems = Math.max(indexMaison, indexGen);
        double width = Math.max(800, (maxItems * ESPACE_X) + (MARGE_X * 2));
        graphPane.setPrefWidth(width);
        graphPane.setPrefHeight(600);
    }

    private void updateStatusLabel() {
        StringBuffer texte = new StringBuffer("Source : Création Manuelle\n");
        texte.append("Coût du réseau : " + new CoutRxElct(rxe).calculeCoutRxE());

        statusLabel.setText(texte.toString());
    }

    private VBox createNode(String imagePath, String labelName) {
        ImageView img = new ImageView();
        try {
            img.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        } catch (Exception e) {
            System.err.println("Img not found: " + imagePath);
        }

        img.setFitWidth(60);
        img.setFitHeight(60);
        Label label = new Label(labelName);
        label.setStyle("-fx-font-weight: bold; -fx-text-alignment: center;"); 
        VBox box = new VBox(5, img, label);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(100); 
        return box;
    }

    private Line createLink(VBox maison, VBox generateur) {
        Line line = new Line();
        line.startXProperty().bind(maison.layoutXProperty().add(maison.widthProperty().divide(2)));
        line.startYProperty().bind(maison.layoutYProperty());
        line.endXProperty().bind(generateur.layoutXProperty().add(generateur.widthProperty().divide(2)));
        line.endYProperty().bind(generateur.layoutYProperty().add(generateur.heightProperty()));
        line.setStrokeWidth(3);
        line.setStyle("-fx-stroke: #555;"); 
        return line;
    }

    public BorderPane getView() {
        return root;
    }
}