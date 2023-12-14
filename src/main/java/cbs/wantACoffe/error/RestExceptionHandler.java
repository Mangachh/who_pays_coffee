package cbs.wantACoffe.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import cbs.wantACoffe.exceptions.GroupHasNoNameException;
import cbs.wantACoffe.exceptions.GroupNotExistsException;
import cbs.wantACoffe.exceptions.IncorrectPasswordException;
import cbs.wantACoffe.exceptions.InvalidTokenFormat;
import cbs.wantACoffe.exceptions.MemberAdminTypeUnknown;
import cbs.wantACoffe.exceptions.MemberAlreadyIsInGroup;
import cbs.wantACoffe.exceptions.MemberHasNoNicknameException;
import cbs.wantACoffe.exceptions.MemberIsNotAdmin;
import cbs.wantACoffe.exceptions.MemberNotInGroup;
import cbs.wantACoffe.exceptions.NullValueInUserDataException;
import cbs.wantACoffe.exceptions.PaymentHasNoAmountException;
import cbs.wantACoffe.exceptions.PaymentHasNoDateException;
import cbs.wantACoffe.exceptions.PaymentHasNoGroupException;
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
        this.log.error("Exception raised: {}", message);
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

    @ExceptionHandler(MemberNotInGroup.class)
    private ResponseEntity<ErrorMessage> memberNotInGroup(MemberNotInGroup e, WebRequest request) {
        return this.createResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MemberIsNotAdmin.class)
    private ResponseEntity<ErrorMessage> memberIsNotAdmin(MemberIsNotAdmin e, WebRequest request) {
        return this.createResponseEntity(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(MemberHasNoNicknameException.class)
    private ResponseEntity<ErrorMessage> memberHasNoNickname(MemberHasNoNicknameException e, WebRequest request) {
        return this.createResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }

     @ExceptionHandler(GroupHasNoNameException.class)
     private ResponseEntity<ErrorMessage> groupHasNoNameException(GroupHasNoNameException e, WebRequest request) {
         return this.createResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
     }
    
     @ExceptionHandler(GroupNotExistsException.class)
     private ResponseEntity<ErrorMessage> groupNotExists(GroupNotExistsException e, WebRequest request) {
         return this.createResponseEntity(HttpStatus.NOT_FOUND, e.getMessage());
     }

    
     /**********************************/
    /*           PAYMENT             */
    /********************************/

    @ExceptionHandler(PaymentHasNoAmountException.class)
    private ResponseEntity<ErrorMessage> paymentHasNoAmountException(PaymentHasNoAmountException e,
            WebRequest request) {
        return this.createResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }
     
    @ExceptionHandler(PaymentHasNoDateException.class)
    private ResponseEntity<ErrorMessage> paymentHasNoDateException(PaymentHasNoDateException e, WebRequest request) {
        return this.createResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(PaymentHasNoGroupException.class)
    private ResponseEntity<ErrorMessage> paymentHasNoGroupException(PaymentHasNoGroupException e, WebRequest request) {
        return this.createResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    
   
    
    /**********************************/
    /*           AUTH                */
    /********************************/

    @ExceptionHandler(InvalidTokenFormat.class)
    private ResponseEntity<ErrorMessage> invalidTokenFormat(InvalidTokenFormat e, WebRequest request) {
        return this.createResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
