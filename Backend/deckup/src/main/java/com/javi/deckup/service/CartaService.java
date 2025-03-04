package com.javi.deckup.service;

import java.util.List;

import com.javi.deckup.model.dto.CartaDTO;

public interface CartaService {

	List<CartaDTO> findAll();

}
