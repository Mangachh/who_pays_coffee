package cbs.wantACoffe.exceptions;

public class GroupHasNoNameException extends Exception{
    private static final String MESSAGE = "The group has no name";

    public GroupHasNoNameException() {
        super(MESSAGE);
    }
}
