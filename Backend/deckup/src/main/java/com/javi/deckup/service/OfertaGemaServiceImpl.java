package com.javi.deckup.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.OfertaGemaDTO;
import com.javi.deckup.repository.dao.OfertaGemaRepository;

@Service
public class OfertaGemaServiceImpl implements OfertaGemaService {

	@Autowired
	OfertaGemaRepository gr;
	
	@Override
	public List<OfertaGemaDTO> findAll() {
		return gr.findAll().stream().map(g -> OfertaGemaDTO.convertToDTO(g)).collect(Collectors.toList());
	}

}
