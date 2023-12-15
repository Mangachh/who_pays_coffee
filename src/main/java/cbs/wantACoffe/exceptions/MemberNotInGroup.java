package cbs.wantACoffe.exceptions;

public class MemberNotInGroup extends Exception{
    private final static String MESSAGE = "The Member is not in the group";

    public MemberNotInGroup() {
        super(MESSAGE);
    }
}
