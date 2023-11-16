package cbs.wantACoffe.service.group;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import cbs.wantACoffe.CommonData;
import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.MemberHasNoNicknameException;
import cbs.wantACoffe.exceptions.NullValueInUserDataException;
import cbs.wantACoffe.exceptions.UsernameEmailAlreadyExistsException;
import cbs.wantACoffe.service.user.IRegisteredUserService;

@ActiveProfiles("h2_test")
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class GroupServiceIntegrationTest {

    @Autowired
    private IGroupService groupService;

    @Autowired
    private IRegisteredUserService userService;

    @Autowired
    private IMemberService meberService;

    

    @Test
    @Order(1)
    void testAddGroupUser() throws NullValueInUserDataException, UsernameEmailAlreadyExistsException, MemberHasNoNicknameException {
        // create user
        RegisteredUser regUser = CommonData.getTestUserWithSuffix("_GroupServiceIntegrationTest");
        //register
        RegisteredUser u = this.userService.saveNewUser(regUser);
        Member m = Member.builder()
            .nickname("Pepote el malote")
            .regUser(u)
                .build();
                
        Group g = Group.builder()
        .groupName("Los chunguitos")
                .build();

        g.getMembers().add(m);
        this.meberService.saveGroupMember(m);
        this.groupService.saveGroup(g);

        System.out.println("\n---------------------------\n");
        System.out.println(g);
        System.out.println("\n---------------------------\n");
        
    }

    @Test
    void testDeleteGroup() {

    }

    @Test
    void testFindAllGroupMembers() {

    }

    @Test
    void testFindGroupById() {

    }

    @Test
    void testFindGroupByOwnerAndName() {

    }

    @Test
    void testFindGroupOwner() {

    }

    @Test
    void testSaveGroup() {

    }
}
