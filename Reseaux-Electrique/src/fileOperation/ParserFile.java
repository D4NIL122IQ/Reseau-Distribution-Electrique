package fileOperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import exceptions.*; // Vos exceptions personnalisées
import model.Connexion;
import model.Consomation;
import model.Generateur;
import model.Maison;
import model.ReseauElectrique;

/*
 * Parser de fichier 
 * 
 * @author Danil Guidjou
 */
public class ParserFile {

	/*
	 * Methode static pour parser un fichier .txt en une instance de
	 * ReseauElectrique
	 * 
	 * @param f : String chemin absolu/relatif du fichier text
	 * 
	 * @author Danil Guidjou
	 */
	public static ReseauElectrique parser(String f) throws Exception {
		ReseauElectrique rxe = new ReseauElectrique(); // On instancie le réseau vide au début

		int numLigne = 0; // COMPTEUR DE LIGNE (Demandé par le PDF)
		int etape = 0; // 0=Gen, 1=Maison, 2=Connexion

		try (BufferedReader bReader = new BufferedReader(new FileReader(f))) {
			String ligne;

			while ((ligne = bReader.readLine()) != null) {
				numLigne++;
				ligne = ligne.trim();

				if (ligne.isEmpty())
					continue; // Ignorer les lignes vides

				if (!ligne.endsWith(".")) {
					throw new FiniPointException("Erreur ligne " + numLigne + " : La ligne doit finir par un point.");
				}

				// On retire le point final pour le traitement
				String contenu = ligne.substring(0, ligne.length() - 1);

				if (contenu.startsWith("generateur")) {
					if (etape > 0)
						throw new OrdreInstanceException(
								"Erreur ligne " + numLigne + " : Les generateurs doivent être definis en premier.");
					rxe.ajoutGenerateur(parserGen(contenu, numLigne));

				} else if (contenu.startsWith("maison")) {
					if (etape == 0)
						etape = 1;
					if (etape > 1)
						throw new OrdreInstanceException("Erreur ligne " + numLigne
								+ " : Les maisons doivent être definies avant les connexions.");
					rxe.ajoutMaison(parserMaison(contenu, numLigne));

				} else if (contenu.startsWith("connexion")) {
					if (etape < 1) {
						throw new OrdreInstanceException("Erreur ordre : définissez les maisons avant les connexions.");
					}
					etape = 2;
					// On passe 'rxe' pour vérifier que les objets existent déjà
					rxe.ajoutConnexion(parserCo(contenu, rxe, numLigne));

				} else {
					throw new ElementErroneException(
							"Erreur ligne " + numLigne + " : Element inconnu '" + contenu + "'");
				}
			}
		}

		return rxe;
	}

	private static Generateur parserGen(String ligne, int n) throws NormbreParametreExeception, NumberFormatException {
		/*
		 * Methode static priver utiliser par le parser une instance de generateur
		 * 
		 * @param ligne : la ligne du fichier qui a pour prefixe 'generateur'
		 * 
		 * @param n : la position de la ligne lu
		 * 
		 * @Throws NormbreParametreExeception : quand le nombre de parametre != 2
		 * 
		 * @Throws NumberFormatException : quand la charge du generateur n'est pas un
		 * int
		 * 
		 * @author Danil Guidjou
		 */
		try {
			String params = ligne.substring(ligne.indexOf("(") + 1, ligne.indexOf(")"));
			String[] data = params.split(",");

			if (data.length != 2)
				throw new NormbreParametreExeception("Ligne " + n + ": 2 paramètres attendus pour generateur.");

			return new Generateur(data[0].trim(), Integer.parseInt(data[1].trim()));
		} catch (IndexOutOfBoundsException e) {
			throw new NormbreParametreExeception("Ligne " + n + ": Format generateur incorrect.");
		}
	}

	private static Maison parserMaison(String ligne, int n)
			throws ArgumentNonDefiniException, NormbreParametreExeception {
		/*
		 * Methode static priver utiliser par le parser une instance de maison
		 * 
		 * @param ligne : la ligne du fichier qui a pour prefixe 'maison'
		 * 
		 * @param n : la position de la ligne lu
		 * 
		 * @Throws ArgumentNonDefiniException : quand la consommation de la maison ne
		 * fait pas partie de notre enumeration
		 * 
		 * @Throws NormbreParametreExeception : quand le nombre de parametre != 2
		 * 
		 * @author Danil Guidjou
		 */
		try {
			String params = ligne.substring(ligne.indexOf("(") + 1, ligne.indexOf(")"));
			String[] data = params.split(",");

			if (data.length != 2)
				throw new NormbreParametreExeception("Ligne " + n + ": 2 paramètres attendus pour maison.");

			String nom = data[0].trim();
			String consoStr = data[1].trim();

			Consomation c;
			try {
				c = Consomation.valueOf(consoStr);
			} catch (IllegalArgumentException e) {
				throw new ArgumentNonDefiniException(
						"Ligne " + n + ": Consommation '" + consoStr + "' inconnue (attendue: BASSE, NORMAL, FORTE).");
			}
			return new Maison(nom, c);

		} catch (IndexOutOfBoundsException e) {
			throw new NormbreParametreExeception("Ligne " + n + ": Format maison incorrect.");
		}
	}

	private static Connexion parserCo(String ligne, ReseauElectrique rxe, int n)
			throws MGException, NormbreParametreExeception {
		/*
		 * Methode static priver utiliser par le parser une instance de connexion
		 * 
		 * @param ligne : la ligne du fichier qui a pour prefixe 'connexion'
		 * 
		 * @param rxe : l'instance du reseau actuelle
		 * 
		 * @param n : la position de la ligne lu
		 * 
		 * @Throws MGException : quand un parametre de connexion n'existe pas soit
		 * maison ou generateur
		 * 
		 * @Throws NormbreParametreExeception : quand le nombre de parametre != 2
		 * 
		 * @author Danil Guidjou
		 */
		try {
			String params = ligne.substring(ligne.indexOf("(") + 1, ligne.indexOf(")"));
			String[] data = params.split(",");

			if (data.length != 2)
				throw new NormbreParametreExeception("Ligne " + n + ": 2 paramètres attendus pour connexion.");

			String arg1 = data[0].trim();
			String arg2 = data[1].trim();

			Maison m = rxe.findMaisonByName(arg1);
			Generateur g = rxe.findGenByName(arg2);

			// Gestion de l'inversion (Maison, Gen) ou (Gen, Maison)
			if (m == null || g == null) {
				m = rxe.findMaisonByName(arg2);
				g = rxe.findGenByName(arg1);
			}

			if (m == null || g == null) {
				throw new MGException(
						"Ligne " + n + ": Impossible de creer une connexion, Maison ou Generateur inconnu (" + arg1
								+ ", " + arg2 + ")");
			}

			return new Connexion(m, g);
		} catch (IndexOutOfBoundsException e) {
			throw new NormbreParametreExeception("Ligne " + n + ": Format connexion incorrect.");
		}
	}
}