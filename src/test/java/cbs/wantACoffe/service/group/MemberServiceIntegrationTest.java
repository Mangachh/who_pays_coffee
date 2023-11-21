package cbs.wantACoffe.service.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.MemberHasNoNicknameException;
import cbs.wantACoffe.exceptions.MemberNotInGroup;
import cbs.wantACoffe.exceptions.NullValueInUserDataException;
import cbs.wantACoffe.exceptions.UsernameEmailAlreadyExistsException;
import cbs.wantACoffe.repository.IGroupRepo;
import cbs.wantACoffe.repository.IRegisteredUserRepo;

@ActiveProfiles("h2_test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestMethodOrder(OrderAnnotation.class)
public class MemberServiceIntegrationTest {

    @Autowired
    private IMemberService memberService;

    @Autowired
    private IRegisteredUserRepo userRepo;

    @Autowired
    private IGroupRepo groupRepo;

    private static Group testGroup;

    private static List<RegisteredUser> users;
    private static Member testMember;

    @BeforeAll
    static void populateUsers() {
        users = CommonData.getRegUsersForGroupWithSuffix("_MemberServiceIntegrationTest");
        testGroup = CommonData.getTestGroup();
    }

    @BeforeEach
    void saveUsers() {
        try {
            for (RegisteredUser u : users) {
                this.userRepo.save(u);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @Test
    @Order(1)
    void testSaveGroupMember()
            throws NullValueInUserDataException, UsernameEmailAlreadyExistsException, MemberHasNoNicknameException {

        Member groupUser = Member.builder()
                .nickname("First User")
                .regUser(users.get(0))
                .group(testGroup)
                .isAdmin(true)
                .build();
        // grabamos grupo
        testGroup = this.groupRepo.save(testGroup);
        Member result = this.memberService.saveGroupMember(groupUser);
        assertEquals(groupUser, result);
        testMember = result;
    }

    @Test
    @Order(2)
    void testSaveGroupMemberNOReg()
            throws NullValueInUserDataException, UsernameEmailAlreadyExistsException, MemberHasNoNicknameException {
        // create user
        Member groupUser = Member.builder()
                .nickname("Pepote el malote")
                .group(testGroup)
                .build();
        Member result = this.memberService.saveGroupMember(groupUser);
        testGroup.tryAddMember(result);
        assertEquals(groupUser, result);
    }

    // check exceptions...
    @Test
    @Order(3)
    void testSaveGroupMemberNoNickname() {
        Member member = new Member();
        assertThrows(MemberHasNoNicknameException.class,
                () -> this.memberService.saveGroupMember(member));
    }

    @Test
    @Order(4)
    void testFindMemberByGroupIdAndRegUserId() throws MemberNotInGroup {
        RegisteredUser user = users.get(0);

        Member m = this.memberService.findMemberByGroupIdAndRegUserId(
                testGroup.getGroupId(),
                testMember.getRegUser().getUserId());

        assertNotNull(m);
        assertEquals(user, m.getRegUser());
    }

    @Test
    @Order(5)
    void testFindMemberByGroupIdAndRegUserIdNotExists() {
        RegisteredUser user = users.get(0);

        assertThrows(MemberNotInGroup.class,
                () -> this.memberService.findMemberByGroupIdAndRegUserId(testGroup.getGroupId(), 2548L));

    }

    @Test
    @Order(6)
    void testFindMemberById() throws MemberNotInGroup {
        // pillamos miebro como lo habíamos hecho en los test anterires, si funca antes,
        // funca ahora
        // así tenemos el id que queremos
        Member expected = testMember;

        Member result = this.memberService.findMemberById(expected.getId());
        assertEquals(expected.getNickname(), result.getNickname());
    }

    @Test
    @Order(7)
    void testFindMemberByIdNotFound() {
        // pillamos miebro como lo habíamos hecho en los test anteriores, si funca
        // antes, funca ahora
        // así tenemos el id que queremos
        assertThrows(MemberNotInGroup.class,
                () -> this.memberService.findMemberById(8547778L));
    }

    @Test
    @Order(8)
    void testFindMemberByGroupNameAndNickname() throws MemberNotInGroup {
        Member expected = testMember;
        Member result = this.memberService.findMemberByGroupIdAndNickname(
                testGroup.getGroupId(),
                testMember.getNickname());

        assertNotNull(result);
        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getNickname(), result.getNickname());
        assertEquals(expected.getGroup().getGroupName(), result.getGroup().getGroupName());
    }

    @Test
    @Order(9)
    void testFindMemberByGroupNameAndNicknameNotInGroup() throws MemberNotInGroup {
        assertThrows(MemberNotInGroup.class,
                () -> this.memberService.findMemberByGroupIdAndNickname(
                        testGroup.getGroupId(),
                        "asdasdasdasdasdasdads"));
    }

    @Test
    @Order(10)
    void testDeleteGroumMemberById() {
        // oju! conforme metamos cosas esto dará errores...
        this.memberService.deleteGroupMemberById(testMember.getId());
        // intentamos pillar miembro
        assertThrows(MemberNotInGroup.class,
                () -> this.memberService.findMemberById(testMember.getId()));
    }

}