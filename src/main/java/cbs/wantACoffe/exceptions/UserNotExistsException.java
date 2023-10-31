package cbs.wantACoffe.exceptions;

/**
 * Excepción que debe saltar si el usuario no existe
 *  
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public class UserNotExistsException extends Exception{
    private final static String MESSAGE = "The user does not exists in the database";

    public UserNotExistsException(){
        super(MESSAGE);
    }

    public UserNotExistsException(final String message){
        super(MESSAGE);
    }
    
}
