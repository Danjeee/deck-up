package com.javi.deckup.repository.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.javi.deckup.repository.entity.Game;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface GameRepository extends JpaRepository<Game, Long>{
	
	@Query(value = "SELECT * FROM games WHERE player2 IS null LIMIT 1", nativeQuery = true)
	Optional<Game> getAbleToJoin();
	
	@Query(value = "SELECT g.id FROM deckup.games g WHERE g.status = 'activo' AND ((g.player1 = (SELECT id FROM deckup.player_status WHERE usuario = ?1 AND game = g.id) AND g.player2 = (SELECT id FROM deckup.player_status WHERE usuario = ?2 AND game = g.id)) OR (g.player1 = (SELECT id FROM deckup.player_status WHERE usuario = ?2 AND game = g.id) AND g.player2 = (SELECT id FROM deckup.player_status WHERE usuario = ?1 AND game = g.id)) OR (g.player1 = (SELECT id FROM deckup.player_status WHERE usuario = ?1 AND game = g.id) AND g.player2 IS NULL AND EXISTS (SELECT 1 FROM deckup.player_status WHERE game = g.id AND usuario = ?2)) OR (g.player1 = (SELECT id FROM deckup.player_status WHERE usuario = ?2 AND game = g.id) AND g.player2 IS NULL AND EXISTS (SELECT 1 FROM deckup.player_status WHERE game = g.id AND usuario = ?1))) LIMIT 1;", nativeQuery = true)
	Optional<Game> getActiveGameByPlayers(Long id1, Long id2);

}
