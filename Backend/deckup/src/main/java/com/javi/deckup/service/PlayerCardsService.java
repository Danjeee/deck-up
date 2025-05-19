package com.javi.deckup.service;

import java.util.List;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.PlayerCardsDTO;
import com.javi.deckup.model.dto.TradeCardsDTO;
import com.javi.deckup.model.dto.UsuarioDTO;

public interface PlayerCardsService {

	List<PlayerCardsDTO> getAllByUser(Long id);

	void giveCard(UsuarioDTO user, CartaDTO carta, Integer cant);

	PlayerCardsDTO findByCard(Integer artifact_aux, Long idUser);

	PlayerCardsDTO findById(Long artifact_long);

	void rmvCard(UsuarioDTO player1, CartaDTO carta, Integer cant);

	
}
