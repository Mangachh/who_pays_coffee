package cbs.wantACoffe.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import cbs.wantACoffe.CommonData;
import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.Payment;
import cbs.wantACoffe.entity.RegisteredUser;

import org.springframework.transaction.annotation.Propagation;

@ActiveProfiles("h2_test")
@DataJpaTest
@Transactional(propagation =Propagation.SUPPORTS)
@TestMethodOrder(OrderAnnotation.class)
public class IPaymentRepoTest {

    @Autowired
    private IGroupRepo groupRepo;

    @Autowired
    private IMemberRepo memberRepo;

    @Autowired
    private IRegisteredUserRepo regUserRepo;

    @Autowired
    private IPaymentRepo paymentRepo;

    private static List<Member> testMembers;
    private static List<RegisteredUser> testUsers;
    private static Group testGroup;
    
    private final static int ADMIN_INDEX = 0;

    @BeforeAll
    static void getGroup() {
        testGroup = CommonData.getTestGroup();
        testUsers = CommonData.getRegUsersForGroupWithSuffix("_IPaymentRepoTest");
        testMembers = new ArrayList<>();

        for (RegisteredUser u : testUsers) {
            testMembers.add(
                    Member.builder()
                            .nickname("NICK_" + u.getUsername())
                            .regUser(u)
                            .build());
        }
        
        // ponemos como admin al primero, y owner
        
    }

    @Test
    @Order(1)
    void init() {
        // registramos users
        testUsers.stream().forEach(this.regUserRepo::save);

        // registramos grupo
        this.groupRepo.save(testGroup);

        testGroup.setMembers(testMembers);
        testGroup.setOwner(testMembers.get(ADMIN_INDEX));
        testGroup.getMembers().get(ADMIN_INDEX).setAdmin(true);
        this.groupRepo.save(testGroup);
        // y debería funcar?

    }

    @Test
    @Order(2)
    void testAddPaymentCorrect() {
        Payment expected = Payment.builder()
                .amount(22.54d)
                .member(testMembers.get(ADMIN_INDEX))
                .memberName(testMembers.get(ADMIN_INDEX).getNickname())
                .paymentDate(Date.valueOf("2023-05-01"))
                .group(testGroup)
                .build();
        testMembers.get(ADMIN_INDEX).getPayments().add(expected);
        Payment result = this.paymentRepo.save(expected);
        assertEquals(expected, result);

    }
    
    @Test
    @Order(3)
    void testAddPaymentIncorrectNoAmount() {
        Payment expected = Payment.builder()
                .member(testMembers.get(ADMIN_INDEX))
                .memberName(testMembers.get(ADMIN_INDEX).getNickname())
                .paymentDate(Date.valueOf("2023-05-01"))
                .group(testGroup)
                .build();        

        assertThrows(DataIntegrityViolationException.class,
                () -> this.paymentRepo.save(expected));
    }
    
    @Test
    @Order(4)
    void testAddPaymentIncorrectNoGroup() {
        Payment expected = Payment.builder()
                .amount(654D)
                .member(testMembers.get(ADMIN_INDEX))
                .memberName(testMembers.get(ADMIN_INDEX).getNickname())
                .paymentDate(Date.valueOf("2023-05-01"))
                .build();

        assertThrows(DataIntegrityViolationException.class,
                () -> this.paymentRepo.save(expected));
    }
    
    @Test
    @Order(5)
    void testAddPaymentIncorrectNoDate() {
        Payment expected = Payment.builder()
                .amount(654D)
                .member(testMembers.get(ADMIN_INDEX))
                .memberName(testMembers.get(ADMIN_INDEX).getNickname())
                .group(testGroup)
                .build();        

        assertThrows(DataIntegrityViolationException.class,
                () -> this.paymentRepo.save(expected));
    }
    
    @Test
    @Order(6)
    void testFindAllTotalsByGroup() {
        // 'amos a pillar las cosas a mano, no lo más eficiente pero bueno
        List<Payment> expected = new ArrayList<>();
        for (Member m : testMembers) {
            expected.addAll(m.getPayments());
        }

        List<Payment> result = this.paymentRepo.findAllByGroupGroupIdOrderByPaymentDateAsc(testGroup.getGroupId());

        assertEquals(expected.size(), result.size());
    }
    
    @Test
    @Order(7)
    void testFindAllTotalsByGroupNoExists() {
        List<Payment> result = this.paymentRepo.findAllByGroupGroupIdOrderByPaymentDateAsc(5555L);

        assertEquals(0, result.size());   
    }
    

    @Test
    void testFindAllByGroupGroupIdAndPaymentDateBetweenOrderByPaymentDateAsc() {
        // metemos dos pagos con 2 fechas intermedias
        // pillamos esos pagos y ale
        final String initDate = "2222-05-07";
        final String endDate = "2222-05-08";
        final String beforeInit = "2222-05-06";
        final String afterEnd = "2222-05-09";

        // creamos pagos

    }

    @Test
    void testFindAllByGroupGroupIdOrderByPaymentDateAsc() {

    }

    @Test
    void testFindAllByMemberMemberIdAndPaymentDateBetweenOrderByPaymentDateAsc() {

    }

    @Test
    void testFindAllByMemberMemberIdOrderByPaymentDateAsc() {

    }

    

    @Test
    void testFindAllTotalsByGroupBetweenDates() {

    }

    @Test
    void testFindTotalsByMemberAndGroup() {

    }

    @Test
    void testFindTotalsByMemberAndGroupBetweenDates() {

    }
}
