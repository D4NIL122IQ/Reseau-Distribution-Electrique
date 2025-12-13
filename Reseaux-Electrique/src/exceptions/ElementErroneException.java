package exceptions;


/*
 * Exception pour declarer que l'argument present dans le fichier
 * argument qui ne fait pas partie de la liste {generateur , maison, connexion}
 * 
 * @author Danil Guidjou
 */
public class ElementErroneException extends Exception{
	public ElementErroneException(String msg) {
		super(msg);
	}
}
