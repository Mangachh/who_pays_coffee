package cbs.wantACoffe.dto.payment;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentData {
    private String nickname;
    private Double amount;
    private Date date;
    private boolean isMember;
}
