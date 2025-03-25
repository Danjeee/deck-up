package com.javi.deckup.service;

import java.util.List;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.PlayerCardsDTO;
import com.javi.deckup.model.dto.UsuarioDTO;

public interface PlayerCardsService {

	List<PlayerCardsDTO> getAllByUser(Long id);

	void giveCard(UsuarioDTO user, CartaDTO carta, Integer cant);
	
}
