package com.javi.deckup.service;

import java.util.List;

import com.javi.deckup.model.dto.MazoDTO;

public interface MazoService {

	List<MazoDTO> findAllByUser(Long id);
	
	MazoDTO save(MazoDTO mazo);

	Long count();

	Long count(Long idUser);

	MazoDTO findById(Long id);

	void deleteById(Long id);

	void select(MazoDTO mazo);
	
}
