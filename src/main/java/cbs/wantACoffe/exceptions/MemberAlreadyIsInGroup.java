package cbs.wantACoffe.exceptions;

public class MemberAlreadyIsInGroup extends Exception {
    
    private final static String MESSAGE = "Member already exist in group";

    public MemberAlreadyIsInGroup(){
        super(MESSAGE);
    }

    public MemberAlreadyIsInGroup(final String message){
        super(message);
    }
}
