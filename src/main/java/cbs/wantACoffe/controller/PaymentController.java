package cbs.wantACoffe.controller;

import java.sql.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cbs.wantACoffe.dto.payment.PaymentsByUser;
import cbs.wantACoffe.dto.payment.PaymentsByUser.SimplePaymentData;
import cbs.wantACoffe.dto.payment.PaymentModel;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.Payment;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.GroupNotExistsException;
import cbs.wantACoffe.exceptions.InvalidTokenFormat;
import cbs.wantACoffe.exceptions.MemberIsNotAdmin;
import cbs.wantACoffe.exceptions.MemberNotInGroup;
import cbs.wantACoffe.exceptions.UserNotExistsException;
import cbs.wantACoffe.service.auth.IAuthService;
import cbs.wantACoffe.service.group.IGroupService;
import cbs.wantACoffe.service.group.IMemberService;
import cbs.wantACoffe.service.payment.IPaymentService;
import cbs.wantACoffe.util.AuthUtils;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("coffee/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final IAuthService authService;
    private final IPaymentService paymentService;
    private final IGroupService groupService;
    private final IMemberService memberService;

    private final Logger log = LoggerFactory.getLogger(PaymentController.class);

    @PutMapping("add")
    public ResponseEntity<String> addPayment(
            @RequestHeader(AuthUtils.HEADER_AUTH_TXT) String token,
            @RequestBody PaymentModel paymentData)
            throws InvalidTokenFormat, UserNotExistsException, MemberNotInGroup, MemberIsNotAdmin {

        // user who makes the payment
        log.info("User trying to make a payment");
        // Bien, este es el usuario que mete el pago en la app
        RegisteredUser userPayed = this.authService.getUserByToken(token);

        // a partir de este usuario, pillamos su membresía en el grupo determinado
        Member memberRequester = this.memberService.findMemberByGroupIdAndRegUserId(
                paymentData.getGroupId(),
                userPayed.getUserId());

        // y pillamos el miembro que paga, NO tiene porque ser el mismo miembro,
        // es decir, yo como X puedo poner que Y es el que paga, así yo soy
        // memberRequester y meto el pago de memberPayed
        Member memberPayed = this.memberService.findMemberById(paymentData.getMemberId());

        // comprobamos que el miembro de pago esté en el grupo donde queremos meter pago
        // nunca está de más
        log.info("Checking if user '{}' who inserts the payment is in the selected group", userPayed.getUserId());
        if (memberPayed.getGroup().getGroupId() != paymentData.getGroupId()) {
            throw new MemberNotInGroup();
        }

        // SI el miembro que mete el pago en la app NO es admin
        // O SU id no es igual al id que queremos meter
        log.info("Checking if user '{}' who inserts the payment is admin of the group", userPayed.getUserId());
        if (memberRequester.isAdmin() == false &&
                memberRequester.getId() != memberPayed.getId()) {
            throw new MemberIsNotAdmin();
        }

        log.info("User '{}' is saving a payment", userPayed.getUserId());
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

        log.info("Getting requester member");
        // pillamos el miembro que hace la petición
        // oju! este es el miembro que hace petición y, por lo tanto, usamos el token de
        // logeo.
        Member requestMember = this.memberService.findMemberByGroupIdAndRegUserId(
                groupId,
                this.authService.getUserByToken(token).getUserId());
        
        log.info("Getting member whose pays want to know");
        // pillamos el miembro de quien queremos saber los pagos
        Member payedMember = this.memberService.findMemberById(userId);

        // comprobamos que el grupo del miembro es el mismo que el grupo del requester y
        // el que sale en el json
        // quizás no sea necesario, pero de esta manera me ahorro bugs de que cualquiera
        // pueda ver
        // los otros grupos cambiando partes del código
        log.info("Checking if requesterMember is in the same group as the payedMember");
        if (payedMember.getGroup() != requestMember.getGroup()) {
            throw new MemberNotInGroup();
        }

        // si no hay fechas, probamos con este
        final List<Payment> payments;
        if (initDate == null) {
            log.info("No dates, so looking for all the payments");
            payments = this.paymentService.getAllPaymentsByMember(payedMember);
        } else {
            payments = null; // no haría falta, pero bueno
        }

        // transformo a clase
        // pagos de usuario X
        // get <- id_user, id_del usuario... podemos usar el mismo para las fechas y tal
        // (fetén)
        // return -> Nickname, [Amount, Date]
        PaymentsByUser paymentByUser = PaymentsByUser.builder().nickname(payedMember.getNickname()).build();
        paymentByUser.setPaymentData(payments.stream()
                .map(payment -> 
                    paymentByUser.new SimplePaymentData(payment.getAmount(), payment.getPaymentDate()))
                .toList());

        return ResponseEntity.ok(paymentByUser);
    }

    // pagos de todo el mundo: (fechas determinadas también)
    // return -> Amount, Date, nickname, is_member

    // pagos totales por persona: (fechas determinadas también)
    // return -> Total_amount, nickname, is_member

}
