package vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import model.*;

/*
 * Scene de création d'un reseau manuellement 
 * 
 * @author Danil Guidjou
 */
public class CreatReseau {

	private MainApp app;
	private VBox root;
	private Pane zoneDessin;
	private ReseauElectrique reseau;

	private Pane vueMaisonSelectionnee = null;
	private Pane vueGenerateurSelectionne = null;

	
	private Map<String, VBox> mapComposantsGraphiques = new HashMap<>();

	private Map<Maison, Line> mapConnexionsGraphiques = new HashMap<>();

	public CreatReseau(MainApp app) {
		this.app = app;
		this.reseau = new ReseauElectrique();

		root = new VBox(10);
		root.setPadding(new Insets(10));
		root.setAlignment(Pos.TOP_CENTER);

		// Zone de dessin
		zoneDessin = new Pane();
		zoneDessin.setStyle("-fx-background-color: white; -fx-border-color: gray;");
		VBox.setVgrow(zoneDessin, Priority.ALWAYS);

		// Barre de boutons
		HBox barreBoutons = new HBox(15);
		barreBoutons.setAlignment(Pos.CENTER);
		barreBoutons.setPadding(new Insets(10));
		barreBoutons.setStyle("-fx-background-color: #eee;");

		Button btnAddMaison = new Button("Ajouter Maison");
		Button btnAddGen = new Button("Ajouter Générateur");
		Button btnAddCon = new Button("Ajouter Connexion");
		Button btnDelCon = new Button("Supprimer Connexion");
		Button btnOpti = new Button("Optimiser le reseau");
		Button btnRtn = new Button("Retour au menu");
		btnRtn.setOnAction(e -> app.showMenu());
		btnAddMaison.setOnAction(e -> actionAjouterMaison());
		btnAddGen.setOnAction(e -> actionAjouterGenerateur());
		btnAddCon.setOnAction(e -> actionAjouterConnexion());
		btnDelCon.setOnAction(e -> actionSupprimerConnexion());
		btnOpti.setOnAction(e -> {

			if (reseau.validerReseau() == null) {
				app.showDisplayView(reseau);
			} else {
				afficherErreur("Reseau non valide", reseau.validerReseau());
			}

		});

		barreBoutons.getChildren().addAll(btnRtn, btnAddMaison, btnAddGen, btnAddCon, btnDelCon, btnOpti);
		root.getChildren().addAll(new Label("Création Réseau"), zoneDessin, barreBoutons);
	}
	
	// methode prive pour ajouter une maison ou maj
	private void actionAjouterMaison() {
		try {
			String nom = demanderTexte("Nouvelle Maison", "Nom :");
			if (nom == null || nom.trim().isEmpty())
				return;

			// Choix consommation
			ChoiceDialog<Consomation> dialog = new ChoiceDialog<>(Consomation.NORMAL, Consomation.values());
			dialog.setContentText("Consommation :");
			Optional<Consomation> result = dialog.showAndWait();
			if (!result.isPresent())
				return;

			// Création objet temporaire pour passage au modèle
			Maison mNouvelle = new Maison(nom, result.get());
			
			// ajouter la maison dans le reseau
			reseau.ajoutMaison(mNouvelle);


			if (mapComposantsGraphiques.containsKey(nom)) {
				VBox vueExistante = mapComposantsGraphiques.get(nom);
				
				Maison mReelle = (Maison) vueExistante.getUserData();

				// On met à jour l'affichage de la maison
				refreshVueMaison(mReelle);

				//actualiser l'affichage pour afficher la maj
				actualiserTousLesGenerateurs();

				afficherInfo("Mise à jour", "Maison '" + nom + "' mise à jour.");
			} else {
				createVueMaison(mNouvelle);
			}

		} catch (Exception e) {
			afficherErreur("Erreur", e.getMessage());
		}
	}

	// methode prive pour ajouter un generateur ou maj
	private void actionAjouterGenerateur() {
		try {
			String nom = demanderTexte("Nouveau Générateur", "Nom :");
			if (nom == null || nom.trim().isEmpty())
				return;

			String chargeStr = demanderTexte("Puissance", "Charge Max (entier) :");
			if (chargeStr == null)
				return;
			int charge = Integer.parseInt(chargeStr);

			Generateur gNouveau = new Generateur(nom, charge);

			reseau.ajoutGenerateur(gNouveau);

			if (mapComposantsGraphiques.containsKey(nom)) {
				VBox vueExistante = mapComposantsGraphiques.get(nom);
				Generateur gReel = (Generateur) vueExistante.getUserData();
				refreshVueGenerateur(gReel);

				afficherInfo("Mise à jour", "Générateur '" + nom + "' mis à jour.");
			} else {
				createVueGenerateur(gNouveau);
			}

		} catch (NumberFormatException e) {
			afficherErreur("Erreur", "La charge doit être un nombre.");
		} catch (Exception e) {
			afficherErreur("Erreur", e.getMessage());
		}
	}
	
	// methode prive pour ajouter une connexion
	private void actionAjouterConnexion() {
		try {
			if (vueMaisonSelectionnee == null || vueGenerateurSelectionne == null) {
				throw new Exception("Sélectionnez une Maison et un Générateur.");
			}

			Maison m = (Maison) vueMaisonSelectionnee.getUserData();
			Generateur g = (Generateur) vueGenerateurSelectionne.getUserData();

			if (mapConnexionsGraphiques.containsKey(m)) {
				throw new Exception("Cette maison est déjà connectée !");
			}

			reseau.ajoutConnexion(m, g);

			dessinerLigneDeConnexion(vueMaisonSelectionnee, vueGenerateurSelectionne, m);

			refreshVueGenerateur(g);

			resetSelection();

		} catch (Exception e) {
			afficherErreur("Impossible de connecter", e.getMessage());
		}
	}
	
	// methode prive pour supprimer une connexion
	private void actionSupprimerConnexion() {
		try {
			if (vueMaisonSelectionnee == null) {
				throw new Exception("Sélectionnez la maison.");
			}

			Maison m = (Maison) vueMaisonSelectionnee.getUserData();

			if (!mapConnexionsGraphiques.containsKey(m)) {
				throw new Exception("Aucune connexion sur cette maison.");
			}

			if (vueGenerateurSelectionne == null) {
				throw new Exception("Sélectionnez aussi le générateur pour confirmer.");
			}
			Generateur g = (Generateur) vueGenerateurSelectionne.getUserData();

			reseau.supprimerConnexion(m, g);

			Line ligne = mapConnexionsGraphiques.get(m);
			zoneDessin.getChildren().remove(ligne);
			mapConnexionsGraphiques.remove(m);

			refreshVueGenerateur(g);

			resetSelection();
			System.out.println("Connexion supprimée.");

		} catch (Exception e) {
			afficherErreur("Erreur suppression", e.getMessage());
		}
	}
	
	private void refreshVueGenerateur(Generateur g) {
		VBox v = mapComposantsGraphiques.get(g.getNomG());
		if (v != null) {
			String texte = g.getNomG() + "\nCharge Max " + g.getCapaciteMax() + "kwh\nCharge Actu " + g.getChargeActu()
					+ "kwh";
			mettreAJourTexteIcone(v, texte);
		}
	}

	private void refreshVueMaison(Maison m) {
		VBox v = mapComposantsGraphiques.get(m.getNomM());
		if (v != null) {
			String texte = m.getNomM() + "\nConso " + m.getConso().getConso() + "kwh";
			mettreAJourTexteIcone(v, texte);
		}
	}

	private void actualiserTousLesGenerateurs() {
		for (VBox v : mapComposantsGraphiques.values()) {
			if (v.getUserData() instanceof Generateur) {
				refreshVueGenerateur((Generateur) v.getUserData());
			}
		}
	}

	private void mettreAJourTexteIcone(VBox container, String nouveauTexte) {
		for (javafx.scene.Node n : container.getChildren()) {
			if (n instanceof Label) {
				((Label) n).setText(nouveauTexte);
				return;
			}
		}
	}

	private void createVueMaison(Maison m) {
		String txt = m.getNomM() + "\nConso " + m.getConso().getConso() + "kwh";
		VBox v = createIcone(txt, "/assets/maison.png"); // Chemin à adapter
		v.setUserData(m);

		v.setOnMouseClicked(e -> {
			if (vueMaisonSelectionnee != null)
				styleDesel(vueMaisonSelectionnee);
			vueMaisonSelectionnee = v;
			styleSel(v);
		});

		zoneDessin.getChildren().add(v);
		mapComposantsGraphiques.put(m.getNomM(), v);
	}

	private void createVueGenerateur(Generateur g) {
		String txt = g.getNomG() + "\nCharge Max " + g.getCapaciteMax() + "kwh\nCharge Actu " + g.getChargeActu()
				+ "kwh";
		VBox v = createIcone(txt, "/assets/generateur-electrique.png"); // Chemin à adapter
		v.setUserData(g);

		v.setOnMouseClicked(e -> {
			if (vueGenerateurSelectionne != null)
				styleDesel(vueGenerateurSelectionne);
			vueGenerateurSelectionne = v;
			styleSel(v);
		});

		zoneDessin.getChildren().add(v);
		mapComposantsGraphiques.put(g.getNomG(), v);
	}

	private VBox createIcone(String texte, String path) {
		ImageView iv = new ImageView();
		try {
			// Assurez-vous que le dossier 'assets' est dans src/main/resources/
			iv.setImage(new Image(getClass().getResourceAsStream(path)));
			iv.setFitWidth(50);
			iv.setFitHeight(50);
		} catch (Exception e) {
			// Fallback si image non trouvée
			System.err.println("Image non trouvée : " + path);
		}

		Label lbl = new Label(texte);
		lbl.setStyle("-fx-text-alignment: center; -fx-font-size: 10px;");

		VBox box = new VBox(2, iv, lbl);
		box.setAlignment(Pos.CENTER);
		box.setStyle("-fx-border-color: transparent; -fx-padding: 5;");

		// Position aléatoire
		box.setLayoutX(20 + Math.random() * 300);
		box.setLayoutY(20 + Math.random() * 200);

		makeDraggable(box);
		return box;
	}

	private void dessinerLigneDeConnexion(Pane start, Pane end, Maison m) {
		Line ligne = new Line();
		// Binding : La ligne suit le mouvement des icônes
		ligne.startXProperty().bind(start.layoutXProperty().add(start.widthProperty().divide(2)));
		ligne.startYProperty().bind(start.layoutYProperty().add(start.heightProperty().divide(2)));
		ligne.endXProperty().bind(end.layoutXProperty().add(end.widthProperty().divide(2)));
		ligne.endYProperty().bind(end.layoutYProperty().add(end.heightProperty().divide(2)));

		ligne.setStrokeWidth(3);
		zoneDessin.getChildren().add(0, ligne); // Ajout en arrière-plan

		mapConnexionsGraphiques.put(m, ligne);
	}


	private void styleSel(Pane p) {
		p.setStyle(
				"-fx-border-color: red; -fx-border-width: 2; -fx-background-color: rgba(255,0,0,0.1); -fx-border-radius: 5;");
	}

	private void styleDesel(Pane p) {
		p.setStyle("-fx-border-color: transparent; -fx-background-color: transparent;");
	}

	private void resetSelection() {
		if (vueMaisonSelectionnee != null)
			styleDesel(vueMaisonSelectionnee);
		if (vueGenerateurSelectionne != null)
			styleDesel(vueGenerateurSelectionne);
		vueMaisonSelectionnee = null;
		vueGenerateurSelectionne = null;
	}

	private void makeDraggable(Pane node) {
		final Delta dragDelta = new Delta();
		node.setOnMousePressed(me -> {
			dragDelta.x = node.getLayoutX() - me.getSceneX();
			dragDelta.y = node.getLayoutY() - me.getSceneY();
			node.setCursor(Cursor.MOVE);
		});
		node.setOnMouseDragged(me -> {
			node.setLayoutX(me.getSceneX() + dragDelta.x);
			node.setLayoutY(me.getSceneY() + dragDelta.y);
		});
		node.setOnMouseReleased(me -> node.setCursor(Cursor.HAND));
	}

	private class Delta {
		double x, y;
	}

	private String demanderTexte(String titre, String msg) {
		TextInputDialog d = new TextInputDialog();
		d.setTitle(titre);
		d.setHeaderText(null);
		d.setContentText(msg);
		return d.showAndWait().orElse(null);
	}

	private void afficherErreur(String t, String c) {
		Alert a = new Alert(Alert.AlertType.ERROR);
		a.setTitle(t);
		a.setHeaderText(null);
		a.setContentText(c);
		a.showAndWait();
	}

	private void afficherInfo(String t, String c) {
		Alert a = new Alert(Alert.AlertType.INFORMATION);
		a.setTitle(t);
		a.setHeaderText(null);
		a.setContentText(c);
		a.showAndWait();
	}

	public VBox getView() {
		return root;
	}
}