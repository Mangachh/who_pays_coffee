package cbs.wantACoffe.exceptions;


public class PaymentHasNoAmountException extends Exception {

    private final static String MESSAGE = "The payment has no amount";

    public PaymentHasNoAmountException() {
        super(MESSAGE);
    }
    
}
