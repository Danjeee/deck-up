package com.javi.deckup.web.webservices;

import java.time.Instant;
import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.Response;
import com.javi.deckup.utils.UserAction;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	UsuarioService us;

	@PostMapping("/getPaid")
	public Response getPaid(@ModelAttribute UsuarioDTO user) {
		user = us.findById(user.getId());
		if (user == null) {
			return Response.builder().status(400).tit("Error").msg("Error encontrando el usuario, vuelva a intentarlo").build();
		}
		if (user.getNextPayment().after(Date.from(Instant.now()))) {
			return Response.builder().status(500).tit("Error").msg("Error recibiendo el pago").build();
		} else {
			Random rand = new Random();
			Integer amount = rand.nextInt(5)+1;
			amount *= 100;
			us.pay(user, amount);
			user = us.findById(user.getId());
			return Response.builder().status(200).tit("Pago correcto").msg(String.valueOf(amount)).user(UsuarioDTO.builder().nextPayment(user.getNextPayment()).currency(user.getCurrency()).build()).build();
		}
		
	}
	@PostMapping("/changepfp")
	public Response changePfp(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		if (user == null) {
			return Response.error("Error al cargar el usuario");
		}
		us.changePFP(user.getId(), data.getCode());
		return Response.success("Donete");
	}
	@PostMapping("/changeusername")
	public Response changeusername(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		if (user == null) {
			return Response.error("Error al cargar el usuario");
		}
		us.changeUsername(user.getId(), data.getCode());
		return Response.success("Donete");
	}
	
	@GetMapping("/{id}")
	public UsuarioDTO getById(@PathVariable("id") Long id) {
		return us.findById(id);
	}
	
	@GetMapping("/getForChat/{username}")
	public UsuarioDTO getForChat(@PathVariable("username") String username) {
		UsuarioDTO aux = us.findByUsername(username);
		return UsuarioDTO.builder().id(aux.getId()).username(aux.getUsername()).pfp(aux.getPfp()).build();
	}
	
}
