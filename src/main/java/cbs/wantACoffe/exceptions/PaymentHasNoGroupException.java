package cbs.wantACoffe.exceptions;

public class PaymentHasNoGroupException extends Exception {
    
    private static final String MESSAGE = "Payment has no Group attached";

    public PaymentHasNoGroupException() {
        super(MESSAGE);
    }
    
}
