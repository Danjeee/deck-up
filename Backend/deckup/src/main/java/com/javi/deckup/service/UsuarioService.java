package com.javi.deckup.service;

import java.util.List;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.UsuarioDTO;

public interface UsuarioService {

	List<UsuarioDTO> findAll();

	UsuarioDTO findByEmail(String email);
	
	UsuarioDTO findByEmail(String email, boolean wantPass);

	void addVerificationCode(String mail);

	UsuarioDTO findByUsername(String username);

	void save(UsuarioDTO user);

	UsuarioDTO findById(Long id);
	
	UsuarioDTO findById(Long id, boolean wantPass);

	void pay(UsuarioDTO user, Integer amount);

	void buy(UsuarioDTO user, CartaDTO card);

	UsuarioDTO findByToken(String auth);
	
	UsuarioDTO findByToken(String auth , boolean wantPass);

	void recive(UsuarioDTO user, CartaDTO i);

	void buy(UsuarioDTO user, Integer precio);
	
}
