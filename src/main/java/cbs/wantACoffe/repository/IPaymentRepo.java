package cbs.wantACoffe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cbs.wantACoffe.entity.Payment;

@Repository
public interface IPaymentRepo extends JpaRepository<Payment, Long>{
    
}
