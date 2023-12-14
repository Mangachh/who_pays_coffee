package cbs.wantACoffe.dto.payment;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que devuelve los pagos hechos por un miembro.
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentsByUser {
    
    private String nickname;

    @Builder.Default
    private List<SimplePaymentData> paymentData = new ArrayList<>();
    
}
