package cbs.wantACoffe.dto.payment;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que usamos para devolver los datos de pago.
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentData {
    private long paymentId;
    private String nickname;
    private Double amount;
    private Date date;
    private boolean isMember;
}
