package com.javi.deckup.service;

import java.util.List;

import com.javi.deckup.model.dto.HabilidadDTO;

public interface HabilidadService {

	List<HabilidadDTO> getAll();
	
	void save(HabilidadDTO hab);
	
}
