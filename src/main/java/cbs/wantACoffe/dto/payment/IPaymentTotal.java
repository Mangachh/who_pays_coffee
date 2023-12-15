package cbs.wantACoffe.dto.payment;

/**
 * Interfaz para pillar los totales de pagos. Usado en {@link #IPaymentRepo}
 */
public interface IPaymentTotal {
    
    String getNickname();

    Double getTotalAmount();

    Long getMemberId();


}
