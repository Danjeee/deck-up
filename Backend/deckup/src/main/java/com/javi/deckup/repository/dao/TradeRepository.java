package com.javi.deckup.repository.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.javi.deckup.repository.entity.Trade;
import com.javi.deckup.repository.entity.TradeCards;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface TradeRepository extends JpaRepository<Trade, Long>{

	@Query(value = "SELECT * FROM trades WHERE code = ?1 AND status = 'waiting'", nativeQuery = true)
	Optional<Trade> findByCode(String code);

	@Query(value = "SELECT * FROM trades WHERE player1 = ?1 OR player2 = ?1 AND status != 'activo'", nativeQuery = true)
	List<Trade> getPast(Long id);
	
}
