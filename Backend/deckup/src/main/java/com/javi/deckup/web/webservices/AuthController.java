package com.javi.deckup.web.webservices;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.RolDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.CodeGenerator;
import com.javi.deckup.utils.EmailService;
import com.javi.deckup.utils.Encrypt;
import com.javi.deckup.utils.Response;
import com.javi.deckup.utils.Session;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	UsuarioService us;
	
	@Autowired
	EmailService mail;

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
			aux.setPassword("");
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
			UsuarioDTO finaluser = us.findByEmail(user.getEmail());
			finaluser.setAuth(aux.getAuth());
			return Response.builder().status(200).tit("Sesión iniciada").msg("Sesión iniciada correctamente").user(finaluser).build();
		} else {
			return Response.builder().status(500).tit("Error").msg("Código incorrecto, intentelo de nuevo").build();
		}
	}
	
	@PostMapping("/logout")
	public Response logout() {
		Session.logOut();
		return Response.builder().status(200).tit("Sesión cerrada").msg("Sesión cerrada correctamente").build();
	}
	
	@PostMapping("/restore")
	public Response restore(@ModelAttribute UsuarioDTO user) {
		UsuarioDTO aux = us.findById(user.getId(), true);
		if (aux == null) {
			return Response.builder().status(500).tit("Lo sentimos").msg("Ha habido un error en su sesión, vuelva a iniciar sesión").build();
		}
		if (aux.getAuth().equals(user.getAuth())) {
			return Response.builder().status(200).user(aux).build();
		} else {
			return Response.builder().status(500).tit("Lo sentimos").msg("Ha habido un error en su sesión, vuelva a iniciar sesión").build();
		}
	}
	
	@PostMapping("/register/verify/{code}")
	public Response verifyRegister(@ModelAttribute UsuarioDTO user, @PathVariable("code") String verification) {
	
		if (us.findByEmail(user.getEmail()) != null) {
			return Response.builder().status(503).tit("Error").msg("El usuario con el email "+user.getEmail()+" ya existe").build();
		}
		if (us.findByUsername(user.getUsername()) != null) {
			return Response.builder().status(503).tit("Error").msg("El usuario "+user.getUsername()+" ya existe").build();
		}
		if (user.getAuth() == "w") {
			return Response.builder().status(500).tit("Error").msg("Codigo incorrecto").build();
		}
		if (user.getAuth() == null) {
			String code = CodeGenerator.generateNewCode();
			mail.sendEmailWithHTML(user.getEmail(), "Verification code", "<h1>Tu código de verificación es:</h1><br><div style='display: flex; gap: 10px;'>"
		    		+ "<div style='border: #13253e 2px solid; padding: 20px; font-size: 30px; color: #fff; background-color: #5898d8; filter: drop-shadow(0px 0px 20px #13253e);'>"+code+"</div>"
		    		+ "</div>"
		    		+ "<h2>Esperamos que disfrute su tiempo con nosotros</h2>"
		    		+ "<p>Si usted no ha solicitado el código, ignore este email o pongase en contacto con nosotros pero bajo ningún concepto comparta el código enviado</p>");
			return Response.builder().status(100).msg(Encrypt.encriptarPassword(code)).build(); // Vuelve a mandar la peticion para indicar que se puede crear el usuario junto con el codigo encriptado
		} else {
			verification = verification.toUpperCase();
			if (auth.matches(verification, user.getAuth())){
				user.setCurrency(500);
				user.setPassword(Encrypt.encriptarPassword(user.getPassword()));
				user.setEstado(true);
				//user.setNextPayment(Timestamp.valueOf(LocalDateTime.now().plusHours(4)));
				//user.setNextPayment(Timestamp.valueOf(LocalDateTime.now().plusMinutes(2)));
				user.setNextPayment(Timestamp.valueOf(LocalDateTime.now()));
				user.setAuth(user.getAuth());
				us.save(user);
				UsuarioDTO aux = us.findByEmail(user.getEmail(), true);
				List<RolDTO> roles = new ArrayList<>();
				roles.add(RolDTO.builder().nombre("ROLE_USER").usuarioDTO(aux).build());
				aux.setRolesDTO(roles);
				us.save(aux);
				aux = us.findByEmail(user.getEmail());
				aux.setAuth(user.getAuth());
				return Response.builder().status(200).tit("Cuenta creada").msg("Cuenta creada correctamente").user(aux).build();
			} else {
				return Response.builder().status(500).tit("Error").msg("Codigo incorrecto").build();
			}
		}
		
	}
	

}
