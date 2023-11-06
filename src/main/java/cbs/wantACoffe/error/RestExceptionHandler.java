package cbs.wantACoffe.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import cbs.wantACoffe.exceptions.IncorrectPasswordException;
import cbs.wantACoffe.exceptions.MemberAdminTypeUnknown;
import cbs.wantACoffe.exceptions.MemberAlreadyIsInGroup;
import cbs.wantACoffe.exceptions.NullValueInUserDataException;
import cbs.wantACoffe.exceptions.UserNotExistsException;
import cbs.wantACoffe.exceptions.UsernameEmailAlreadyExistsException;

/**
 * Handler para todos las excepciones que saltan en los endpoints de los
 * controladores.
 * <p>
 * Idealmente deberíamos tener una clase de estas por cada controlador.
 *  
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
@ControllerAdvice
@ResponseStatus
public class RestExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);
    
    /**********************************/
    /*      USER EXCEPTIONS          */
    /********************************/

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ErrorMessage> incorrectPasswordException(IncorrectPasswordException e, WebRequest request){
        return this.createResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NullValueInUserDataException.class)
    public ResponseEntity<ErrorMessage> nullValueInUserDataException(NullValueInUserDataException e, WebRequest request){
        return this.createResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(UsernameEmailAlreadyExistsException.class)
    public ResponseEntity<ErrorMessage> usernameEmailAlreadyExistsException(UsernameEmailAlreadyExistsException e, WebRequest request){
        return this.createResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(UserNotExistsException.class)
    public ResponseEntity<ErrorMessage> userNotExistsException(UserNotExistsException e, WebRequest request){
        return this.createResponseEntity(HttpStatus.NOT_FOUND, e.getMessage());
    }

    private ResponseEntity<ErrorMessage> createResponseEntity(final HttpStatus status, final String message) {
        ErrorMessage error = new ErrorMessage(status, message);
        this.log.error("Execption raised: {}", message);
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    /**********************************/
    /*      GROUP EXCEPTIONS         */
    /********************************/

    @ExceptionHandler(MemberAlreadyIsInGroup.class)
    private ResponseEntity<ErrorMessage> memberIsInGroup(MemberAlreadyIsInGroup e, WebRequest request) {
        return this.createResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MemberAdminTypeUnknown.class)
    private ResponseEntity<ErrorMessage> memberAdminTypeUnknow(MemberAdminTypeUnknown e, WebRequest reuqest) {
        return this.createResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
