package com.javi.deckup.repository.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.javi.deckup.repository.entity.Payment;

import jakarta.transaction.Transactional;

@Transactional
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>{

	@Query(value = "SELECT * FROM payments WHERE orderid = ?1", nativeQuery = true)
	Payment findByOrderId(String orderId);

	@Query(value = "SELECT * FROM payments WHERE id_user = ?1 AND claimed = false", nativeQuery = true)
	List<Payment> findByUser(Long id);

	@Query(value = "SELECT * FROM payments WHERE id_user = ?1 AND claimed = false AND status = 'APPROVED'", nativeQuery = true)
	List<Payment> findUnclaimedVerifiedPaymentsByUser(Long id);

}
