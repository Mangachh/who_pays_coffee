package cbs.wantACoffe.exceptions;

public class MemberAdminTypeUnknown extends Exception{
    
    private final static String MESSAGE = "The type is unknown";

    public MemberAdminTypeUnknown (){
        super(MESSAGE);
    }

    public MemberAdminTypeUnknown (final String message){
        super(message);
    }
}
