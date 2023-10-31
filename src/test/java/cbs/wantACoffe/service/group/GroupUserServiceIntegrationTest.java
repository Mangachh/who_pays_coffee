package cbs.wantACoffe.service.group;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import cbs.wantACoffe.CommonData;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.NullValueInUserDataException;
import cbs.wantACoffe.exceptions.UsernameEmailAlreadyExistsException;
import cbs.wantACoffe.repository.IRegisteredMemberRepo;

@ActiveProfiles("h2_test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestMethodOrder(OrderAnnotation.class)
public class GroupUserServiceIntegrationTest {

    @Autowired
    private IMemberService groupUserService;


    @Autowired
    private IRegisteredMemberRepo userRepo;

    private static List<RegisteredUser> users;

    @BeforeAll
    static void populateUsers() {
        users = CommonData.getRegUsersForGroupWithSuffix("_GroupUserServiceIntegrationTest");
    }

    @BeforeEach
    void saveUsers() {
        try{
            for (RegisteredUser u : users) {
                this.userRepo.save(u);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
    }

    @Test
    @Order(1)
    void testSaveGroupMember() throws NullValueInUserDataException, UsernameEmailAlreadyExistsException {
        
        Member groupUser = Member.builder()
                .nickname("First User")
                .regUser(users.get(0))
                .build();

        System.out.println("\n\n-------------------- First Test ------------");
        System.out.println(users);
        
        System.out.println("-----------------------------------------\n\n");
        Member result = this.groupUserService.saveGroupMember(groupUser);
        assertEquals(groupUser, result);
        //System.out.println(result);
        
        
    }

    @Test
    @Order(2)
    void testSaveGroupMemberNOReg() throws NullValueInUserDataException, UsernameEmailAlreadyExistsException {
        // create user
        Member groupUser = Member.builder()
                .nickname("Pepote el malote")
                .build();
        Member result = this.groupUserService.saveGroupMember(groupUser);
        System.out.println("\n\n-------------------- Second Test ------------");
        System.out.println(result);
        System.out.println("-----------------------------------------\n\n");
    }

    // check exceptions...

    
    public Member deleteGroupUserById(Member id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteGroupUserById'");
    }

    public Member updateNickname(String newNickname) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateNickname'");
    }

    public Member addRegisteredUser(RegisteredUser regUser) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addRegisteredUser'");
    }

    public Member deleteRegisteredUser(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteRegisteredUser'");
    }
}