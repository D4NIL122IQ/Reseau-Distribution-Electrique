package model;

import java.util.ArrayList;
import java.util.Scanner;

/*
 * Classe qui contient tout les methodes pour calculer le cout du reseau
 * 
 * @param rxe : une instance de ReseauElect
 * 
 * @author Danil Guidjou
 */
public class CoutRxElct {
	private ReseauElectrique rxe;
	private double lastDisp = 0;
	private double lastSurcharge = 0;

	// Par défaut 10, mais modifiable via ligne de commande
	private double severitePenalisation = 10.0;

	public CoutRxElct(ReseauElectrique rxe) {
		this.rxe = rxe;
	}

	public void setSeverite(double s) {
		this.severitePenalisation = s;
	}

	private double moyenneGen() {
		ArrayList<Generateur> gen = rxe.getGens();
		if (gen.isEmpty())
			return 0;
		double somme = 0;
		for (Generateur g : gen) {
			somme += (double) g.getChargeActu() / g.getCapaciteMax();
		}
		return (double) somme / gen.size();
	}

	private double disp() {
		ArrayList<Generateur> gen = rxe.getGens();
		double somme = 0;
		double Um = moyenneGen();
		for (Generateur g : gen) {
			somme += Math.abs((double) g.getChargeActu() / g.getCapaciteMax() - Um);
		}
		this.lastDisp = somme;
		return somme;
	}

	private double surcharge() {
		double somme = 0;
		ArrayList<Generateur> gen = rxe.getGens();
		for (Generateur g : gen) {
			if (g.getCapaciteMax() > 0) {
				somme += Math.max(0, ((double) g.getChargeActu() - g.getCapaciteMax()) / g.getCapaciteMax());
			} else if (g.getChargeActu() > 0) {
				somme += Double.POSITIVE_INFINITY;
			}
		}
		this.lastSurcharge = somme;
		return somme;
	}

	public double getDisp() {
		return lastDisp;
	}

	public double getSurcharge() {
		return lastSurcharge;
	}

	public ReseauElectrique getRxe() {
		return rxe;
	}

	public double calculeCoutRxE() {
		if (rxe.getGens().isEmpty())
			return 0;
		// Utilise la variable severitePenalisation configurée
		return disp() + severitePenalisation * surcharge();
	}

	/**
	 * MODIFIÉ POUR CORRESPONDRE AUX EXIGENCES Demande l'ancienne connexion (M1 G1)
	 * puis la nouvelle (M1 G2)
	 */
	/**
	 * Permet de modifier une connexion existante entre une maison et un générateur.
	 * L'utilisateur doit saisir une connexion existante (ex. : "M1 G1") puis une
	 * nouvelle connexion pour déplacer la maison vers un autre générateur.
	 *
	 * @param clavier scanner utilisé pour les saisies utilisateur
	 */
	public void modifierConnexion(Scanner clavier) {

		if (rxe.getConnexions().isEmpty()) {
			System.err.println("Aucune connexion à modifier.");
			return;
		}
		System.out.println("--- Modification d'une connexion ---");
		System.out.println(rxe.afficherConnexion()); // Montrer l'état actuel

		// 1. Saisir l'ancienne connexion
		System.out.print("Veuillez saisir la connexion que vous souhaitez modifier (ex: M1 G1) : ");
		String lineAncienne = clavier.nextLine().trim();
		String[] partsAncienne = lineAncienne.split(" ");
		if (partsAncienne.length != 2) {
			System.err.println("Format invalide.");
			return;
		}

		// 2. Trouver les objets
		Maison m = rxe.findMaisonByName(partsAncienne[0]);
		Generateur gAncien = rxe.findGenByName(partsAncienne[1]);
		if (m == null || gAncien == null) { // Essai inversé
			m = rxe.findMaisonByName(partsAncienne[1]);
			gAncien = rxe.findGenByName(partsAncienne[0]);
		}
		if (m == null) {
			System.err.println("Maison introuvable.");
			return;
		}
		if (gAncien == null) {
			System.err.println("Ancien generateur introuvable.");
			return;
		}

		// 3. Vérifier si la connexion existe
		Connexion connexionAModifier = null;
		for (Connexion c : rxe.getConnexions()) {
			if (c.getMs().equals(m) && c.getGen().equals(gAncien)) {
				connexionAModifier = c;
				break;
			}
		}
		if (connexionAModifier == null) {
			System.err.println("Erreur: La connexion '" + m.getNomM() + " -> " + gAncien.getNomG() + "' n'existe pas.");
			return;
		}

		// 4. Saisir la nouvelle connexion
		System.out.print("Veuillez saisir la nouvelle connexion (ex: M1 G2) : ");
		String lineNouvelle = clavier.nextLine().trim();
		String[] partsNouvelle = lineNouvelle.split(" ");
		if (partsNouvelle.length != 2) {
			System.err.println("Format invalide.");
			return;
		}

		// 5. Trouver les nouveaux objets
		Maison mNouveau = rxe.findMaisonByName(partsNouvelle[0]);
		Generateur gNouveau = rxe.findGenByName(partsNouvelle[1]);
		if (mNouveau == null || gNouveau == null) { // Essai inversé
			mNouveau = rxe.findMaisonByName(partsNouvelle[1]);
			gNouveau = rxe.findGenByName(partsNouvelle[0]);
		}

		// 6. Valider
		if (mNouveau == null) {
			System.err.println("Nouvelle maison introuvable.");
			return;
		}
		if (gNouveau == null) {
			System.err.println("Nouveau generateur introuvable.");
			return;
		}
		if (!mNouveau.equals(m)) {
			System.err.println("Erreur: Vous ne pouvez pas changer la maison (seulement le generateur).");
			return;
		}
		if (gAncien.equals(gNouveau)) {
			System.out.println("  -> La maison est déjà connectée à ce générateur.");
			return;
		}

		// 7. Exécuter la modification
		int conso = m.getConso().getConso();
		System.out.println("  ... Déconnexion de " + gAncien.getNomG() + " ...");
		gAncien.soustraireCharge(conso);

		System.out.println("  ... Connexion à " + gNouveau.getNomG() + " ...");
		gNouveau.setChargeActu(conso);

		// Mettre à jour l'objet Connexion
		connexionAModifier.setGen(gNouveau);

		System.out.println("  -> Modification terminée.");
	}

	public void afficherrxe() {
		System.out.println(rxe.afficherConnexion());
	}
}