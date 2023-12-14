package cbs.wantACoffe.service.group;

import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.mockito.Mockito;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import cbs.wantACoffe.CommonData;
import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.MemberHasNoNicknameException;
import cbs.wantACoffe.exceptions.MemberNotInGroup;
import cbs.wantACoffe.exceptions.NullValueInUserDataException;
import cbs.wantACoffe.exceptions.UsernameEmailAlreadyExistsException;
import cbs.wantACoffe.repository.IMemberRepo;

import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

@ActiveProfiles("h2_test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestMethodOrder(OrderAnnotation.class)
public class MemberServiceUnitTest {

    @Autowired
    private IMemberService memberService;

    @MockBean
    private IMemberRepo memberRepo;

    private static Group testGroup;

    private static List<RegisteredUser> users;
    private static Member testMember;

    @BeforeAll
    static void populateUsers() {
        users = CommonData.getRegUsersForGroupWithSuffix("_MemberServiceUnitTest");
        testGroup = CommonData.getTestGroup();
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

        Mockito.when(this.memberRepo.save(groupUser))
                .thenReturn(groupUser);

        Member result = this.memberService.saveGroupMember(groupUser);
        assertEquals(groupUser, result);
        testMember = result;
        testMember.setMemberId(25L); // en integración l hace el repo
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
        Mockito.when(this.memberRepo.save(groupUser))
                .thenReturn(groupUser);

        Member result = this.memberService.saveGroupMember(groupUser);
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

        Mockito.when(this.memberRepo.findMemberByGroupIdAndRegUserId(
                testGroup.getGroupId(),
                testMember.getRegUser().getUserId()))
                .thenReturn(Optional.of(testMember));

        Member m = this.memberService.getMemberByGroupIdAndRegUserId(
                testGroup.getGroupId(),
                testMember.getRegUser().getUserId());

        assertNotNull(m);
        assertEquals(user, m.getRegUser());
    }

    @Test
    @Order(5)
    void testFindMemberByGroupIdAndRegUserIdNoRegUser() {

        Mockito.when(this.memberRepo.findMemberByGroupIdAndRegUserId(
                testGroup.getGroupId(),
                testMember.getRegUser().getUserId()))
                .thenReturn(Optional.empty());

        assertThrows(MemberNotInGroup.class, () -> this.memberService.getMemberByGroupIdAndRegUserId(
                testGroup.getGroupId(),
                testMember.getRegUser().getUserId()));
    }

    @Test
    @Order(6)
    void testFindMemberById() throws MemberNotInGroup {
        // pillamos miebro como lo habíamos hecho en los test anterires, si funca antes,
        // funca ahora
        // así tenemos el id que queremos
        Member expected = testMember;
        Mockito.when(this.memberRepo.findById(expected.getMemberId()))
                .thenReturn(Optional.of(testMember));

        Member result = this.memberService.getMemberById(expected.getMemberId());
        assertEquals(expected.getNickname(), result.getNickname());
    }

    @Test
    @Order(7)
    void testFindMemberByIdNotFound() {
        // pillamos miebro como lo habíamos hecho en los test anteriores, si funca
        // antes, funca ahora
        // así tenemos el id que queremos
        Long id = 54545454L;
        Mockito.when(this.memberRepo.findById(id))
                .thenReturn(Optional.empty());
        assertThrows(MemberNotInGroup.class,
                () -> this.memberService.getMemberById(id));
    }

    @Test
    @Order(8)
    void testFindMemberByGroupNameAndNickname() throws MemberNotInGroup {
        Member expected = testMember;

        Mockito.when(this.memberRepo.findMemberByGroupIdAndNickname(
                testGroup.getGroupId(),
                testMember.getNickname()))
                .thenReturn(Optional.of(testMember));

        Member result = this.memberService.getMemberByGroupIdAndNickname(
                testGroup.getGroupId(),
                testMember.getNickname());

        assertNotNull(result);
        assertEquals(expected.getMemberId(), result.getMemberId());
        assertEquals(expected.getNickname(), result.getNickname());
        assertEquals(expected.getGroup().getGroupName(), result.getGroup().getGroupName());
    }

    @Test
    @Order(9)
    void testFindMemberByGroupNameAndNicknameNotInGroup() throws MemberNotInGroup {
        String nickname = "adasdasdas";

        Mockito.when(this.memberRepo.findMemberByGroupIdAndNickname(
                testGroup.getGroupId(),
                nickname))
                .thenReturn(Optional.empty());

        assertThrows(MemberNotInGroup.class,
                () -> this.memberService.getMemberByGroupIdAndNickname(
                        testGroup.getGroupId(),
                        nickname));
    }

}
