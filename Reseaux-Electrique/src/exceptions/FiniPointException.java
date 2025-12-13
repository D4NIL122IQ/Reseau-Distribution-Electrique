package exceptions;


/*
 * Exception pour declarer qu'une ligne dans un fichier ne fini pas avec un point 
 * 
 * @author Danil Guidjou
 */
public class FiniPointException extends Exception{
	public FiniPointException(String m) {
		super(m);
	}
}
