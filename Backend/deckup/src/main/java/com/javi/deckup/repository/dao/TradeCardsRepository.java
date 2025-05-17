package com.javi.deckup.repository.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.javi.deckup.repository.entity.Trade;
import com.javi.deckup.repository.entity.TradeCards;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface TradeCardsRepository extends JpaRepository<TradeCards, Long>{

	@Query(value = "SELECT * FROM trades_cartas WHERE id_trade = ?1", nativeQuery = true)
	List<TradeCards> getAllCards(Long id);

	@Query(value = "SELECT * FROM trades_cartas WHERE id_trade = ?1 AND id_jugador = ?2 AND id_carta = ?3", nativeQuery = true)
	Optional<TradeCards> findCardByTradeAndPlayerAndCard(Long id, Long id2, Integer id3);

	@Modifying
	@Query(value = "DELETE FROM trades_cartas WHERE id = ?1", nativeQuery = true)
	void deleteTC(Long id);

}
