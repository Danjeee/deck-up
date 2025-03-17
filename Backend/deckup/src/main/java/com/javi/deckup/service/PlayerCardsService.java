package com.javi.deckup.service;

import java.util.List;

import com.javi.deckup.model.dto.PlayerCardsDTO;

public interface PlayerCardsService {

	List<PlayerCardsDTO> getAllByUser(Long id);
	
}
