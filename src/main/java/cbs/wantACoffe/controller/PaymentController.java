package cbs.wantACoffe.controller;

import java.sql.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cbs.wantACoffe.dto.payment.PaymentsByUser;
import cbs.wantACoffe.dto.payment.SimplePaymentData;
import cbs.wantACoffe.dto.payment.IPaymentTotal;
import cbs.wantACoffe.dto.payment.PaymentData;
import cbs.wantACoffe.dto.payment.PaymentModel;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.Payment;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.GroupNotExistsException;
import cbs.wantACoffe.exceptions.InvalidTokenFormat;
import cbs.wantACoffe.exceptions.MemberIsNotAdmin;
import cbs.wantACoffe.exceptions.MemberNotInGroup;
import cbs.wantACoffe.exceptions.PaymentHasNoAmountException;
import cbs.wantACoffe.exceptions.PaymentHasNoDateException;
import cbs.wantACoffe.exceptions.PaymentHasNoGroupException;
import cbs.wantACoffe.exceptions.UserNotExistsException;
import cbs.wantACoffe.service.auth.IAuthService;
import cbs.wantACoffe.service.group.IMemberService;
import cbs.wantACoffe.service.payment.IPaymentService;
import cbs.wantACoffe.util.AuthUtils;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
/**
 * Controlador de los pagos.
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public class PaymentController {

    private final IAuthService authService;
    private final IPaymentService paymentService;
    private final IMemberService memberService;

    private final Logger log = LoggerFactory.getLogger(PaymentController.class);

    /**
     * Añade un pago a la base de datos. El requester TIENE que pertenecer al grupo
     * en el que se mete el pago y ADEMÁS ó es admin y puede meter el pago de
     * cualquier
     * usuario Ó mete su propio pago.
     * Un member no admin no puede meter un pago que no es el suyo
     * 
     * @param token       -> token de sesión
     * @param paymentData -> {@link #PaymentModel} datos de pago
     * @return -> string con mensaje
     * @throws InvalidTokenFormat
     * @throws UserNotExistsException
     * @throws MemberNotInGroup
     * @throws MemberIsNotAdmin
     * @throws PaymentHasNoAmountException
     * @throws PaymentHasNoGroupException
     * @throws PaymentHasNoDateException
     */
    @PutMapping("add")
    public ResponseEntity<String> addPayment(
            @RequestHeader(AuthUtils.HEADER_AUTH_TXT) String token,
            @RequestBody PaymentModel paymentData)
            throws InvalidTokenFormat, UserNotExistsException, MemberNotInGroup, MemberIsNotAdmin, PaymentHasNoAmountException, PaymentHasNoDateException, PaymentHasNoGroupException {

        // user who makes the payment

        // Bien, este es el usuario que mete el pago en la app
        RegisteredUser userPayed = this.authService.getUserByToken(token);
        log.info("User {} trying to make a payment", userPayed.getUsername());

        // a partir de este usuario, pillamos su membresía en el grupo determinado
        Member memberRequester = this.memberService.getMemberByGroupIdAndRegUserId(
                paymentData.getGroupId(),
                userPayed.getUserId());

        // y pillamos el miembro que paga, NO tiene porque ser el mismo miembro,
        // es decir, yo como X puedo poner que Y es el que paga, así yo soy
        // memberRequester y meto el pago de memberPayed
        Member memberPayed = this.memberService.getMemberById(paymentData.getMemberId());

        // comprobamos que el miembro de pago esté en el grupo donde queremos meter pago
        // nunca está de más
        log.info("Checking if member '{}' who inserts the payment is in the selected group", userPayed.getUserId());
        if (memberPayed.getGroup().getGroupId().equals(paymentData.getGroupId()) == false) {
            throw new MemberNotInGroup();
        }

        // SI el miembro que mete el pago en la app NO es admin
        // O SU id no es igual al id que queremos meter
        log.info("Checking if user '{}' who inserts the payment is admin of the group", userPayed.getUserId());
        if (memberRequester.isAdmin() == false &&
                memberRequester.getMemberId().equals(memberPayed.getMemberId()) == false) {
            throw new MemberIsNotAdmin();
        }

        log.info("Member '{}' is saving a payment", userPayed.getUserId());
        // guardamos
        this.paymentService.savePayment(
                Payment.builder()
                        .group(memberPayed.getGroup())
                        .amount(paymentData.getAmount())
                        .paymentDate(paymentData.getPaymentDate())
                        .member(memberPayed)
                        .memberName(memberPayed.getNickname())
                        .build());

        // do payment or error
        return ResponseEntity.ok("Payment done");
    }

    /**
     * Pilla todos los pagos hechos por un miembro determinado
     * en un grupo determinado.
     * La verdad, el id de grupo nos lo podríamos saltar, aún así
     * está bien ponerlo porque de esta manera tenemos un chequeo más
     * y, por ejemplo, si quien quiere pillar los pagos NO pertenece al grupo,
     * por mucho que el miembro "userId" sí lo haga, no los devuelve.
     * Es una buena manera de proteger os datos
     * 
     * @param token    -> token de sesión
     * @param userId   -> id
     * @param groupId  -> id del grupo donde está el requester
     * @param initDate -> opcional. Fecha de inicio de los pagos.
     * @param endDate  -> opcional. Fecha final de los pagos
     * @return -> lista de pagos
     * @throws UserNotExistsException
     * @throws GroupNotExistsException
     * @throws InvalidTokenFormat
     * @throws MemberNotInGroup
     */
    @GetMapping("get/by/user")
    public ResponseEntity<PaymentsByUser> getUserPayments(
            @RequestHeader(AuthUtils.HEADER_AUTH_TXT) String token,
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "groupId") Long groupId,
            @RequestParam(name = "initDate", required = false) Date initDate,
            @RequestParam(name = "endDate", required = false) Date endDate)
            throws UserNotExistsException, GroupNotExistsException, InvalidTokenFormat, MemberNotInGroup {
        log.info("Getting payments by member");
        // checkers????
        if (userId == null) {
            throw new UserNotExistsException(); // de momento dejamos esta
        }

        if (groupId == null) {
            throw new GroupNotExistsException(); // lo mimsmo, dejamos esta
        }

        // pillamos el miembro que hace la petición
        // oju! este es el miembro que hace petición y, por lo tanto, usamos el token de
        // logeo.
        Member requestMember = this.getMemberByToken(groupId, token);

        // pillamos el miembro de quien queremos saber los pagos
        Member payedMember = this.memberService.getMemberById(userId);
        log.info("{} member wants to know the payments from {}",
                requestMember.getNickname(),
                payedMember.getNickname());

        // comprobamos que el grupo del miembro es el mismo que el grupo del requester y
        // el que sale en el json
        // quizás no sea necesario, pero de esta manera me ahorro bugs de que cualquiera
        // pueda ver
        // los otros grupos cambiando partes del código
        log.info("Checking if requesterMember {} is in the same group as the payedMember {}",
                requestMember.getNickname(), payedMember.getNickname());
        if (payedMember.getGroup() != requestMember.getGroup()) {
            throw new MemberNotInGroup();
        }

        // si no hay fechas, probamos con este
        final List<Payment> payments;
        if (initDate == null || endDate == null) {
            log.info("No dates, so looking for all the payments");
            payments = this.paymentService.getAllPaymentsByMember(payedMember.getMemberId());
        } else {
            log.info("Dates found. Looking for payments between dates");
            payments = this.paymentService.getAllPaymentsByMember(payedMember.getMemberId(), initDate, endDate);
        }

        // transformo a clase
        // mmmmmm, quizás en el service?
        PaymentsByUser paymentByUser = PaymentsByUser.builder().nickname(payedMember.getNickname()).build();
        paymentByUser.setPaymentData(payments.stream()
                .map(payment -> new SimplePaymentData(
                        payment.getId(),
                        payment.getAmount(),
                        payment.getPaymentDate()))
                .toList());

        return ResponseEntity.ok(paymentByUser);
    }

    /**
     * Devuelve todos y cada uno de los pagos hechos en el grupo ordenados por
     * fecha.
     * 
     * 
     * @param token    -> token de sesión
     * @param groupId  -> id del grupo
     * @param initDate -> opcional. Fecha de inicio de los pagos
     * @param endDate  -> opcional. Fecha final de los pagos
     * @return -> lista de pagos
     * @throws MemberNotInGroup
     * @throws InvalidTokenFormat
     * @throws UserNotExistsException
     */
    @GetMapping("get/by/group")
    public ResponseEntity<List<PaymentData>> getGroupPayments(
            @RequestHeader(AuthUtils.HEADER_AUTH_TXT) String token,
            @RequestParam(name = "groupId") Long groupId,
            @RequestParam(name = "initDate", required = false) Date initDate,
            @RequestParam(name = "endDate", required = false) Date endDate)
            throws MemberNotInGroup, InvalidTokenFormat, UserNotExistsException {

        // get the requester member
        Member requesterMember = this.getMemberByToken(groupId, token);

        final List<Payment> payments;
        if (initDate == null || endDate == null) {
            log.info("No dates, so looking for all the payments");
            payments = this.paymentService.getAllPaymentsByGroup(groupId);
        } else {
            log.info("Dates found. Looking for payments between dates");
            payments = this.paymentService.getAllPaymentsByGroup(groupId, initDate, endDate);
        }

        // lo mismo no sé si meterlo aquí o en el service
        // o devolver esto en ul repo
        final List<PaymentData> paymentRes = payments.stream().map(
                pay -> PaymentData.builder()
                        .paymentId(pay.getId())
                        .nickname(pay.getMemberName())
                        .amount(pay.getAmount())
                        .date(pay.getPaymentDate())
                        .isMember(pay.isMemberActive())
                        .build())
                .toList();
        return ResponseEntity.ok(paymentRes);
    }

    /**
     * Devuelve el total de pagos de cada usuario. OJU! es el total y ya.
     * 
     * @param token    -> token de sesión
     * @param groupId  -> grupo donde están los pagos
     * @param initDate -> opcional. fecha inicio pagos
     * @param endDate  -> opcional. fecha final pagos
     * @return
     * @throws MemberNotInGroup
     * @throws InvalidTokenFormat
     * @throws UserNotExistsException
     */
    @GetMapping("get/totals/by/group")
    public ResponseEntity<List<IPaymentTotal>> getTotalPayments(
            @RequestHeader(AuthUtils.HEADER_AUTH_TXT) String token,
            @RequestParam(name = "groupId") Long groupId,
            @RequestParam(name = "initDate", required = false) Date initDate,
            @RequestParam(name = "endDate", required = false) Date endDate)
            throws MemberNotInGroup, InvalidTokenFormat, UserNotExistsException {

        // get the requester member
        Member requesterMember = this.getMemberByToken(groupId, token);

        List<IPaymentTotal> payments = null;
        if (initDate == null || endDate == null) {
            log.info("No dates, so looking for all the payments");
            payments = this.paymentService.getAllPaymentTotals(groupId);
        } else {
            log.info("Dates found. Looking for payments between dates");
            payments = this.paymentService.getAllPaymentTotals(groupId, initDate, endDate);
        }

        return ResponseEntity.ok().body(payments);

    }

    /**
     * Elimina un pago de un grupo determinado con un id determinado
     * @param token -> token de usuario
     * @param groupId -> grupo en el que se quita el pago
     * @param paymentId -> id del pago
     * @return
     * @throws MemberNotInGroup
     * @throws InvalidTokenFormat
     * @throws UserNotExistsException
     */
    @DeleteMapping("delete")
    public ResponseEntity<String> deletePayment(
            @RequestHeader(AuthUtils.HEADER_AUTH_TXT) String token,
            @RequestParam(name = "groupId") Long groupId,
            @RequestParam(name = "paymentId") Long paymentId)
            throws MemberNotInGroup, InvalidTokenFormat, UserNotExistsException {
        // get the requester member
        Member requesterMember = this.getMemberByToken(groupId, token);

        if (requesterMember.isAdmin()) {
            this.paymentService.deletePayment(paymentId);
        }

        return ResponseEntity.ok("Done");
    }

    
    /**
     * Pilla un miembro de un grupo a partir del token.
     * También comprueba si el miembro pertence al grupo determinado
     * De esta manera simplificamos un poco las cosas.
     * 
     * @param groupId -> grupo al que pertenece el miembro
     * @param token   -> token de sesión
     * @return
     * @throws MemberNotInGroup
     * @throws InvalidTokenFormat
     * @throws UserNotExistsException
     */
    private Member getMemberByToken(final Long groupId, final String token)
            throws MemberNotInGroup, InvalidTokenFormat, UserNotExistsException {

        Member member = this.memberService.getMemberByGroupIdAndRegUserId(
                groupId,
                this.authService.getUserByToken(token).getUserId());
        log.info("Member {} trying to get payments by group", member.getNickname());

        log.info("Checking if member {} is in group", member.getNickname());
        // check if member in group, again, better to be sure than sorry
        if (member.getGroup().getGroupId() != groupId) {
            throw new MemberNotInGroup();
        }
        return member;
    }

}
