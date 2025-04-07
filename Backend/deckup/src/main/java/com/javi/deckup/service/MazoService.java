package com.javi.deckup.service;

import java.util.List;

import com.javi.deckup.model.dto.MazoDTO;

public interface MazoService {

	List<MazoDTO> findAllByUser(Long id);
	
	void save(MazoDTO mazo);

	Long count();

	MazoDTO findById(Long id);

	void deleteById(Long id);
	
}
