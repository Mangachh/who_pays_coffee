package cbs.wantACoffe.service.payment;

import java.sql.Date;
import java.util.List;

import cbs.wantACoffe.entity.Payment;

public class PaymentServiceImpl implements IPaymentService {
    

    @Override
    public Payment savePayment(Payment toSave) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'savePayment'");
    }

    @Override
    public void deletePayment(Long paymentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deletePayment'");
    }

    @Override
    public List<Payment> getAllPaymentsByGroup(Long groupId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllPaymentsByGroup'");
    }

    @Override
    public List<Payment> getAllPaymentsByInitEndDate(Date startDate, Date endDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllPaymentsByInitEndDate'");
    }
    
}
