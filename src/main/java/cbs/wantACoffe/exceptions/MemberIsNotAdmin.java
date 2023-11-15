package cbs.wantACoffe.exceptions;

public class MemberIsNotAdmin extends Exception {
    private final static String MESSAGE = "Member is not admin";

    public MemberIsNotAdmin() {
        super(MESSAGE);
    }
    
}
