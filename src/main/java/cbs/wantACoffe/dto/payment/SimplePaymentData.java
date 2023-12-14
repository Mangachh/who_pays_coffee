package cbs.wantACoffe.dto.payment;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimplePaymentData { // esto será su propia clase, pero lo dejo por aquí así
    private Long paymentId;
    private Double amount;
    private Date paymentDate;
}