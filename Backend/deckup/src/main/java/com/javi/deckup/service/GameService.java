package com.javi.deckup.service;

import java.util.List;

import com.javi.deckup.model.dto.GameDTO;
import com.javi.deckup.model.dto.UsuarioDTO;

public interface GameService {

	void save(GameDTO game);
	
	GameDTO join(UsuarioDTO user);
	
	List<GameDTO> findAll();
	
	GameDTO findById(Long id);

	GameDTO findByPlayer1(Long id, Boolean unstarted);
	
	void deleteById(Long id);

	void delete(GameDTO game);
	
}
