package cbs.wantACoffe.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cbs.wantACoffe.dto.payment.PaymentModel;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.Payment;
import cbs.wantACoffe.entity.RegisteredUser;
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
        if (memberPayed.getGroup().getGroupId() != paymentData.getGroupId()) {
            throw new MemberNotInGroup();
        }

        // SI el miembro que mete el pago en la app NO es admin
        // O SU id no es igual al id que queremos meter
        if (memberRequester.isAdmin() == false &&
                memberRequester.getId() != memberPayed.getId()) {
            throw new MemberIsNotAdmin();

        }

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

}
