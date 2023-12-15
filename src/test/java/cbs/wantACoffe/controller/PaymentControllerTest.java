package cbs.wantACoffe.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import cbs.wantACoffe.CommonData;
import cbs.wantACoffe.dto.MemberGroup;
import cbs.wantACoffe.dto.group.CreateGroup;
import cbs.wantACoffe.dto.group.GroupModel;
import cbs.wantACoffe.dto.payment.PaymentData;
import cbs.wantACoffe.dto.payment.PaymentModel;
import cbs.wantACoffe.dto.payment.PaymentsByUser;
import cbs.wantACoffe.dto.payment.SimplePaymentData;
import cbs.wantACoffe.dto.user.RegisteredUserToken;
import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.GroupNotExistsException;
import cbs.wantACoffe.exceptions.MemberIsNotAdmin;
import cbs.wantACoffe.exceptions.MemberNotInGroup;
import cbs.wantACoffe.exceptions.PaymentHasNoAmountException;
import cbs.wantACoffe.exceptions.PaymentHasNoDateException;
import cbs.wantACoffe.exceptions.PaymentHasNoGroupException;
import cbs.wantACoffe.exceptions.UserNotExistsException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("h2_test")
@TestMethodOrder(OrderAnnotation.class)
public class PaymentControllerTest {

    @LocalServerPort
    private int PORT;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String IP = "http://localhost";
    private static final String API = "/coffee/api";
    private static String address;
    private static HttpHeaders headerAdmin;
    private static HttpHeaders headerNoAdmin;

    // creo que no los necesitamos
    private static RegisteredUser userAdmin;
    private static RegisteredUser userNoAdmin;
    private static long groupId;

    private static final Date INIT_DATE = Date.valueOf("2222-07-05");
    private static final Date END_DATE = Date.valueOf("2222-07-15");
    private static final Date BEFORE_INIT_DATE = Date.valueOf("2222-07-04");
    private static final Date AFTER_END_DATE = Date.valueOf("2222-07-16");

    private static MemberGroup[] members;

    @Test
    @Order(1)
    void init() throws URISyntaxException {
        final String registerPath = "/auth/p/register";
        address = IP + ":" + String.valueOf(PORT) + API;
        URI uri = new URI(address + registerPath);

        // user admin
        userAdmin = CommonData.getTestUserWithSuffix("_PaymentControllerTest");
        ResponseEntity<RegisteredUserToken> resAdminToken = this.restTemplate.postForEntity(uri, userAdmin,
                RegisteredUserToken.class);

        // nunca está de más chequear
        assertEquals(HttpStatus.OK, resAdminToken.getStatusCode());

        headerAdmin = new HttpHeaders();
        headerAdmin.add("Authorization",
                resAdminToken.getBody().getHead().concat(" ")
                        .concat(resAdminToken.getBody().getToken()));

        // user no admin
        userNoAdmin = CommonData.getTestUserWithSuffix("_NO_ADMIN_PaymentControllerTest");
        ResponseEntity<RegisteredUserToken> resNoAdminToken = this.restTemplate.postForEntity(uri, userNoAdmin,
                RegisteredUserToken.class);

        headerNoAdmin = new HttpHeaders();
        headerNoAdmin.add("Authorization",
                resNoAdminToken.getBody().getHead().concat(" ")
                        .concat(resNoAdminToken.getBody().getToken()));

        // voy a meter un grupo y un miembro y bla bla bla

        Group g = CommonData.getTestGroup("_PaymentControllerTest");
        uri = new URI(address + "/groups/add/group");

        RequestEntity<CreateGroup> reqGroup = RequestEntity.post(uri)
                .headers(headerAdmin)
                .accept(MediaType.APPLICATION_JSON)
                .body(CreateGroup.builder()
                        .groupName(g.getGroupName())
                        .memberName("TestOwner")
                        .build());

        ResponseEntity<GroupModel> response = this.restTemplate.exchange(reqGroup, GroupModel.class);

        // nunca está de más chequear
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        groupId = response.getBody().getId();

        // metemos un par de miembros más? sip
        MemberGroup memberNoAdmin = MemberGroup.builder()
                .groupId(groupId)
                .nickname("segundin")
                .username(userNoAdmin.getUsername())
                .build();

        uri = new URI(address + "/groups/add/member");
        RequestEntity<MemberGroup> reqMember = RequestEntity.post(uri)
                .accept(MediaType.APPLICATION_JSON)
                .headers(headerAdmin)
                .body(memberNoAdmin);

        ResponseEntity<String> resMember = this.restTemplate.exchange(reqMember, String.class);

        // de nuevo, chequear está bien
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        // pillamos todos los miembros (2) del grupo a ver
        uri = new URI(address + "/groups/get/members/group/" + groupId);
        RequestEntity<Void> reqGetMembers = RequestEntity.get(uri).headers(headerAdmin).build();

        ResponseEntity<MemberGroup[]> resGetMembs = restTemplate.exchange(reqGetMembers, MemberGroup[].class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        members = resGetMembs.getBody();

    }

    @Test
    @Order(2)
    void testAddPaymentAdmin() throws URISyntaxException {
        URI uri = new URI(this.getUriString(UriType.ADD_PAY));

        // el admin meterá pagos para todo el mundo, tanto para él como para otro
        // miembro
        for (MemberGroup m : members) {
            RequestEntity<PaymentModel> request = this.createPayModel(
                    uri, headerAdmin, 27.5D, INIT_DATE, groupId, m.getUserId());

            ResponseEntity<String> response = this.restTemplate.exchange(request, String.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        // meteremos pagos en fecha final
        for (MemberGroup m : members) {
            RequestEntity<PaymentModel> request = this.createPayModel(
                    uri, headerAdmin, 21.5D, END_DATE, groupId, m.getUserId());

            ResponseEntity<String> response = this.restTemplate.exchange(request, String.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        // pagos después de las fechas que usamos de test
        for (MemberGroup m : members) {
            RequestEntity<PaymentModel> request = this.createPayModel(
                    uri, headerAdmin, 7.22D, BEFORE_INIT_DATE, groupId, m.getUserId());

            ResponseEntity<String> response = this.restTemplate.exchange(request, String.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        for (MemberGroup m : members) {
            RequestEntity<PaymentModel> request = this.createPayModel(
                    uri, headerAdmin, 666D, AFTER_END_DATE, groupId, m.getUserId());

            ResponseEntity<String> response = this.restTemplate.exchange(request, String.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    @Order(3)
    void testAddPaymentNoAdminSelf() throws URISyntaxException {
        URI uri = new URI(this.getUriString(UriType.ADD_PAY));

        // los no admin meterán pagos sólo para ellos
        for (MemberGroup m : members) {
            if (m.getIsAdmin()) {
                continue;
            }

            RequestEntity<PaymentModel> request = this.createPayModel(
                    uri, headerNoAdmin, 45.4D, END_DATE, groupId, m.getUserId());

            ResponseEntity<String> response = this.restTemplate.exchange(request, String.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    private RequestEntity<PaymentModel> createPayModel(URI uri, HttpHeaders header, double amount, Date payDate,
            long groupId, long memberId) {
        return RequestEntity
                .put(uri)
                .headers(header)
                .body(PaymentModel.builder()
                        .amount(25D)
                        .paymentDate(INIT_DATE)
                        .groupId(groupId)
                        .memberId(memberId)
                        .build());
    }

    @Test
    @Order(4)
    void testAddPaymentNotInGroup() throws URISyntaxException {
        URI uri = new URI(this.getUriString(UriType.ADD_PAY));

        RequestEntity<PaymentModel> request = RequestEntity
                .put(uri)
                .headers(headerAdmin)
                .body(PaymentModel.builder()
                        .amount(25D)
                        .paymentDate(INIT_DATE)
                        .groupId(55555L)
                        .memberId(members[0].getUserId())
                        .build());

        ResponseEntity<MemberNotInGroup> resp = this.restTemplate.exchange(request, MemberNotInGroup.class);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

    }

    @Test
    @Order(5)
    void testAddPaymentNotAdminTryAddPayment() throws URISyntaxException {
        URI uri = new URI(this.getUriString(UriType.ADD_PAY));

        RequestEntity<PaymentModel> request = RequestEntity
                .put(uri)
                .headers(headerNoAdmin)
                .body(PaymentModel.builder()
                        .amount(25D)
                        .paymentDate(INIT_DATE)
                        .groupId(55555L)
                        .memberId(members[0].getUserId())
                        .build());

        ResponseEntity<MemberIsNotAdmin> resp = this.restTemplate.exchange(request, MemberIsNotAdmin.class);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

    }

    @Test
    @Order(6)
    void testAddPaymentAdminNoDate() throws URISyntaxException {
        URI uri = new URI(this.getUriString(UriType.ADD_PAY));

        RequestEntity<PaymentModel> request = RequestEntity
                .put(uri)
                .headers(headerAdmin)
                .body(PaymentModel.builder()
                        .amount(25D)
                        .groupId(groupId)
                        .memberId(members[0].getUserId())
                        .build());

        ResponseEntity<PaymentHasNoDateException> resp = this.restTemplate.exchange(request,
                PaymentHasNoDateException.class);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

    }

    @Test
    @Order(7)
    void testAddPaymentAdminNoAmount() throws URISyntaxException {
        URI uri = new URI(this.getUriString(UriType.ADD_PAY));

        RequestEntity<PaymentModel> request = RequestEntity
                .put(uri)
                .headers(headerAdmin)
                .body(PaymentModel.builder()
                        .paymentDate(END_DATE)
                        .groupId(groupId)
                        .memberId(members[0].getUserId())
                        .build());

        ResponseEntity<PaymentHasNoAmountException> resp = this.restTemplate.exchange(request,
                PaymentHasNoAmountException.class);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

    }

    @Test
    @Order(8)
    void testAddPaymentAdminNoGroup() throws URISyntaxException {
        URI uri = new URI(this.getUriString(UriType.ADD_PAY));

        RequestEntity<PaymentModel> request = RequestEntity
                .put(uri)
                .headers(headerAdmin)
                .body(PaymentModel.builder()
                        .amount(25D)
                        .paymentDate(AFTER_END_DATE)
                        .memberId(members[0].getUserId())
                        .build());

        ResponseEntity<PaymentHasNoGroupException> resp = this.restTemplate.exchange(request,
                PaymentHasNoGroupException.class);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    @Order(9)
    void testGetUserPayments() throws URISyntaxException {
        final String nickname = members[0].getNickname();
        final Long id = members[0].getGroupId();

        URI uri = new URI(this.getUriString(UriType.GET_USER_PAY)
                .concat("?userId=").concat(id.toString())
                .concat("&groupId=").concat(String.valueOf(groupId)));
        RequestEntity<Void> request = RequestEntity.get(uri).headers(headerNoAdmin).build();

        ResponseEntity<PaymentsByUser> resp = this.restTemplate.exchange(request, PaymentsByUser.class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());

        // sabemos que tenemos pagos, vamos a ver que la lista no está ni vacía
        // y que todos son del user determinado
        assertTrue(resp.getBody() != null);
        assertTrue(resp.getBody().getPaymentData().size() > 0);
        assertEquals(resp.getBody().getNickname(), nickname);
    }

    @Test
    @Order(10)
    void testGetUserPaymentsNoUserID() throws URISyntaxException {
        URI uri = new URI(this.getUriString(UriType.GET_USER_PAY)
                .concat("&groupId=").concat(String.valueOf(groupId)));
        RequestEntity<Void> request = RequestEntity.get(uri).headers(headerNoAdmin).build();

        ResponseEntity<UserNotExistsException> resp = this.restTemplate.exchange(request, UserNotExistsException.class);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    @Order(11)
    void testGetUserPaymentsNoGroupId() throws URISyntaxException {

        final Long id = members[0].getGroupId();
        URI uri = new URI(this.getUriString(UriType.GET_USER_PAY)
                .concat("?userId=").concat(id.toString()));

        RequestEntity<Void> request = RequestEntity.get(uri).headers(headerNoAdmin).build();

        ResponseEntity<GroupNotExistsException> resp = this.restTemplate.exchange(request,
                GroupNotExistsException.class);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    @Order(12)
    void testGetUserPaymentsDate() throws URISyntaxException {

        final String nickname = members[0].getNickname();
        final Long id = members[0].getGroupId();
        URI uri = new URI(this.getUriString(UriType.GET_USER_PAY)
                .concat("?userId=").concat(id.toString())
                .concat("&groupId=").concat(String.valueOf(groupId))
                .concat("&initDate").concat(INIT_DATE.toString())
                .concat("&endDate=").concat(END_DATE.toString()));

        RequestEntity<Void> request = RequestEntity.get(uri).headers(headerNoAdmin).build();

        ResponseEntity<PaymentsByUser> resp = this.restTemplate.exchange(request, PaymentsByUser.class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());

        // sabemos que tenemos pagos, vamos a ver que la lista no está ni vacía
        // y que todos son del user determinado
        assertTrue(resp.getBody() != null);
        assertTrue(resp.getBody().getPaymentData().size() > 0);
        assertEquals(resp.getBody().getNickname(), nickname);

        // miramos que las fechas sean correctas
        for (SimplePaymentData payment : resp.getBody().getPaymentData()) {
            assertTrue(
                    payment.getPaymentDate().compareTo(INIT_DATE) >= 0 &&
                            payment.getPaymentDate().compareTo(END_DATE) <= 0);
        }
    }

    @Test
    @Order(13)
    void testGetUserPaymentsDateNoUserId() throws URISyntaxException {
        URI uri = new URI(this.getUriString(UriType.GET_USER_PAY)
                .concat("&groupId=").concat(String.valueOf(groupId))
                .concat("&initDate").concat(INIT_DATE.toString())
                .concat("&endDate=").concat(END_DATE.toString()));

        RequestEntity<Void> request = RequestEntity.get(uri).headers(headerNoAdmin).build();

        ResponseEntity<UserNotExistsException> resp = this.restTemplate.exchange(request, UserNotExistsException.class);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    @Order(14)
    void testGetUserPaymentsDateNoGroup() throws URISyntaxException {
        final Long id = members[0].getGroupId();
        URI uri = new URI(this.getUriString(UriType.GET_USER_PAY)
                .concat("?userId=").concat(id.toString())
                .concat("&initDate").concat(INIT_DATE.toString())
                .concat("&endDate=").concat(END_DATE.toString()));

        RequestEntity<Void> request = RequestEntity.get(uri).headers(headerNoAdmin).build();

        ResponseEntity<GroupNotExistsException> resp = this.restTemplate.exchange(request,
                GroupNotExistsException.class);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

    }

    @Test
    @Order(15)
    void testGetGroupPayments() throws URISyntaxException {
        URI uri = new URI(this.getUriString(UriType.GET_GROUP_PAY)
                .concat("?groupId=").concat(String.valueOf(groupId)));

        RequestEntity<Void> request = RequestEntity.get(uri).headers(headerAdmin).build();

        ResponseEntity<PaymentData[]> response = this.restTemplate.exchange(request, PaymentData[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // hemos metido cosas, así que miramos que el tamaño sea mayor de 0
        assertTrue(response.hasBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    @Order(16)
    void testGetGroupPaymentsFixedDate() throws URISyntaxException {
        URI uri = new URI(this.getUriString(UriType.GET_GROUP_PAY)
                .concat("?groupId=").concat(String.valueOf(groupId))
                .concat("&initDate=").concat(INIT_DATE.toString())
                .concat("&endDate=").concat(END_DATE.toString()));

        RequestEntity<Void> request = RequestEntity.get(uri).headers(headerAdmin).build();

        ResponseEntity<PaymentData[]> response = this.restTemplate.exchange(request, PaymentData[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // hemos metido cosas, así que miramos que el tamaño sea mayor de 0
        assertTrue(response.hasBody());
        assertTrue(response.getBody().length > 0);

        // miramos que las fechas estén entremedias
        for (int i = 0; i < response.getBody().length; i++) {
            PaymentData p = response.getBody()[i];
            assertTrue(p.getDate().compareTo(INIT_DATE) >= 0 &&
                    p.getDate().compareTo(END_DATE) <= 0);
        }
    }

    @Test
    @Order(17)
    void testDeletePayment() throws URISyntaxException {

        // pillaremos los pagos
        // feo, es un copia y pega del test
        URI uriPay = new URI(this.getUriString(UriType.GET_GROUP_PAY)
                .concat("?groupId=").concat(String.valueOf(groupId)));

        RequestEntity<Void> request = RequestEntity.get(uriPay).headers(headerAdmin).build();

        ResponseEntity<PaymentData[]> resPay = this.restTemplate.exchange(request, PaymentData[].class);
        assertEquals(HttpStatus.OK, resPay.getStatusCode());

        // no está de más comprobar, aún siendo copia y pega
        assertTrue(resPay.hasBody());
        assertTrue(resPay.getBody().length > 0);
        PaymentData[] data = resPay.getBody();

        // borramos todos
        for (int i = 0; i < data.length; i++) {
            URI uriDel = new URI(this.getUriString(UriType.DELETE_PAY)
                    .concat("?groupId=").concat(String.valueOf(groupId))
                    .concat("&paymentId=").concat(String.valueOf(data[i].getPaymentId())));
        
            request = RequestEntity.delete(uriDel).headers(headerAdmin).build();
            ResponseEntity<String> resDelete = this.restTemplate.exchange(request, String.class);

            assertEquals(HttpStatus.OK, resDelete.getStatusCode());
        }

        // comprobamos que no hay ninguno, de nuevo, copia y pega
        request = RequestEntity.get(uriPay).headers(headerAdmin).build();
        resPay = this.restTemplate.exchange(request, PaymentData[].class);
        assertEquals(HttpStatus.OK, resPay.getStatusCode());
        assertTrue(resPay.getBody().length == 0);

    }

    enum UriType {
        ADD_PAY, GET_USER_PAY, GET_GROUP_PAY, DELETE_PAY
    }

    private String getUriString(final UriType type) throws URISyntaxException {
        return switch (type) {
            case ADD_PAY -> address + "/payments/add";
            case GET_USER_PAY -> address + "/payments/get/by/user";
            case GET_GROUP_PAY -> address + "/payments/get/by/group";
            case DELETE_PAY -> address + "/payments/delete";
        };
    }
}
