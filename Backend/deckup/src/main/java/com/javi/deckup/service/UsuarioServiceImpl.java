package com.javi.deckup.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.repository.dao.UsuarioRepository;
import com.javi.deckup.repository.entity.Rol;
import com.javi.deckup.repository.entity.Usuario;

@Service
public class UsuarioServiceImpl implements UsuarioService, UserDetailsService{
	
	@Autowired
	UsuarioRepository ur;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		Usuario usuario = ur.findByEmail(email).orElse(null);
		if (usuario != null) {
			List<GrantedAuthority> listaPermisos = new ArrayList<GrantedAuthority>();
			List<Rol> listaRoles = new ArrayList<Rol>(usuario.getRoles());
			for (Rol rol : listaRoles) {
				listaPermisos.add(new SimpleGrantedAuthority(rol.getNombre()));
			}
			System.out.println(usuario);
			return new User(usuario.getUsername(), usuario.getPassword(), listaPermisos);
		} else {
			throw new UsernameNotFoundException(email);
		}
	}

	@Override
	public List<UsuarioDTO> findAll() {
		return ur.findAll().stream().map(u -> UsuarioDTO.convertToDTO(u)).collect(Collectors.toList());
	}

	@Override
	public UsuarioDTO findByEmail(String email) {
		Usuario user = ur.findByEmail(email).orElse(null);
		return user == null ? null : UsuarioDTO.convertToDTO(user);
	}

	@Override
	public UsuarioDTO findByEmail(String email, boolean wantPass) {
		Usuario user = ur.findByEmail(email).orElse(null);
		return user == null ? null : UsuarioDTO.convertToDTO(user, wantPass);
	}
	
}
