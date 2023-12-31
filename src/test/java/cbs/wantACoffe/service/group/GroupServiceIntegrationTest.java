package cbs.wantACoffe.service.group;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import cbs.wantACoffe.CommonData;
import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.GroupHasNoNameException;
import cbs.wantACoffe.exceptions.GroupNotExistsException;
import cbs.wantACoffe.exceptions.MemberHasNoNicknameException;
import cbs.wantACoffe.exceptions.NullValueInUserDataException;
import cbs.wantACoffe.exceptions.UsernameEmailAlreadyExistsException;
import cbs.wantACoffe.service.user.IRegisteredUserService;

@ActiveProfiles("h2_test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestMethodOrder(OrderAnnotation.class)
public class GroupServiceIntegrationTest {

    @Autowired
    private GroupServiceImpl groupService;

    @Autowired
    private IRegisteredUserService userService;

    @Autowired
    private IMemberService meberService;

    private static Group testGroup;
    private static List<RegisteredUser> testUsers;

    @BeforeAll
    static void init() {
        testGroup = CommonData.getTestGroup("_GroupServiceIntegrationTest");
        testUsers = CommonData.getRegUsersForGroupWithSuffix("_GroupServiceIntegrationTest");
    }

    @Test
    @Order(1)
    void testSaveGroup() throws MemberHasNoNicknameException, NullValueInUserDataException,
            UsernameEmailAlreadyExistsException, GroupHasNoNameException {

        RegisteredUser user = this.userService.saveNewUser(testUsers.get(0));

        Member member = Member.builder()
                .nickname("Pepote el malote")
                .regUser(user)
                .build();

        testGroup.getMembers().add(member);
        this.meberService.saveGroupMember(member);
        testGroup.setOwner(member);
        Group result = this.groupService.saveGroup(testGroup);

        assertEquals(testGroup.getGroupName(), result.getGroupName());
        assertEquals(testGroup.getGroupId(), result.getGroupId());

        testGroup = result;
    }
    
    @Test
    @Order(2)
    void testSaveGroupNoName() {
        assertThrows(GroupHasNoNameException.class,
                () -> this.groupService.saveGroup(new Group()));
    }

    @Test
    @Order(3)
    void testFindGroupById() throws GroupNotExistsException {
        Group result = this.groupService.getGroupById(testGroup.getGroupId());
        assertEquals(testGroup.getGroupName(), result.getGroupName());
        assertEquals(testGroup.getGroupId(), result.getGroupId());
    }

    @Test
    @Order(4)
    void testFindGroupByIdNotExists() {
        assertThrows(
                GroupNotExistsException.class,
                () -> this.groupService.getGroupById(45545454L));
    }    
     

    @Test
    void testDeleteGroup() {
        this.groupService.deleteGroup(testGroup.getGroupId());
        assertThrows(
                GroupNotExistsException.class,
                () -> this.groupService.getGroupById(testGroup.getGroupId()));
    }

   
    

   

   
}
