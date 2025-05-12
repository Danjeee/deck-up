package com.javi.deckup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.TiendaDTO;
import com.javi.deckup.repository.dao.TiendaRepository;

@Service
public class TiendaServiceImpl implements TiendaService {

	@Autowired
	TiendaRepository tr;

	@Override
	public TiendaDTO findById(Integer i) {
		return TiendaDTO.convertToDTO(tr.findById(i).orElse(null));
	}

	@Override
	public void change() {
	}
	
}
