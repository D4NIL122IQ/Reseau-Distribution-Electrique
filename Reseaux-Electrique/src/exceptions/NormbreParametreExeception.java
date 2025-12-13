package exceptions;

/*
 * Exception pour declarer que les nombre de parametres de {generateur || maison || connexion}
 * n'st pas correcte
 * 
 * @author Danil Guidjou
 */
public class NormbreParametreExeception extends Exception{
	public NormbreParametreExeception(String msg) {
		super(msg);
	}
}
