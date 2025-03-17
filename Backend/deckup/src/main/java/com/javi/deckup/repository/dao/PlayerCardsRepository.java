package com.javi.deckup.repository.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.javi.deckup.repository.entity.PlayerCards;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface PlayerCardsRepository extends JpaRepository<PlayerCards, Long>{

	@Query(value = "SELECT * FROM jugadores_cartas WHERE id_jugador = ?1", nativeQuery = true)
	List<PlayerCards> findAllByUser(Long id);

}
