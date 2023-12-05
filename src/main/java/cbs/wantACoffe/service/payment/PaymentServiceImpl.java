package cbs.wantACoffe.service.payment;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cbs.wantACoffe.entity.Payment;
import cbs.wantACoffe.repository.IPaymentRepo;

@Service
public class PaymentServiceImpl implements IPaymentService {

    @Autowired
    private IPaymentRepo repo;

    @Override
    public Payment savePayment(final Payment toSave) {
        return this.repo.save(toSave);
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
