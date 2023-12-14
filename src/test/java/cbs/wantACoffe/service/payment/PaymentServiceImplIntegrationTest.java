package cbs.wantACoffe.service.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import cbs.wantACoffe.CommonData;
import cbs.wantACoffe.dto.payment.IPaymentTotal;
import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.Payment;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.PaymentHasNoAmountException;
import cbs.wantACoffe.exceptions.PaymentHasNoDateException;
import cbs.wantACoffe.exceptions.PaymentHasNoGroupException;
import cbs.wantACoffe.repository.IGroupRepo;
import cbs.wantACoffe.repository.IPaymentRepo;
import cbs.wantACoffe.repository.IRegisteredUserRepo;

import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@ActiveProfiles("h2_test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestMethodOrder(OrderAnnotation.class)
public class PaymentServiceImplIntegrationTest {

    @Autowired
    private PaymentServiceImpl payService;

    @Autowired
    private IPaymentRepo payRepo; // lo usamos para comprobar que se quitan bien

    @Autowired
    private IGroupRepo groupRepo;

    @Autowired
    private IRegisteredUserRepo regUserRepo;

    private static Group testGroup;
    private static List<Member> testMembers;
    private static List<RegisteredUser> testUsers;
    private static List<Payment> testPayments;
    private static final int ADMIN_INDEX = 0;

    private static Date INIT_DATE = Date.valueOf("2023-03-01");
    private static Date END_DATE = Date.valueOf("2023-03-25");
    private static Date BEFORE_DATE = Date.valueOf("2023-02-25");
    private static Date AFTER_DATE = Date.valueOf("2023-03-29");

    @BeforeAll
    static void init() {
        testPayments = new ArrayList<>();

        testGroup = CommonData.getTestGroup();
        testUsers = CommonData.getRegUsersForGroupWithSuffix("_PaymentServiceImplIntegrationTest");
        testMembers = new ArrayList<>();
        for (RegisteredUser u : testUsers) {
            // creo que para esto no hace falta más
            Member m = Member.builder()
                    .nickname(u.getUsername())
                    .regUser(u)
                    .group(testGroup)
                    .build();

            testMembers.add(m);
        }

        createPayments();
    }

    // pffff creamos unos pagos y a ver qué pasa
    static void createPayments() {
        // pay a
        Payment a = Payment.builder()
                .amount(27D)
                .group(testGroup)
                .paymentDate(INIT_DATE)
                .member(testMembers.get(ADMIN_INDEX))
                .memberName(testMembers.get(ADMIN_INDEX).getNickname())
                .build();

        // pay b
        Payment b = Payment.builder()
                .amount(12D)
                .group(testGroup)
                .paymentDate(END_DATE)
                .member(testMembers.get(ADMIN_INDEX))
                .memberName(testMembers.get(ADMIN_INDEX).getNickname())
                .build();
        // pay c
        Payment c = Payment.builder()
                .amount(5D)
                .group(testGroup)
                .paymentDate(AFTER_DATE)
                .member(testMembers.get(ADMIN_INDEX))
                .memberName(testMembers.get(ADMIN_INDEX).getNickname())
                .build();

        Payment d = Payment.builder()
                .amount(12D)
                .group(testGroup)
                .paymentDate(BEFORE_DATE)
                .member(testMembers.get(testMembers.size() - 1))
                .memberName(testMembers.get(testMembers.size() - 1).getNickname())
                .build();

        testPayments.addAll(List.of(a, b, c, d));
    }

    @Test
    @Order(0)
    void initRepos() {
        // metemos los users
        for (RegisteredUser regUser : testUsers) {
            this.regUserRepo.save(regUser);
        }

        // metemos grupos y miembros, quitamos o
        this.groupRepo.save(testGroup);
        testGroup.setMembers(testMembers);
        this.groupRepo.save(testGroup);
        System.out.println("Helloooooo");
    }

    @Test
    @Order(1)
    void testSavePayment() throws PaymentHasNoAmountException, PaymentHasNoDateException, PaymentHasNoGroupException {

        // metemos todos los pagos
        for (Payment p : testPayments) {
            Payment result = this.payService.savePayment(p);
            assertEquals(p, result);
        }
    }

    @Test
    @Order(2)
    void testSavePaymentNoAmount() {
        Payment p = Payment.builder()
                .group(testGroup)
                .member(testMembers.get(ADMIN_INDEX))
                .paymentDate(INIT_DATE).build();

        assertThrows(PaymentHasNoAmountException.class, () -> this.payService.savePayment(p));
    }

    @Test
    @Order(3)
    void testSavePaymentNegative() {
        Payment p = Payment.builder()
                .amount(-25D)
                .group(testGroup)
                .member(testMembers.get(ADMIN_INDEX))
                .paymentDate(INIT_DATE).build();

        assertThrows(PaymentHasNoAmountException.class, () -> this.payService.savePayment(p));
    }

    @Test
    @Order(4)
    void testSavePaymentZero() {
        Payment p = Payment.builder()
                .amount(0D)
                .group(testGroup)
                .member(testMembers.get(ADMIN_INDEX))
                .paymentDate(INIT_DATE).build();

        assertThrows(PaymentHasNoAmountException.class, () -> this.payService.savePayment(p));
    }

    @Test
    @Order(5)
    void testSavePaymentNoDate() {
        Payment p = Payment.builder()
                .amount(27D)
                .group(testGroup)
                .member(testMembers.get(ADMIN_INDEX))
                .build();

        assertThrows(PaymentHasNoDateException.class, () -> this.payService.savePayment(p));
    }

    @Test
    @Order(6)
    void testGetAllPaymentsByGroup() {

        List<Payment> result = this.payService.getAllPaymentsByGroup(testGroup.getGroupId());

        assertEquals(testPayments.size(), result.size());
        assertTrue(this.arePaymentListsEqual(result, testPayments));

    }

    @Test

    @Order(7)
    void testGetAllPaymentsByGroupFilterDate() {
        List<Payment> expected = testPayments.stream().filter(
                p -> this.isDateBetween(p.getPaymentDate(), INIT_DATE, END_DATE))
                .toList();

        List<Payment> result = this.payService.getAllPaymentsByGroup(testGroup.getGroupId(), INIT_DATE,
                END_DATE);
        assertTrue(this.arePaymentListsEqual(expected, result));
    }

    @Test
    @Order(8)
    void testGetAllPaymentsByMember() {
        final String name = testMembers.get(ADMIN_INDEX).getNickname();
        final Long memberId = testMembers.get(ADMIN_INDEX).getMemberId();
        List<Payment> expected = testPayments.stream().filter(
                p -> p.getMemberName().equals(name)).toList();

        List<Payment> result = this.payService.getAllPaymentsByMember(memberId);

        assertTrue(this.arePaymentListsEqual(expected, result));
    }

    @Test

    @Order(9)
    void testGetAllPaymentsByMemberFilterDate() {
        final String name = testMembers.get(ADMIN_INDEX).getNickname();
        final Long memberId = testMembers.get(ADMIN_INDEX).getMemberId();
        List<Payment> expected = testPayments.stream().filter(
                p -> p.getMemberName().equals(name) &&
                        this.isDateBetween(p.getPaymentDate(), INIT_DATE, END_DATE))
                .toList();

        List<Payment> result = this.payService.getAllPaymentsByMember(memberId);
        assertTrue(this.arePaymentListsEqual(expected, result));
    }

    @Test

    @Order(10)
    void testGetAllPaymentTotals() {
        List<IPaymentTotal> expected = new ArrayList<>();
        List<String> keys = testPayments.stream().map(p -> p.getMemberName()).distinct().toList();

        for (String key : keys) {
            double sum = testPayments.stream()
                    .collect(Collectors.summingDouble(p -> p.getMemberName().equals(key) ? p.getAmount() : 0));

            expected.add(new IPaymentTotal() {

                @Override
                public String getNickname() {
                    return key;
                }

                @Override
                public Double getTotalAmount() {
                    return sum;
                }

                @Override
                public Long getMemberId() {
                    Long memberId = -1L;
                    for (Member m : testMembers) {
                        if (m.getNickname().equals(key)) {
                            memberId = m.getMemberId();
                        }
                    }

                    return memberId;
                }

            });
        }

        List<IPaymentTotal> result = this.payService.getAllPaymentTotals(testGroup.getGroupId());
        assertEquals(expected.size(), result.size());
        assertTrue(this.areIPaymentTotalListEqual(expected, result));
    }

    @Test
    @Order(11)
    void testGetAllPaymentTotalsFilterDate() {
        List<IPaymentTotal> expected = new ArrayList<>();
        List<String> keys = testPayments.stream().map(p -> p.getMemberName()).distinct().toList();

        for (String key : keys) {
            double sum = testPayments.stream()
                    .collect(
                            Collectors.summingDouble(p -> this.isDateBetween(p.getPaymentDate(),
                                    INIT_DATE, END_DATE) &&
                                    p.getMemberName().equals(key) ? p.getAmount() : 0));

            if (sum == 0) {
                continue;
            }

            expected.add(new IPaymentTotal() {

                @Override
                public String getNickname() {
                    return key;
                }

                @Override
                public Double getTotalAmount() {
                    return sum;
                }

                @Override
                public Long getMemberId() {
                    Long memberId = -1L;
                    for (Member m : testMembers) {
                        if (m.getNickname().equals(key)) {
                            memberId = m.getMemberId();
                        }
                    }

                    return memberId;
                }
            });
        }

        List<IPaymentTotal> result = this.payService.getAllPaymentTotals(testGroup.getGroupId(), INIT_DATE,
                END_DATE);

        assertEquals(expected.size(), result.size());
        assertTrue(this.areIPaymentTotalListEqual(expected, result));

    }

    @Test
    @Order(12)
    void testGetMemberPaymentTotals() {
        // copy paste de lo otro, no es lo más limpio pero funca igual
        List<IPaymentTotal> expected = new ArrayList<>();
        final String nickname = testMembers.get(ADMIN_INDEX).getNickname();

        double sum = testPayments.stream().collect(
                Collectors.summingDouble(p -> p.getMember().getNickname()
                        .equals(nickname) ? p.getAmount() : 0));

        expected.add(new IPaymentTotal() {

            @Override
            public String getNickname() {
                return testMembers.get(ADMIN_INDEX).getNickname();
            }

            @Override
            public Double getTotalAmount() {
                return sum;
            }

            @Override
            public Long getMemberId() {
                Long memberId = -1L;
                for (Member m : testMembers) {
                    if (m.getNickname().equals(nickname)) {
                        memberId = m.getMemberId();
                    }
                }
                return memberId;
            }
        });

        // TODO: mirar el unit que está mal
        List<IPaymentTotal> result = this.payService.getMemberPaymentTotals(testGroup.getGroupId(), nickname);

        assertEquals(expected.size(), result.size());
        assertTrue(this.areIPaymentTotalListEqual(expected, result));

    }

    @Test
    @Order(13)
    void testGetMemberPaymentTotalsFixedDate() {
        final String nickname = testMembers.get(ADMIN_INDEX).getNickname();
        List<IPaymentTotal> expected = new ArrayList<>();

        double sum = testPayments.stream().collect(
                Collectors.summingDouble(
                        p -> this.isDateBetween(p.getPaymentDate(), INIT_DATE, END_DATE) &&
                                p.getMember().getNickname()
                                        .equals(testMembers.get(ADMIN_INDEX).getNickname()) ? p.getAmount() : 0));

        expected.add(new IPaymentTotal() {

            @Override
            public String getNickname() {
                return testMembers.get(ADMIN_INDEX).getNickname();
            }

            @Override
            public Double getTotalAmount() {
                return sum;
            }

            @Override
            public Long getMemberId() {
                Long memberId = -1L;
                for (Member m : testMembers) {
                    if (m.getNickname().equals(nickname)) {
                        memberId = m.getMemberId();
                    }
                }
                return memberId;
            }
        });

        List<IPaymentTotal> result = this.payService.getMemberPaymentTotals(testGroup.getGroupId(), nickname, INIT_DATE,
                END_DATE);

        assertEquals(expected.size(), result.size());
        assertTrue(this.areIPaymentTotalListEqual(expected, result));
    }

    @Test
    @Order(14)
    void testDeletePayment() {
        Optional<Payment> result;
        for (Payment payment : testPayments) {
            this.payService.deletePayment(payment.getId());
            result = this.payRepo.findById(payment.getId());
            assertTrue(result.isEmpty());
        }
    }

    private boolean isDatePlus(final Date toCheck, final Date limit) {
        return toCheck.compareTo(limit) >= 0;
    }

    private boolean isDateMinus(final Date toCheck, final Date limit) {
        return toCheck.compareTo(limit) <= 0;
    }

    private boolean isDateBetween(final Date toCheck, final Date initDate, final Date endDate) {
        return this.isDatePlus(toCheck, initDate) && this.isDateMinus(toCheck, endDate);
    }

    private boolean arePaymentListsEqual(final List<Payment> listA, final List<Payment> listB) {
        int counter = 0;
        for (Payment payment : listA) {
            for (Payment expected : listB) {
                // podriamos modificar el método equals o crear algo para comparar
                // pero lo dejamos así de momento
                if (payment.getId().equals(expected.getId()) &&
                        payment.getAmount().equals(expected.getAmount()) &&
                        payment.getPaymentDate().equals(expected.getPaymentDate()) &&
                        payment.getMemberName().equals(expected.getMemberName())) {
                    counter++;
                    break;
                }
            }
        }

        return counter == listA.size();
    }

    private boolean areIPaymentTotalListEqual(final List<IPaymentTotal> listA, final List<IPaymentTotal> listB) {
        int counter = 0;
        for (IPaymentTotal payA : listA) {
            for (IPaymentTotal payB : listB) {
                if (payA.getMemberId().equals(payB.getMemberId()) &&
                        payA.getNickname().equals(payB.getNickname()) &&
                        payA.getTotalAmount().equals(payB.getTotalAmount())) {
                    counter++;
                    break;
                }
            }
        }

        return counter == listA.size();
    }
}
