package cbs.wantACoffe.service.payment;

import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import cbs.wantACoffe.dto.payment.IPaymentTotal;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.Payment;

@Service
public interface IPaymentService {
    
    // añadir pago -> DONE
    Payment savePayment(final Payment toSave);

    // eliminar pago -> lo dejaré para el final
    void deletePayment(final Long paymentId);

    // lista de todos los pagos
    // usaremos una clase custom para eso, no? o no?
    List<Payment> getAllPaymentsByGroup(final Long groupId);

    // lista de todos los pagos fecha_inicio - fecha_final -> DONE
    List<Payment> getAllPaymentsByGroup(final Long groupId, final Date startDate, final Date endDate);

    // lista de todos los pagos de un miembro -> DONE
    List<Payment> getAllPaymentsByMember(final Long memberId);

    // lista de todos los pagos de un miembro fechas limitadas -> DONE
    List<Payment> getAllPaymentsByMember(final Long memberId, final Date initDate, final Date endDate);

    // lista de los totales de cada usuario -> DONE
    List<IPaymentTotal> getAlIPaymentTotals(final Long groupId);

    // lista de los totales de cada usuario fecha_inicio - fecha_final
    List<IPaymentTotal> getAlIPaymentTotals(final Long groupId, final Date initDate, final Date endDate);

    // lista de los totales de un usuario
    
    // lista de todos los pagos fecha_inicio - fecha_final agrupados por usuario y sumados
}
