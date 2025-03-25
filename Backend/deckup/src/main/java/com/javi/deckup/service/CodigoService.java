package com.javi.deckup.service;

import java.util.List;

import com.javi.deckup.model.dto.CodigoDTO;
import com.javi.deckup.model.dto.UsuarioDTO;

public interface CodigoService {

	List<CodigoDTO> findAll();
	
	CodigoDTO findByCodigo(String code);

	boolean hasUserUsed(Long id, Integer idcode);

	void save(CodigoDTO code);
	
	void save(CodigoDTO code, UsuarioDTO user);

}
