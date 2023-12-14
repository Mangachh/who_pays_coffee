package cbs.wantACoffe.repository;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cbs.wantACoffe.CommonData;
import cbs.wantACoffe.dto.group.IGroupInfo;
import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;

@ActiveProfiles("h2_test")
@DataJpaTest
@Transactional(propagation = Propagation.SUPPORTS)
@TestMethodOrder(OrderAnnotation.class)
public class IGroupRepoTest {

    @Autowired
    private IGroupRepo groupRepo;

    @Autowired
    private IMemberRepo memberRepo;

    @Autowired
    private IRegisteredUserRepo regUserRepo;

    private static List<RegisteredUser> members;

    private static Group testGroup;

    private static Long regUserAdminId;
    private static Long regUserNoAdminId;

    @BeforeAll
    static void getUsers() {
        members = CommonData.getRegUsersForGroupWithSuffix("_IGroupRepoTest");
        testGroup = CommonData.getTestGroup("_IGroupRepotest");
    }

    @Test
    @Order(1)
    void testCreateGroup() {

        // metemos los user en la base de datos
        members.stream().forEach(m -> this.regUserRepo.save(m));

        // registramos a la primera
        Group expected = testGroup;

        // registramos al primer member como admin del grupo
        Member creator = this.memberRepo.save(Member.builder()
                .isAdmin(true)
                .nickname("Soy el primero")
                .regUser(members.get(0))
                .build());

        expected.setOwner(creator);

        boolean memberAdded = expected.tryAddMember(creator);
        Group result = groupRepo.save(expected);

        assertNotNull(result);
        assertEquals(expected.getGroupName(), result.getGroupName());
        assertTrue(memberAdded);
        assertEquals(expected.getMembers().size(), result.getMembers().size());

        // check member, a mano para qeu no de stackover
        Member resultMember = result.getMembers().get(0);

        assertEquals(creator.getRegUser().getUserId(), resultMember.getRegUser().getUserId());
        assertEquals(creator.getMemberId(), resultMember.getMemberId());
        assertEquals(creator.getNickname(), resultMember.getNickname());
        assertEquals(resultMember.getGroup().getGroupId(), result.getGroupId());

        regUserAdminId = creator.getRegUser().getUserId();
    }

    @Test
    @Order(2)
    void testCreateGroupNoName() {

        Group group = new Group();
        assertThrows(DataIntegrityViolationException.class, () -> this.groupRepo.save(group));
    }

    @Test
    @Order(3)
    void testFindById() {
        Group expected = testGroup;
        Group result = this.groupRepo.findById(testGroup.getGroupId()).get();
        Long expectedId = testGroup.getGroupId();

        assertNotNull(result);
        assertEquals(expected.getGroupName(), result.getGroupName());
        assertEquals(expectedId, result.getGroupId());
    }

    @Test
    @Order(4)
    void testFindByIdNoGroup() {
        Optional<Group> result = this.groupRepo.findById(521458L);
        assertTrue(result.isEmpty());
    }

    @Test
    @Order(5)
    void testAddMemberToGroup() {
        RegisteredUser toAdd = members.get(1);
        Optional<Group> g = this.groupRepo.findById(testGroup.getGroupId());

        assertTrue(g.isPresent());
        Member memberToAdd = Member.builder()
                .nickname("Prueba exitosa")
                .regUser(toAdd)
                .build();

        assertTrue(g.get().tryAddMember(memberToAdd));
        assertTrue(g.get().getMembers().contains(memberToAdd));
        g.get().tryAddMember(memberToAdd);

        // save the group
        testGroup = this.groupRepo.save(g.get());

        Optional<Group> result = this.groupRepo.findById(testGroup.getGroupId());
        assertTrue(result.isPresent());
        assertEquals(g.get().getMembers().size(), result.get().getMembers().size());
        assertEquals(g.get().getGroupId(), result.get().getGroupId());
        assertEquals(g.get().getGroupName(), result.get().getGroupName());

        regUserNoAdminId = memberToAdd.getRegUser().getUserId();
    }

    @Test
    @Order(6)
    void testFindAllByMembersRegUser() {
        final int index = 0;
        List<Group> groups = this.groupRepo.findAllByMembersRegUser(
                testGroup.getMembers().get(index).getRegUser());
        assertEquals(groups.size(), 1);
        assertEquals(groups.get(index).getGroupName(), testGroup.getGroupName());
        assertEquals(groups.get(0).getGroupId(), testGroup.getGroupId());
    }

    @Test
    @Order(6)
    void testFindAllByMembersIsAdmin() {

        List<Group> groups = this.groupRepo.findAllByRegUserAndMembersIsAdmin(
                regUserAdminId,
                true);

        assertEquals(1, groups.size());
        assertEquals(groups.get(0).getGroupName(), testGroup.getGroupName());
        assertEquals(groups.get(0).getGroupId(), testGroup.getGroupId());

    }

    @Test
    @Order(7)
    void testFindAllByMembersIsNotAdmin() {
        List<Group> groups = this.groupRepo.findAllByRegUserAndMembersIsAdmin(
                regUserNoAdminId,
                false);
        assertEquals(1, groups.size());
        assertEquals(groups.get(0).getGroupName(), testGroup.getGroupName());
        assertEquals(groups.get(0).getGroupId(), testGroup.getGroupId());
    }

    @Test
    @Order(8)
    void testFindAllGroupsAndCountMembers() {

        // vale este me da problemas al hacer prueba de todos :(

        Long groupNum = this.groupRepo.count();
        List<Group> allGroups = this.groupRepo.findAll();

        List<IGroupInfo> groupsInfo = this.groupRepo.findAllGroupsAndCountMembers();

        assertEquals(groupNum, groupsInfo.size());
        int counter = 0;

        for (Group g : allGroups) {
            // check if group is in group info
            for (IGroupInfo info : groupsInfo) {
                if (info.getGroupName().equals(g.getGroupName()) &&
                        info.getNumMembers() == g.getMembers().size()) {
                    counter++;
                    break;
                }
            }
        }

        // suponemos que todo está bien
        assertEquals(counter, groupsInfo.size());
    }

}
