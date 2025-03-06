package com.javi.deckup.service;

import java.util.List;

import com.javi.deckup.model.dto.UsuarioDTO;

public interface UsuarioService {

	List<UsuarioDTO> findAll();

	UsuarioDTO findByEmail(String email);
	
	UsuarioDTO findByEmail(String email, boolean wantPass);
	
}
