package cbs.wantACoffe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cbs.wantACoffe.dto.token.Token;
import cbs.wantACoffe.dto.token.Token.TokenType;
import cbs.wantACoffe.entity.AdminUser;
import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.RegisteredUser;

public class CommonData {
    
    public static Random rand = new Random();

    public static RegisteredUser getTestUserWithSuffix(final String sufix) {
        return RegisteredUser.builder()
                .userId(100L)
                .username("Pepote" + sufix)
                .email("pepote@mail.se" + sufix)
                .password("1234")
                .build();
    }
    
    public static List<RegisteredUser> getRegUsersForGroupWithSuffix(final String suffix) {
        List<RegisteredUser> users = List.of(
                RegisteredUser.builder()
                        .username("Mariela" + suffix)
                        .email("mariela@mail.seo" + suffix)
                        .password("1234")
                        .build(),
                RegisteredUser.builder()
                        .username("Jaimito" + suffix)
                        .email("Jaimito@mail.se" + suffix)
                        .password("1234")
                        .build(),
                RegisteredUser.builder()
                        .username("Laura" + suffix)
                        .email("laura@mail.se" + suffix)
                        .password("1234")
                        .build());

        return new ArrayList<RegisteredUser>(users);
    }
    
    public static Group getTestGroup(final String suffix) {
        return Group.builder()
                .groupName("Grupo test 1" + suffix)
                .groupId(88L)
                .build();
    }
    
    public static AdminUser getTestAdmin() {
        return AdminUser.builder()
                .userId(100L)
                .username("Test Admin")
                .password("1234")
                .build();
    }
    
    
    public static Token getToken() {
        return new Token(TokenType.USER, "N2IwOTcyODUtZjNjMS0zMGM3LWI3NmYtNmQ0ZTdmZGI4ZDZk");
        
    }

    public static String getPrefix(){
        return "CBS";
    }

    
}
