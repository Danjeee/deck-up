package com.javi.deckup.repository.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.javi.deckup.repository.entity.Trade;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface TradeRepository extends JpaRepository<Trade, Long>{

	@Query(value = "SELECT * FROM trades WHERE code = ?1", nativeQuery = true)
	Optional<Trade> findByCode(String code);
	
}
