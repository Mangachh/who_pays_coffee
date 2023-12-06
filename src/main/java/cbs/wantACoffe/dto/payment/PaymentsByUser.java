package cbs.wantACoffe.dto.payment;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentsByUser {
    
    private String nickname;
    @Builder.Default
    private List<SimplePaymentData> paymentData = new ArrayList<>();

    @Data
    @AllArgsConstructor
    public class SimplePaymentData{ // esto será su propia clase, pero lo dejo por aquí así
        private Double amount;
        private Date paymentDate;
    }
}
