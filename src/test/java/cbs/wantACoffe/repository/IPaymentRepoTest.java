package cbs.wantACoffe.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import cbs.wantACoffe.dto.payment.IPaymentTotal;
import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.Payment;
import cbs.wantACoffe.entity.RegisteredUser;

import org.springframework.transaction.annotation.Propagation;

@ActiveProfiles("h2_test")
@DataJpaTest
@Transactional(propagation = Propagation.SUPPORTS)
@TestMethodOrder(OrderAnnotation.class)
public class IPaymentRepoTest {

    @Autowired
    private IGroupRepo groupRepo;

    @Autowired
    private IRegisteredUserRepo regUserRepo;

    @Autowired
    private IPaymentRepo paymentRepo;

    private static List<Member> testMembers;
    private static List<RegisteredUser> testUsers;
    private static List<Payment> allPayments;
    private static Group testGroup;

    // fechas
    private final String INIT_DATE = "2222-05-07";
    private final String END_DATE = "2222-05-15";
    private final String BETWEEN_DATE = "2222-05-09";
    private final String BEFORE_INIT = "2222-05-06";
    private final String AFTER_END = "2222-05-16";

    private final static int ADMIN_INDEX = 0;

    @BeforeAll
    static void getGroup() {
        testGroup = CommonData.getTestGroup("_IPaymentRepoTest");
        testUsers = CommonData.getRegUsersForGroupWithSuffix("_IPaymentRepoTest");
        testMembers = new ArrayList<>();
        allPayments = new ArrayList<>();
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

        // metemos payment en la lista
        allPayments.add(result);

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
        int numEquals = this.checkPaymentsAndReturnNumberEquals(result, expected);
        assertEquals(numEquals, result.size());
    }

    private int checkPaymentsAndReturnNumberEquals(final List<Payment> resultList, final List<Payment> expectedList) {
        int counter = 0;
        for (Payment res : resultList) {
            for (Payment ex : expectedList) {
                if (ex.getId().equals(res.getId()) &&
                        ex.getMemberName().equals(res.getMemberName()) &&
                        ex.getAmount().equals(res.getAmount())) {
                    counter++;
                    break;
                }
            }
        }

        return counter;
    }

    @Test
    @Order(7)
    void testFindAllTotalsByGroupNoExists() {
        List<Payment> result = this.paymentRepo.findAllByGroupGroupIdOrderByPaymentDateAsc(5555L);

        assertEquals(0, result.size());
    }

    @Test
    @Order(8)
    void testFindAllByGroupGroupIdAndPaymentDateBetweenOrderByPaymentDateAsc() {
        // metemos dos pagos con 2 fechas intermedias
        // pillamos esos pagos y ale

        List<Payment> expectedPayemnts = new ArrayList<>();

        // creamos pagos
        Payment initPayment = Payment.builder()
                .amount(25D)
                .group(testGroup)
                .member(testMembers.get(ADMIN_INDEX))
                .memberName(testMembers.get(ADMIN_INDEX).getNickname())
                .paymentDate(Date.valueOf(this.INIT_DATE))
                .build();
        this.paymentRepo.save(initPayment);
        expectedPayemnts.add(initPayment);

        Payment endPayment = Payment.builder()
                .amount(21D)
                .group(testGroup)
                .member(testMembers.get(ADMIN_INDEX))
                .memberName(testMembers.get(ADMIN_INDEX).getNickname())
                .paymentDate(Date.valueOf(this.END_DATE))
                .build();
        this.paymentRepo.save(endPayment);
        expectedPayemnts.add(endPayment);

        Payment betweenPayment = Payment.builder()
                .amount(19D)
                .group(testGroup)
                .member(testMembers.get(ADMIN_INDEX))
                .memberName(testMembers.get(ADMIN_INDEX).getNickname())
                .paymentDate(Date.valueOf(this.BETWEEN_DATE))
                .build();
        this.paymentRepo.save(betweenPayment);
        expectedPayemnts.add(betweenPayment);

        Payment afterPayment = Payment.builder()
                .amount(25D)
                .group(testGroup)
                .member(testMembers.get(ADMIN_INDEX))
                .memberName(testMembers.get(ADMIN_INDEX).getNickname())
                .paymentDate(Date.valueOf(this.AFTER_END))
                .build();
        this.paymentRepo.save(afterPayment);
        allPayments.add(afterPayment);

        Payment beforePayment = Payment.builder()
                .amount(25D)
                .group(testGroup)
                .member(testMembers.get(ADMIN_INDEX))
                .memberName(testMembers.get(ADMIN_INDEX).getNickname())
                .paymentDate(Date.valueOf(this.BEFORE_INIT))
                .build();
        this.paymentRepo.save(beforePayment);
        allPayments.add(beforePayment);
        allPayments.addAll(expectedPayemnts);

        List<Payment> result = this.paymentRepo
                .findAllByGroupGroupIdAndPaymentDateBetweenOrderByPaymentDateAsc(testGroup.getGroupId(),
                        Date.valueOf(INIT_DATE), Date.valueOf(END_DATE));

        assertEquals(expectedPayemnts.size(), result.size());
        int numEquals = this.checkPaymentsAndReturnNumberEquals(result, expectedPayemnts);
        assertEquals(numEquals, result.size());
    }

    @Test
    @Order(9)
    void testFindAllByGroupGroupIdOrderByPaymentDateAsc() {
        // bien aquí sacaremos todos los pagos y...
        // espera que podemos poner too en una lista
        List<Payment> result = this.paymentRepo.findAllByGroupGroupIdOrderByPaymentDateAsc(testGroup.getGroupId());

        assertEquals(result.size(), allPayments.size());
        // chequeamos que la siguiente fecha es mayoer que la anterior
        for (int i = 0; i < result.size() - 2; i++) {

            int comparition = result.get(i).getPaymentDate().compareTo(
                    result.get(i + 1).getPaymentDate());
            assertTrue(comparition <= 0);
        }

        int numCheck = this.checkPaymentsAndReturnNumberEquals(result, allPayments);
        assertEquals(result.size(), numCheck);
    }

    @Test
    @Order(10)
    void testFindAllByMemberMemberIdAndPaymentDateBetweenOrderByPaymentDateAsc() {
        // pillamos los pagos de la lista
        List<Payment> expected = allPayments.stream().filter(
                p -> p.getMemberName().equals(testMembers.get(ADMIN_INDEX).getNickname()) &&
                        p.getPaymentDate().compareTo(Date.valueOf(INIT_DATE)) >= 0 &&
                        p.getPaymentDate().compareTo(Date.valueOf(END_DATE)) <= 0)
                .toList();

        // método
        List<Payment> result = this.paymentRepo
                .findAllByMemberMemberIdAndPaymentDateBetweenOrderByPaymentDateAsc(
                        testMembers.get(ADMIN_INDEX).getMemberId(), Date.valueOf(INIT_DATE), Date.valueOf(END_DATE));

        assertEquals(expected.size(), result.size());
        int counter = this.checkPaymentsAndReturnNumberEquals(result, expected);
        assertEquals(counter, result.size());
    }

    @Test
    @Order(11)
    void testFindAllByMemberMemberIdOrderByPaymentDateAsc() {
        // pillamos los pagos
        List<Payment> expected = allPayments.stream().filter(
                p -> p.getMemberName().equals(testMembers.get(ADMIN_INDEX).getNickname())).toList();

        // check
        List<Payment> result = this.paymentRepo
                .findAllByGroupGroupIdOrderByPaymentDateAsc(testMembers.get(ADMIN_INDEX).getMemberId());

        assertEquals(expected.size(), result.size());
        int counter = this.checkPaymentsAndReturnNumberEquals(result, expected);
        assertEquals(counter, result.size());
    }

    @Test
    @Order(12)
    void testFindAllTotalsByGroupBetweenDates() {
        Map<String, Double> expected = new HashMap<>();

        // keys
        List<String> keys = allPayments.stream().map(p -> p.getMemberName()).distinct().toList();

        // values
        for (String key : keys) {
            double sum = allPayments.stream().collect(Collectors.summingDouble(
                    p -> p.getPaymentDate().compareTo(Date.valueOf(INIT_DATE)) >= 0 &&
                            p.getPaymentDate().compareTo(Date.valueOf(END_DATE)) <= 0 &&
                            p.getMemberName().equals(key) ? p.getAmount() : 0));
            expected.put(key, sum);
        }

        List<IPaymentTotal> result = this.paymentRepo.findAllTotalsByGroupBetweenDates(
                testGroup.getGroupId(), Date.valueOf(INIT_DATE), Date.valueOf(END_DATE));

        assertEquals(expected.size(), result.size());
        for (IPaymentTotal pay : result) {
            // pillamos key
            String key = pay.getNickname();

            // pillamos total
            double total = pay.getTotalAmount();

            // compare
            assertEquals(expected.get(key), total);

        }

    }

    @Test
    @Order(13)
    void testFindTotalsByMemberAndGroup() {
        Map<String, Double> expected = new HashMap<>();

        // keys
        List<String> keys = allPayments.stream().map(p -> p.getMemberName()).distinct().toList();

        // values
        for (String key : keys) {
            double sum = allPayments.stream().collect(Collectors.summingDouble(
                    p -> p.getMemberName().equals(key) ? p.getAmount() : 0));
            expected.put(key, sum);
        }

        List<IPaymentTotal> result = this.paymentRepo.findAllTotalsByGroup(testGroup.getGroupId());

        assertEquals(expected.size(), result.size());
        for (IPaymentTotal pay : result) {
            // pillamos key
            String key = pay.getNickname();

            // pillamos total
            double total = pay.getTotalAmount();

            // compare
            assertEquals(expected.get(key), total);

        }
    }

    @Test
    void testFindTotalsByMemberAndGroupBetweenDates() {
        final String memberName = testMembers.get(ADMIN_INDEX).getNickname();
        final Double totals = allPayments.stream().collect(Collectors.summingDouble(
            p -> p.getPaymentDate().compareTo(Date.valueOf(INIT_DATE)) >= 0 &&
                 p.getPaymentDate().compareTo(Date.valueOf(END_DATE)) <= 0 &&
                 p.getMemberName().equals(memberName) ?
                 p.getAmount() : 0 
        ));

        List<IPaymentTotal> result = this.paymentRepo.findTotalsByMemberAndGroupBetweenDates(
            testGroup.getGroupId(), 
            memberName, 
                Date.valueOf(INIT_DATE), Date.valueOf(END_DATE));

        // solo devuelve un user
        assertEquals(result.size(), 1);

        assertEquals(memberName, result.get(0).getNickname());
        assertEquals(totals, result.get(0).getTotalAmount());

    }
}
