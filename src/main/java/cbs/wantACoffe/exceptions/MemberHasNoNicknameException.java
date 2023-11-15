package cbs.wantACoffe.exceptions;

public class MemberHasNoNicknameException extends Exception {
    public final static String MESSAGE = "The member has no nickname";

    public MemberHasNoNicknameException() {
        super(MESSAGE);
    }
    
}
