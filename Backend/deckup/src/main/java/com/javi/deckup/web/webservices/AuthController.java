package com.javi.deckup.web.webservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.Encrypt;
import com.javi.deckup.utils.Response;
import com.javi.deckup.utils.Session;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	UsuarioService us;

	BCryptPasswordEncoder auth = new BCryptPasswordEncoder();

	@PostMapping("/login")
	public Response login(@ModelAttribute UsuarioDTO data) {
		UsuarioDTO aux = us.findByEmail(data.getEmail(), true);
		if (aux == null) {
			return new Response(404, "Error", "El usuario no existe");
		}
		if (!aux.isEstado()) {
			return new Response(500, "Error", "El usuario está inactivo");
		} else {
		if (auth.matches(data.getPassword(), aux.getPassword())) {
			Session.logIn(data.getUsername(), data.getPassword());
			return new Response(200, "Sesión iniciada", "Sesión iniciada correctamente", aux);
		} else {
			return new Response(500, "Error", "Contraseña incorrecta");
		}
		}
	}
	@PostMapping("/verify/{mail}")
	public ResponseEntity<Response> addVerificationCode(@ModelAttribute UsuarioDTO user) {
		us.addVerificationCode(user.getEmail());
		return new ResponseEntity<>(Response.builder().status(200).tit("Done").msg("Código enviado correctamente").build(), HttpStatus.OK);
	}
	
	@PostMapping("/verify")
	public Response verify(@ModelAttribute UsuarioDTO user) {
		UsuarioDTO aux = us.findByEmail(user.getEmail(), true);
		if (auth.matches(user.getAuth(), aux.getAuth())) {
			return Response.builder().status(200).tit("Sesión iniciada").msg("Sesión iniciada correctamente").user(us.findByEmail(user.getEmail())).build();
		} else {
			return Response.builder().status(500).tit("Error").msg("Código incorrecto, intentelo de nuevo").build();
		}

	}
	

}
