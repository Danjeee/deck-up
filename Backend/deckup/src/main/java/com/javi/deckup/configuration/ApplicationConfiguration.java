package com.javi.deckup.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.repository.dao.UsuarioRepository;

@Configuration
public class ApplicationConfiguration {

	@Autowired
	UsuarioRepository ur;

	@Bean
	 public UserDetailsService userDetailsService() {
	 return username -> ur.findByEmail(username).map(u -> User.builder()
	 													  .username(u.getUsername())
	 													  .password(u.getPassword())
	 													  .roles(u.getRoles().get(0).getNombre())
	 													  .build()).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));
	 }

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(this.userDetailsService());
		provider.setPasswordEncoder(this.passwordEncoder());
		return provider;
	}
}
