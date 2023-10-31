package cbs.wantACoffe.exceptions;

/**
 * Excepción que debe saltar si los datos del usuario están vacíos
 *  
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public class NullValueInUserDataException extends Exception{

    private static final String MESSAGE = "The user has empty or null values";

    public NullValueInUserDataException(){
        super(MESSAGE);
    }
    public NullValueInUserDataException(final String message){
        super(message);
    }    
}
