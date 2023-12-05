package cbs.wantACoffe.service.payment;

import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Service;

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

    // lista de todos los pagos fecha_inicio - fecha_final
    List<Payment> getAllPaymentsByInitEndDate(final Date startDate, final Date endDate);
    
    // lista de todos los pagos fecha_inicio - fecha_final agrupados por usuario y sumados

    // lista de todos los pagos por usuario X

    // totales por cada usuario?
}
