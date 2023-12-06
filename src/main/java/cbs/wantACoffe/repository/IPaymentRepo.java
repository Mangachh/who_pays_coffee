package cbs.wantACoffe.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.Payment;

@Repository
public interface IPaymentRepo extends JpaRepository<Payment, Long> {
    
    List<Payment> findAllByMember(final Member member);

    List<Payment> findAllByMemberAndPaymentDateBetween(final Member member, final Date startDate, final Date endDate);
}
