package cbs.wantACoffe.service.payment;

import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import cbs.wantACoffe.dto.payment.IPaymentTotal;
import cbs.wantACoffe.entity.Payment;
import cbs.wantACoffe.exceptions.PaymentHasNoAmountException;
import cbs.wantACoffe.exceptions.PaymentHasNoDateException;
import cbs.wantACoffe.exceptions.PaymentHasNoGroupException;

/**
 * Interfaz de servicio de los pagos
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
@Service
public interface IPaymentService {
    
    /**
     * Añade un pago
     * @param toSave -> pago que guardar
     * @return
     * @throws PaymentHasNoAmountException
     * @throws PaymentHasNoDateException
     * @throws PaymentHasNoGroupException
     */
    Payment savePayment(final Payment toSave) throws PaymentHasNoAmountException, PaymentHasNoDateException, PaymentHasNoGroupException;

    /**
     * Elimina un pago de la base de datos
     * @param paymentId -> id del pago
     */
    void deletePayment(final Long paymentId);

    /**
     * Lista de todos los pagos hechos en un grupo
     * @param groupId -> id del grupo donde están los pagos
     * @return
     */
    List<Payment> getAllPaymentsByGroup(final Long groupId);

    /**
     * Lista de todos los pagos hechos en un grupo, delimitados por fechas
     * @param groupId -> id del grupo de los pagos
     * @param startDate -> fecha inicio
     * @param endDate -> fecha final
     * @return
     */
    List<Payment> getAllPaymentsByGroup(final Long groupId, final Date startDate, final Date endDate);

    /**
     * Lista con todos los pagos hechos por un miembro determinado
     * @param memberId -> id del miembro
     * @return
     */
    List<Payment> getAllPaymentsByMember(final Long memberId);

    /**
     * Lista con todos los pagos hechos por un miembro determinado entre unas fechas concretas
     * @param memberId -> id del miembro
     * @param initDate -> fecha inicial
     * @param endDate -> fecha final
     * @return
     */
    List<Payment> getAllPaymentsByMember(final Long memberId, final Date initDate, final Date endDate);

    /**
     * Lista de pagos totales por cada miembro de un grupo
     * @param groupId -> id del grupo
     * @return
     */
    List<IPaymentTotal> getAllPaymentTotals(final Long groupId);

    /**
     * Lista de pagos totales por cada miembro de un grupo entr dos fechas
     * @param groupId -> id del grupo
     * @param initDate -> fecha inicio
     * @param endDate -> fecha final
     * @return
     */
    List<IPaymentTotal> getAllPaymentTotals(final Long groupId, final Date initDate, final Date endDate);

    /**
     * Lista
     * @param groupId
     * @param memberNickname
     * @deprecated
     * @return
     */
    List<IPaymentTotal> getMemberPaymentTotals(final Long groupId, final String memberNickname);

    /**
     * @deprecated
     * @param groupId
     * @param memberNickname
     * @param initDate
     * @param endDate
     * @return
     */
    List<IPaymentTotal> getMemberPaymentTotals(final Long groupId, final String memberNickname, final Date initDate, final Date endDate);
}
