package vue;

import model.Consomation;
import model.CoutRxElct;
import model.Generateur;
import model.Maison;
import model.ReseauElectrique;

import fileOperation.*;
import model.Optimiseur;
public class test {
	public static void main(String args[]) {
		//Maison m1 = new Maison("M1", Consomation.FORTE);
		//Maison m2 = new Maison("M2", Consomation.FORTE);
		//Maison m3 = new Maison("M3", Consomation.NORMAL);
		//Maison m4 = new Maison("M4", Consomation.BASSE);
		
		//Generateur g1 = new Generateur("G1", 60);
		//Generateur g2 = new Generateur("G2", 60);
		
		ReseauElectrique rxe = new ReseauElectrique();
		//rxe.ajoutMaison(m4);
		//rxe.ajoutMaison(m3);
		//rxe.ajoutMaison(m2);
		//rxe.ajoutMaison(m1);
		//rxe.ajoutGenerateur(g2);
		//rxe.ajoutGenerateur(g1);
		//rxe.ajoutConnexion(m1, g1);
		//rxe.ajoutConnexion(m2, g2);
		//rxe.ajoutConnexion(m4, g2);
		//rxe.ajoutConnexion(m3, g1);
		
		try {
			rxe = ParserFile.parser("./src/fichierTest/instance2.txt");
			CoutRxElct c = new CoutRxElct(rxe);
			double cout = c.calculeCoutRxE();
			System.out.printf("  -> Le cout total du reseau est : %.3f\n", cout);
			Optimiseur.resolutionAutomatique(rxe, 10);
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		
		
		
		
		//CoutRxElct c = new CoutRxElct(rxe);
		
        //double cout = c.calculeCoutRxE();
        // Affichage des détails comme demandé
        //System.out.printf("  -> Disp(S) = %.2f\n", c.getDisp());
        //System.out.printf("  -> Surcharge(S) = %.2f\n", c.getSurcharge());
        //System.out.printf("  -> Le cout total du reseau est : %.3f\n", cout);
		
	}
}
