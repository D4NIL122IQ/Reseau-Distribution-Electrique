package exceptions;

/*
 * Exception pour declarer que l'ordre de declaration dans le fichier n'est pas bien defini
 * {generateur -> maison -> connexion}
 * 
 * @author Danil Guidjou
 */
public class OrdreInstanceException extends Exception{
	public OrdreInstanceException(String msg) {
		super(msg);
	}
}
