package cbs.wantACoffe.repository;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cbs.wantACoffe.CoffeeApplication;
import cbs.wantACoffe.CommonData;
import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;

@ActiveProfiles("h2_test")
@DataJpaTest
@Transactional(propagation =Propagation.SUPPORTS)
@TestMethodOrder(OrderAnnotation.class)
public class IGroupRepoTest {

    @Autowired
    private IGroupRepo groupRepo;

    @Autowired
    private IMemberRepo memberRepo;

    @Autowired
    private IRegisteredMemberRepo regUserRepo;

    private static List<RegisteredUser> members;
    

    private static Group testGroup;

    @BeforeAll
    static void getUsers() {
        members = CommonData.getRegUsersForGroupWithSuffix("_IGroupRepoTest");
        testGroup = CommonData.getTestGroup();
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

        boolean memberAdded = expected.tryAddMember(creator);
        Group result = groupRepo.save(expected);

        assertNotNull(result);
        assertEquals(expected.getGroupName(), result.getGroupName());
        assertTrue(memberAdded);
        assertEquals(expected.getMembers().size(), result.getMembers().size());

        // check member, a mano para qeu no de stackover
        Member resultMember = result.getMembers().get(0);

        assertEquals(creator.getRegUser(), resultMember.getRegUser());
        assertEquals(creator.getId(), resultMember.getId());
        assertEquals(creator.getNickname(), resultMember.getNickname());
        assertEquals(resultMember.getGroup().getGroupId(), result.getGroupId());

        System.out.println("\n--------------------\n");
        System.out.println(creator);
        System.out.println("\n------------------------\n");
    }
    
    @Test
    @Order(2)
    void testFindById() {
        Group expected = testGroup;
        Group result = this.groupRepo.findById(testGroup.getGroupId()).get();
        Long expectedId = testGroup.getGroupId();

        assertNotNull(result);
        assertEquals(expected.getGroupName(), result.getGroupName());
        assertEquals(expectedId, result.getGroupId());
    }

    void testAddMemberToGroup() {
        
    }

    @Test
    void testFindAllByMembersIsAdmin() {

    }

    @Test
    void testFindAllByMembersRegUser() {

    }
}
