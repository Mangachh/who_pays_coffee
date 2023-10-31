package cbs.wantACoffe.exceptions;

/**
 * Excepción que debe saltar si el password del usuario es incorrecto
 *  
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public class IncorrectPasswordException extends Exception{

    private final static String MESSAGE = "The password is incorrect";

    public IncorrectPasswordException(){
        super(MESSAGE);
    }

    public IncorrectPasswordException(final String message){
        super(message);
    }
    
}
