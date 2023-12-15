package cbs.wantACoffe.exceptions;

public class GroupNotExistsException extends Exception {
    private final static String MESSAGE = "The group not exists";

    public GroupNotExistsException() {
        super(MESSAGE);
    }
    
}
