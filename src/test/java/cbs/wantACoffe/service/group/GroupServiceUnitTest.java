package cbs.wantACoffe.service.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import cbs.wantACoffe.CommonData;
import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.GroupHasNoNameException;
import cbs.wantACoffe.exceptions.GroupNotExistsException;
import cbs.wantACoffe.exceptions.MemberHasNoNicknameException;
import cbs.wantACoffe.exceptions.NullValueInUserDataException;
import cbs.wantACoffe.exceptions.UsernameEmailAlreadyExistsException;
import cbs.wantACoffe.repository.IGroupRepo;
import cbs.wantACoffe.service.user.IRegisteredUserService;

@ActiveProfiles("h2_test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestMethodOrder(OrderAnnotation.class)
public class GroupServiceUnitTest {
    @Autowired
    private IGroupService groupService;

    @MockBean
    private IGroupRepo groupRepo;

    private static Group testGroup;
    private static List<RegisteredUser> testUsers;

    @BeforeAll
    static void init() {
        testGroup = CommonData.getTestGroup();
        testUsers = CommonData.getRegUsersForGroupWithSuffix("_GroupServiceIntegrationTest");
    }

    @Test
    @Order(1)
    void testSaveGroup() throws MemberHasNoNicknameException, NullValueInUserDataException,
        UsernameEmailAlreadyExistsException, GroupHasNoNameException {

       // RegisteredUser user = this.userService.saveNewUser(testUsers.get(0));

        Member member = Member.builder()
                .nickname("Pepote el malote")
                .build();

        testGroup.getMembers().add(member);
        testGroup.setOwner(member);

        Mockito.when(this.groupRepo.save(testGroup))
                .thenReturn(testGroup);
                
        
        Group result = this.groupService.saveGroup(testGroup);

        assertEquals(testGroup.getGroupName(), result.getGroupName());
        assertEquals(testGroup.getGroupId(), result.getGroupId());

        testGroup = result;
    }
    
    @Test
    @Order(2)
    void testSaveGroupNoName() {
        Group noName = new Group();
        
        assertThrows(GroupHasNoNameException.class,
                () -> this.groupService.saveGroup(new Group()));
    }

    @Test
    @Order(3)
    void testFindGroupById() throws GroupNotExistsException {
        Mockito.when(this.groupRepo.findById(testGroup.getGroupId()))
                .thenReturn(Optional.of(testGroup));

        Group result = this.groupService.getGroupById(testGroup.getGroupId());
        assertEquals(testGroup.getGroupName(), result.getGroupName());
        assertEquals(testGroup.getGroupId(), result.getGroupId());
    }

    @Test
    @Order(4)
    void testFindGroupByIdNotExists() {
        long id = 6554744L;
        Mockito.when(this.groupRepo.findById(id))
                .thenReturn(Optional.empty());
        assertThrows(
                GroupNotExistsException.class,
                () -> this.groupService.getGroupById(id));
    }    
     

    
}
