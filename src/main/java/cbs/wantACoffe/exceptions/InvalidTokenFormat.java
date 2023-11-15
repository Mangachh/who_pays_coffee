package cbs.wantACoffe.exceptions;

public class InvalidTokenFormat extends Exception{
    private final static String MESSAGE = "The token format is incorrect";

    public InvalidTokenFormat() {
        super(MESSAGE);
    }
}
