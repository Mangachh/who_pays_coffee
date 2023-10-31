package cbs.wantACoffe.exceptions;

/**
 * Excepción que debe saltar si el nombre de usuario o el correo existen
 *  
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public class UsernameEmailAlreadyExistsException extends Exception {
    
    private final static String MESSAGE = "Username Or Email already exists";

    public UsernameEmailAlreadyExistsException(){
        super(MESSAGE);
    }

    public UsernameEmailAlreadyExistsException(final String message){
        super(message);
    }
}
