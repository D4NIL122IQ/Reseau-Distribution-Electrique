package fileOperation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import model.Connexion;
import model.Generateur;
import model.Maison;
import model.ReseauElectrique;

/*
 * writer d'une instance de reseau electrique
 * 
 * © Danil Guidjou
 */
public class WriterFile {

	/*
	 * Methode static qui permet de d'enregistrer l'instance du reseau electrique
	 * dans un fichier 'filename'
	 * 
	 * @param rxe : instance de ReseauElectrique
	 * 
	 * @param filename : chemin absolu/relatif du fichier dans le quelle on veut
	 * ecrire
	 * 
	 * @Throws IOException : erreur de nom de fichier
	 * 
	 * @author Danil Guidjou
	 */
	public static void saveReseau(ReseauElectrique rxe, String filename) throws IOException {
		try (PrintWriter printW = new PrintWriter(new BufferedWriter(new FileWriter(filename)))) {
			printW.print(saveGen(rxe.getGens()));
			printW.print(saveMai(rxe.getMaisons()));
			printW.print(saveCo(rxe.getConnexions()));
		}
		// On ne catch pas l'exception ici, on la laisse remonter
	}

	/*
	 * Methode static priver utiliser par saveReseau pour ecrire une instance de
	 * generateur
	 * 
	 * @param gen : Liste de generateur
	 * 
	 * @author Danil Guidjou
	 */
	private static String saveGen(ArrayList<Generateur> gen) {
		StringBuilder temp = new StringBuilder();
		for (Generateur g : gen) {
			temp.append("generateur(").append(g.getNomG()).append(",").append(g.getCapaciteMax()).append(").\n");
		}
		// Pas de deleteCharAt risqué. Si la liste est vide, on retourne une chaine
		// vide.
		return temp.toString();
	}

	/*
	 * Methode static priver utiliser par saveReseau pour ecrire une instance de
	 * maison
	 * 
	 * @param mai : Liste de maison
	 * 
	 * © Danil Guidjou
	 */
	private static String saveMai(ArrayList<Maison> mai) {
		StringBuilder temp = new StringBuilder();
		for (Maison m : mai) {
			temp.append("maison(").append(m.getNomM()).append(",").append(m.getConso()).append(").\n");
		}
		return temp.toString();
	}

	/*
	 * Methode static priver utiliser par saveReseau pour ecrire une instance de
	 * connexion
	 * 
	 * @param co : Liste de connexion
	 * 
	 * © Danil Guidjou
	 */
	private static String saveCo(ArrayList<Connexion> co) {
		StringBuilder temp = new StringBuilder();
		for (Connexion c : co) {
			temp.append("connexion(").append(c.getGen().getNomG()).append(",").append(c.getMs().getNomM())
					.append(").\n");
		}
		return temp.toString();
	}
}