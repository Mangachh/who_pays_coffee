package cbs.wantACoffe.service.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.mockito.Mockito;
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
import cbs.wantACoffe.repository.IPaymentRepo;

import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

@ActiveProfiles("h2_test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestMethodOrder(OrderAnnotation.class)
public class PaymentServiceImplUnitTest {

    @Autowired
    private PaymentServiceImpl payService;

    @MockBean
    private IPaymentRepo payRepo;

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

        testGroup = CommonData.getTestGroup("_PaymentServiceImplUnitTest");
        testUsers = CommonData.getRegUsersForGroupWithSuffix("_PaymentServiceImplUnitTest");
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

        testGroup.setMembers(testMembers);
        testGroup.getMembers().get(ADMIN_INDEX).setAdmin(true);
        testGroup.setOwner(testGroup.getMembers().get(ADMIN_INDEX));
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
    @Order(1)
    void testSavePayment() throws PaymentHasNoAmountException, PaymentHasNoDateException, PaymentHasNoGroupException {

        Payment p = Payment.builder()
                .amount(25D)
                .group(testGroup)
                .member(testMembers.get(ADMIN_INDEX))
                .paymentDate(INIT_DATE).build();

        Mockito.when(
                payRepo.save(p)).thenReturn(p);

        Payment result = this.payService.savePayment(p);
        assertEquals(p, result);

    }

    @Test
    @Order(2)
    void testSavePaymentNoAmount() {
        Payment p = Payment.builder()
                .group(testGroup)
                .member(testMembers.get(ADMIN_INDEX))
                .paymentDate(INIT_DATE).build();

        Mockito.when(
                payRepo.save(p)).thenReturn(p);

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

        Mockito.when(
                payRepo.save(p)).thenReturn(p);

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

        Mockito.when(
                payRepo.save(p)).thenReturn(p);

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

        Mockito.when(
                payRepo.save(p)).thenReturn(p);

        assertThrows(PaymentHasNoDateException.class, () -> this.payService.savePayment(p));
    }

    @Test
    @Order(6)
    void testGetAllPaymentsByGroup() {
        Mockito.when(this.payRepo.findAllByGroupGroupIdOrderByPaymentDateAsc(testGroup.getGroupId()))
                .thenReturn(testPayments);

        List<Payment> result = this.payService.getAllPaymentsByGroup(testGroup.getGroupId());

        assertEquals(testPayments, result);

    }

    @Test
    @Order(7)
    void testGetAllPaymentsByGroupFilterDate() {
        List<Payment> expected = testPayments.stream().filter(
                p -> this.isDateBetween(p.getPaymentDate(), INIT_DATE, END_DATE))
                .toList();

        Mockito.when(this.payRepo.findAllByGroupGroupIdAndPaymentDateBetweenOrderByPaymentDateAsc(
                testGroup.getGroupId(), INIT_DATE, END_DATE))
                .thenReturn(expected);

        List<Payment> result = this.payService.getAllPaymentsByGroup(testGroup.getGroupId(), INIT_DATE, END_DATE);
        assertEquals(expected, result);
    }

    @Test
    @Order(8)
    void testGetAllPaymentsByMember() {
        final String name = testMembers.get(ADMIN_INDEX).getNickname();
        final Long memberId = testMembers.get(ADMIN_INDEX).getMemberId();
        List<Payment> expected = testPayments.stream().filter(
                p -> p.getMemberName().equals(name)).toList();

        Mockito.when(this.payRepo.findAllByMemberMemberIdOrderByPaymentDateAsc(memberId))
                .thenReturn(expected);

        List<Payment> result = this.payService.getAllPaymentsByMember(memberId);

        assertEquals(expected, result);
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

        Mockito.when(this.payRepo.findAllByMemberMemberIdOrderByPaymentDateAsc(memberId))
                .thenReturn(expected);

        List<Payment> result = this.payService.getAllPaymentsByMember(memberId);

        assertEquals(expected, result);
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
                    return 1L;
                }

            });
        }

        Mockito.when(this.payRepo.findAllTotalsByGroup(testGroup.getGroupId()))
                .thenReturn(expected);

        List<IPaymentTotal> result = this.payService.getAllPaymentTotals(testGroup.getGroupId());

        assertEquals(expected, result);

    }

    @Test
    @Order(11)
    void testGetAllPaymentTotalsFilterDate() {
        List<IPaymentTotal> expected = new ArrayList<>();
        List<String> keys = testPayments.stream().map(p -> p.getMemberName()).distinct().toList();
        for (String key : keys) {
            double sum = testPayments.stream()
                    .collect(
                            Collectors.summingDouble(p -> this.isDateBetween(p.getPaymentDate(), INIT_DATE, END_DATE) &&
                                    p.getMemberName().equals(key) ? p.getAmount() : 0));

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
                    return 1L;
                }

            });
        }

        Mockito.when(this.payRepo.findAllTotalsByGroupBetweenDates(testGroup.getGroupId(), INIT_DATE, END_DATE))
                .thenReturn(expected);

        List<IPaymentTotal> result = this.payService.getAllPaymentTotals(testGroup.getGroupId(), INIT_DATE, END_DATE);

        assertEquals(expected, result);

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
                return nickname;
            }

            @Override
            public Double getTotalAmount() {
                return sum;
            }

            @Override
            public Long getMemberId() {
                return 1L;
            }

        });

        Mockito.when(this.payRepo.findTotalsByMemberAndGroup(testGroup.getGroupId(), nickname))
                .thenReturn(expected);

        List<IPaymentTotal> result = this.payService.getMemberPaymentTotals(testGroup.getGroupId(), nickname);

        assertEquals(expected, result);

    }

    @Test
    @Order(13)
    void testGetMemberPaymentTotalsFixedDate() {
        List<IPaymentTotal> expected = new ArrayList<>();
        final String nickname = testMembers.get(ADMIN_INDEX).getNickname();

        double sum = testPayments.stream().collect(
                Collectors.summingDouble(
                        p -> this.isDateBetween(p.getPaymentDate(), INIT_DATE, END_DATE) &&
                        p.getMember().getNickname()
                        .equals(nickname) ? p.getAmount() : 0));

        expected.add(new IPaymentTotal() {

            @Override
            public String getNickname() {
                return nickname;
            }

            @Override
            public Double getTotalAmount() {
                return sum;
            }

            @Override
            public Long getMemberId() {
                return 1L;
            }

        });

        Mockito.when(this.payRepo.findTotalsByMemberAndGroupBetweenDates(testGroup.getGroupId(), nickname, INIT_DATE, END_DATE))
                .thenReturn(expected);

        List<IPaymentTotal> result = this.payService.getMemberPaymentTotals(testGroup.getGroupId(), nickname, INIT_DATE, END_DATE);

        assertEquals(expected, result);
    }

    @Test
    void testDeletePayment() {

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
}
