package cbs.wantACoffe.dto.payment;

import java.sql.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentModel {
    
    private Double amount;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date paymentDate;
    private Long groupId;
    private Long memberId;

}
