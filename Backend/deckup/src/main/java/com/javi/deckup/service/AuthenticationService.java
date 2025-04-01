package com.javi.deckup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.repository.dao.UsuarioRepository;

@Service
public class AuthenticationService {
	@Autowired
	private UsuarioService us;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;

	public UsuarioDTO login(UsuarioDTO input) {
		UsuarioDTO user = us.findByEmail(input.getEmail(), true);
		if (user == null) {
			return null;
		}
		System.out.println(input);
		if ((!passwordEncoder.matches(input.getPassword(), user.getPassword())) && !input.getPassword().equals(user.getPassword()) ) {
			return UsuarioDTO.builder().id(null).build();
		}

		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));
		return user;
	}
}
