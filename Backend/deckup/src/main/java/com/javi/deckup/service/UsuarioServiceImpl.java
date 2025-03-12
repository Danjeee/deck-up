package com.javi.deckup.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.repository.dao.UsuarioRepository;
import com.javi.deckup.repository.entity.Rol;
import com.javi.deckup.repository.entity.Usuario;
import com.javi.deckup.utils.CodeGenerator;
import com.javi.deckup.utils.EmailService;
import com.javi.deckup.utils.Encrypt;

@Service
public class UsuarioServiceImpl implements UsuarioService, UserDetailsService{
	
	@Autowired
	UsuarioRepository ur;
	
	@Autowired
	private EmailService es;

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

	@Override
	public void addVerificationCode(String mail) {
	    String generatedString = CodeGenerator.generateNewCode();
	    es.sendEmailWithHTML(mail, "Verification code", "<h1>Tu código de verificación es:</h1><br><div style='display: flex; gap: 10px;'>"
	    		+ "<div style='border: #13253e 2px solid; padding: 20px; font-size: 30px; color: #fff; background-color: #5898d8; filter: drop-shadow(0px 0px 20px #13253e);'>"+generatedString+"</div>"
	    		+ "</div>"
	    		+ "<h2>Esperamos que disfrute su tiempo con nosotros</h2>"
	    		+ "<p>Si usted no ha solicitado el código, ignore este email o pongase en contacto con nosotros pero bajo ningún concepto comparta el código enviado</p>");
	    ur.addVerificationCode(mail, Encrypt.encriptarPassword(generatedString));
		
	}

	@Override
	public UsuarioDTO findByUsername(String username) {
		Usuario user = ur.findByUsername(username).orElse(null);
		return user == null ? null : UsuarioDTO.convertToDTO(user); 
	}

	@Override
	public void save(UsuarioDTO user) {
		ur.save(UsuarioDTO.convertToEntity(user));
		
	}

	@Override
	public UsuarioDTO findById(Long id) {
		Usuario aux = ur.findById(id).orElse(null);
		return aux == null ? null : UsuarioDTO.convertToDTO(aux);
	}

	@Override
	public void pay(UsuarioDTO user, Integer amount) {
		Integer total = (user.getCurrency()+amount);
		Timestamp np = (Timestamp.valueOf(LocalDateTime.now().plusHours(4)));
		Usuario user_final = ur.findById(user.getId()).orElse(null);
		user_final.setCurrency(total);
		user_final.setNextPayment(np);
		ur.save(user_final);
	}

	@Override
	public UsuarioDTO findById(Long id, boolean wantPass) {
		Usuario user = ur.findById(id).orElse(null);
		return user == null ? null : UsuarioDTO.convertToDTO(user, wantPass);
	}
	
}
