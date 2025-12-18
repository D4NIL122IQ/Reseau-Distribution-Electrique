package exceptions;
/**
 * Exception levée lorsqu'une maison est déjà connectée à un générateur
 * et qu'une nouvelle connexion est tentée.
 * 
 * Cette exception permet de garantir la contrainte :
 * une maison ne peut être reliée qu'à un seul générateur.
 */

public class MaisonDejaConnecteeException extends Exception {
    public MaisonDejaConnecteeException(String message) {
        super(message);
    }
}
