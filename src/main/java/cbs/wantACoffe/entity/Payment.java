package cbs.wantACoffe.entity;

import java.sql.Date;

import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad para los pagos.
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Payment.TABLE_NAME,
        indexes = {
                // index de ID GRUPO
                @Index(name = "idx_payment_group_id", columnList = Payment.COLUMN_GROUP_ID_NAME),
                //index de ID GRUPO por FECHA
                @Index(name = "idx_payment_group_id_date", columnList = Payment.COLUMN_GROUP_ID_NAME + ", "
                        + Payment.COLUMN_DATE_NAME),
                // index por FECHA
                @Index(name = "idx_payment_date", columnList = Payment.COLUMN_DATE_NAME),
                // index por ID MIEMBRO
                @Index(name = "idx_payment_member_id", columnList = Payment.COLUMN_MEMBER_PAYED_ID_NAME),
                // index por ID GRUPO y ID MIEMBRO 
                @Index(name = "idx_payment_group_id_member_id", columnList = Payment.COLUMN_GROUP_ID_NAME + ", " +
                        Payment.COLUMN_MEMBER_PAYED_ID_NAME)
                // creo que con estos indexs vamos sobrados, no?
    
})
public class Payment {

    public final static String TABLE_NAME = "payments";
    public final static String COLUMN_ID_NAME = "payment_id";
    public final static String COLUMN_AMOUNT_NAME = "amount";
    public final static String COLUMN_DATE_NAME = "payment_date";
    public final static String COLUMN_GROUP_ID_NAME = "group_id";
    public final static String COLUMN_MEMBER_PAYED_ID_NAME = "member_id";
    public final static String COLUMN_MEMBER_PAYED_NAME_NAME = "member_name";

    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_id_generator")
    @SequenceGenerator(sequenceName = "PaymentId", name = "payment_id_generator", allocationSize = 1)
    @Column(name = COLUMN_ID_NAME) 
    private Long id;

    @Column(name = COLUMN_AMOUNT_NAME, nullable = false)
    private Double amount;

    @Column(name = COLUMN_DATE_NAME, nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")

    private Date paymentDate;

    @ManyToOne
    @JoinColumn(name = COLUMN_GROUP_ID_NAME, nullable = false, updatable = false)
    private Group group;

    @Column(name = COLUMN_MEMBER_PAYED_NAME_NAME)
    private String memberName;

    @ManyToOne
    @JoinColumn(name = COLUMN_MEMBER_PAYED_ID_NAME, nullable = true)
    private Member member;

    @Transient
    @Builder.ObtainVia(method = "isMemberActive")
    private boolean isActive;

    public boolean isMemberActive() {
        return this.member != null;
    }
    
}
