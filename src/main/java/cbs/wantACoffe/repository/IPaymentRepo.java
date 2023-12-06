package cbs.wantACoffe.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cbs.wantACoffe.entity.Payment;

@Repository
public interface IPaymentRepo extends JpaRepository<Payment, Long> {
    
    List<Payment> findAllByMemberMemberIdOrderByPaymentDateAsc(final Long memberId);

    List<Payment> findAllByMemberMemberIdAndPaymentDateBetweenOrderByPaymentDateAsc(final Long memberId, final Date startDate, final Date endDate);

    List<Payment> findAllByGroupGroupIdOrderByPaymentDateAsc(final Long groupId);

    List<Payment> findAllByGroupGroupIdAndPaymentDateBetweenOrderByPaymentDateAsc(final Long groupId, final Date startDate, final Date endDate);
}
