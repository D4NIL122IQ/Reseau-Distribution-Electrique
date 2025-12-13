package exceptions;

/*
 * Exception pour declarer les parametres d'une connexion (m,g) n'existe pas 
 * 
 * @author Danil Guidjou
 */
public class MGException extends Exception{
	public MGException(String m) {
		super(m);
	}
}
