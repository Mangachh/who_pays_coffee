package cbs.wantACoffe.exceptions;

public class PaymentHasNoDateException extends Exception {

    private static final String MESSAGE = "Payment has no Date";

    public PaymentHasNoDateException() {
        super(MESSAGE);
    }
    
}
