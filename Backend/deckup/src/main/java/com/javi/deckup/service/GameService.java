package com.javi.deckup.service;

import java.util.List;

import com.javi.deckup.model.dto.GameDTO;
import com.javi.deckup.model.dto.LineaDTO;
import com.javi.deckup.model.dto.PlayerStatusDTO;
import com.javi.deckup.model.dto.UsuarioDTO;

public interface GameService {

	void save(GameDTO game);

	void save(GameDTO game, boolean notify);
	
	void save(GameDTO game, String line);
	
	GameDTO join(UsuarioDTO user);
	
	List<GameDTO> findAll();
	
	GameDTO findById(Long id);

	GameDTO findByPlayer1(Long id, Boolean unstarted);
	
	void deleteById(Long id);

	void delete(GameDTO game);

	LineaDTO save(LineaDTO nuevaLinea);

	PlayerStatusDTO save(PlayerStatusDTO player1);

	void deleteLinea(LineaDTO linea);
	void deleteLineaById(LineaDTO linea);

	void deleteAllLines(Long id);

	List<GameDTO> findPrev(Long id);
	
}
