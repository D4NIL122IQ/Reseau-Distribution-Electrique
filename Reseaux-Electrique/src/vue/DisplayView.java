package vue;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
import fileOperation.ParserFile;

public class DisplayView {

    private BorderPane root = new BorderPane();
    private Pane graphPane = new Pane(); 
    // NOUVEAU : Le label d'information global accessible partout dans la classe
    private Label statusLabel = new Label(); 
    
    private MainApp app;
    private ReseauElectrique rxe;

    // Configuration de l'espacement
    private final double ESPACE_X = 120; 
    private final double MARGE_X = 50;
    private final double Y_GEN = 50;     
    private final double Y_MAISON = 400; 

    public DisplayView(MainApp app, File f) {
        this.app = app;

        try {
            rxe = ParserFile.parser(f.getAbsolutePath());
            
            // --- Center : ScrollPane ---
            ScrollPane scrollPane = new ScrollPane(graphPane);
            scrollPane.setPannable(true);
            scrollPane.setFitToHeight(true);

            // --- Bottom : Conteneur global (Label + Boutons) ---
            VBox bottomContainer = new VBox(10); // Espacement vertical de 10
            bottomContainer.setAlignment(Pos.CENTER);
            bottomContainer.setPadding(new javafx.geometry.Insets(15));
            bottomContainer.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-width: 1 0 0 0;");

            // 1. Configuration du Label d'info
            statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333; -fx-font-weight: bold;");
            
            // 2. Configuration des Boutons
            HBox buttonBar = new HBox(15); 
            buttonBar.setAlignment(Pos.CENTER);
            
            Button exportBtn = new Button("Exporter");
            exportBtn.setDisable(true); 

            exportBtn.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Sauvegarder le réseau optimisé");
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
                    rxe = Optimiseur.resolutionAutomatique(rxe, 10).getRxe();
                    
                    dessinerReseau(f); // Met à jour le graph ET le label
                    
                    exportBtn.setDisable(false);
                } catch (Exception ex) {
                    System.err.println("Erreur optimisation : " + ex.getMessage());
                }
            });
            
            Button rtrnLobby = new Button("Choisir un autre fichier");
            rtrnLobby.setOnAction(e  -> app.showImportView());            // Assemblage du bas
            buttonBar.getChildren().addAll(rtrnLobby,optimizeBtn, exportBtn);
            bottomContainer.getChildren().addAll(statusLabel, buttonBar); // On empile Label puis Boutons

            // Premier dessin
            dessinerReseau(f); 

            // Assemblage racine
            root.setCenter(scrollPane);
            root.setBottom(bottomContainer); // On met le conteneur VBox en bas

        } catch (Exception e) {
            System.out.println("Erreur DisplayView : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void dessinerReseau(File f) {
        graphPane.getChildren().clear();

        Map<Maison, VBox> nodeMaison = new HashMap<>();
        Map<Generateur, VBox> nodeGen = new HashMap<>();

        int indexMaison = 0;
        int indexGen = 0;

        // --- Dessin des Maisons ---
        for (Maison m : rxe.getMaisons()) {
            VBox box = createNode("/assets/maison.png", m.getNomM() + "\nConso "+ m.getConso().getConso() + "Kwh");
            box.setLayoutX(MARGE_X + (indexMaison * ESPACE_X));
            box.setLayoutY(Y_MAISON);
            nodeMaison.put(m, box);
            graphPane.getChildren().add(box);
            indexMaison++;
        }

        // --- Dessin des Générateurs ---
        for (Generateur g : rxe.getGens()) {
            VBox box = createNode("/assets/generateur-electrique.png", g.getNomG() + "\nCapa "+ g.getCapaciteMax() + "\n Charge " + g.getChargeActu());
            box.setLayoutX(MARGE_X + (indexGen * ESPACE_X));
            box.setLayoutY(Y_GEN);
            nodeGen.put(g, box);
            graphPane.getChildren().add(box);
            indexGen++;
        }

        // --- Dessin des Connexions ---
        for (Connexion c : rxe.getConnexions()) {
            VBox maisonNode = nodeMaison.get(c.getMs());
            VBox genNode = nodeGen.get(c.getGen());

            if (maisonNode != null && genNode != null) {
                Line link = createLink(maisonNode, genNode);
                graphPane.getChildren().add(0, link); 
            }
        }

        // --- NOUVEAU : Mise à jour du Label d'information ---
        // On met à jour le texte ici car c'est appelé au démarrage et après l'optimisation
        updateStatusLabel(f);

        // Ajustement taille ScrollPane
        int maxItems = Math.max(indexMaison, indexGen);
        graphPane.setPrefWidth((maxItems * ESPACE_X) + (MARGE_X * 2));
        graphPane.setPrefHeight(600);
    }
    
    // Petite méthode utilitaire pour formater le texte
    private void updateStatusLabel(File f) {
    	StringBuffer texte = new StringBuffer("Fichier : " + f.getName() + "\n");
        texte.append("Cout du reseau : "+ new CoutRxElct(rxe).calculeCoutRxE());
                                     
        statusLabel.setText(texte.toString());
    }

    private VBox createNode(String imagePath, String labelName) {
        ImageView img = new ImageView();
        try {
            img.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        } catch (Exception e) { System.err.println("Img not found: " + imagePath); }
        
        img.setFitWidth(60);
        img.setFitHeight(60);
        Label label = new Label(labelName);
        label.setStyle("-fx-font-weight: bold;"); 
        VBox box = new VBox(5, img, label);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(80); 
        return box;
    }

    private Line createLink(VBox maison, VBox generateur) {
        Line line = new Line();
        line.startXProperty().bind(maison.layoutXProperty().add(maison.widthProperty().divide(2)));
        line.startYProperty().bind(maison.layoutYProperty()); 
        line.endXProperty().bind(generateur.layoutXProperty().add(generateur.widthProperty().divide(2)));
        line.endYProperty().bind(generateur.layoutYProperty().add(generateur.heightProperty())); 
        line.setStrokeWidth(3);
        line.setStyle("-fx-stroke: #555;"); // Couleur du câble un peu plus visible
        return line;
    }

    public BorderPane getView() {
        return root;
    }
}