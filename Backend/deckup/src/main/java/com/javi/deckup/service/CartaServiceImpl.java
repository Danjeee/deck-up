package com.javi.deckup.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.repository.dao.CartaRepository;

@Service
public class CartaServiceImpl implements CartaService {

	@Autowired
	CartaRepository cr;
	
	@Override
	public List<CartaDTO> findAll() {
		return cr.findAll().stream().map(c -> CartaDTO.convertToDTO(c, true)).collect(Collectors.toList());
	}

}
