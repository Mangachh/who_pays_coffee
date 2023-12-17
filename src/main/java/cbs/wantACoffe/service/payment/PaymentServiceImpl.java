package cbs.wantACoffe.service.payment;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cbs.wantACoffe.dto.payment.IPaymentTotal;
import cbs.wantACoffe.entity.Payment;
import cbs.wantACoffe.exceptions.PaymentHasNoAmountException;
import cbs.wantACoffe.exceptions.PaymentHasNoDateException;
import cbs.wantACoffe.exceptions.PaymentHasNoGroupException;
import cbs.wantACoffe.repository.IPaymentRepo;

@Service
public class PaymentServiceImpl implements IPaymentService {

    @Autowired
    private IPaymentRepo repo;

    @Override
    public Payment savePayment(final Payment toSave) throws PaymentHasNoAmountException, PaymentHasNoDateException, PaymentHasNoGroupException {

        // no amount exception
        if (toSave.getAmount() == null ||toSave.getAmount() <= 0) {
            throw new PaymentHasNoAmountException();
        }

        // no date exception
        if (toSave.getPaymentDate() == null) {
            throw new PaymentHasNoDateException();
        }

        // no group exception
        if (toSave.getGroup() == null) {
            throw new PaymentHasNoGroupException();
        }
        
        return this.repo.save(toSave);
    }

    @Override
    public void deletePayment(final Long paymentId) {
        this.repo.deleteById(paymentId);
    }

    @Override
    public List<Payment> getAllPaymentsByGroup(final Long groupId, final Date startDate, final Date endDate) {
        return this.repo.findAllByGroupGroupIdAndPaymentDateBetweenOrderByPaymentDateAsc(groupId, startDate, endDate);
    }

    @Override
    public List<Payment> getAllPaymentsByGroup(final Long groupId) {
        return this.repo.findAllByGroupGroupIdOrderByPaymentDateAsc(groupId);
    }


    @Override
    public List<Payment> getAllPaymentsByMember(Long memberId) {
        return this.repo.findAllByMemberMemberIdOrderByPaymentDateAsc(memberId);
    }

    @Override
    public List<Payment> getAllPaymentsByMember(Long memberId, Date initDate, Date endDate) {
        return this.repo.findAllByMemberMemberIdAndPaymentDateBetweenOrderByPaymentDateAsc(memberId, initDate, endDate);
    }

    @Override
    public List<IPaymentTotal> getAllPaymentTotals(Long groupId) {
        return this.repo.findAllTotalsByGroup(groupId);
    }

    @Override
    public List<IPaymentTotal> getAllPaymentTotals(Long groupId, Date initDate, Date endDate) {
        return this.repo.findAllTotalsByGroupBetweenDates(groupId, initDate, endDate);
    }

    @Override
    public List<IPaymentTotal> getMemberPaymentTotals(final Long groupId, final String memberNickname) {
        return this.repo.findTotalsByMemberAndGroup(groupId, memberNickname);
    }
    
    @Override
    public List<IPaymentTotal> getMemberPaymentTotals(Long groupId, final String memberNickname, Date initDate, Date endDate) {
        return this.repo.findTotalsByMemberAndGroupBetweenDates(groupId, memberNickname, initDate, endDate);
    }

    
    
}
