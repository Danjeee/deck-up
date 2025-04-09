package com.javi.deckup.web.webservices;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.GameDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.GameService;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.Response;
import com.javi.deckup.utils.UserAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/game")
public class GameController {

	@Autowired
	UsuarioService us;
	
	@Autowired
	GameService gs;
	
	@PostMapping("/matchmaking")
	public Response joinqueue(@ModelAttribute UsuarioDTO user) {
		user = us.findByToken(user.getAuth());
		if (user == null) {
			return Response.error("Ha habido un error en la sesión");
		}
		gs.join(user);
		return Response.success("Cola iniciada correctamente");
	}
	
	@PostMapping("/matchmaking/cancel")
	public Response cancel(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		if (user == null) {
			return Response.error("Ha habido un error en la sesión");
		}
		GameDTO game = gs.findByPlayer1(user.getId(), true);
		if (game != null) {
			gs.delete(game);
		}
		
		return Response.success("Cola iniciada correctamente");
	}
	
	@PostMapping("/get")
	public GameDTO getGame(@ModelAttribute GameDTO data) {
		GameDTO game = gs.findById(data.getId());

		System.out.println(game);
		return game;
	}
	
	
}
