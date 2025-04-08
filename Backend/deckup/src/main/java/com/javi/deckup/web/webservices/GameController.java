package com.javi.deckup.web.webservices;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.GameDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.GameService;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.Response;

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
		GameDTO game = gs.join(user);
		System.out.println(game);
		
		return Response.success("Cola iniciada correctamente");
	}
	
	
}
