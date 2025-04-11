package com.javi.deckup.web.webservices;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.GameDTO;
import com.javi.deckup.model.dto.LineaDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.CartaService;
import com.javi.deckup.service.GameService;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.GameAction;
import com.javi.deckup.utils.Response;
import com.javi.deckup.utils.UserAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	@Autowired
	CartaService cs;
	
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
		return game;
	}
	@PostMapping("/put")
	public Response put(@ModelAttribute GameAction data) {
		GameDTO game = gs.findById(data.getGame_id());
		CartaDTO card = cs.findById(data.getCard_id());
		LineaDTO linea = gs.save(GameAction.nuevaLinea(card, game));
		switch (data.getLinea()) {
			case "l1_1": game.setL1_1(linea); break; 
			case "l1_2": game.setL1_2(linea); break; 
			case "l1_3": game.setL1_3(linea); break; 
			case "l1_4": game.setL1_4(linea); break; 
			case "l1_5": game.setL1_5(linea); break; 
			case "l2_1": game.setL2_1(linea); break; 
			case "l2_2": game.setL2_2(linea); break; 
			case "l2_3": game.setL2_3(linea); break; 
			case "l2_4": game.setL2_4(linea); break; 
			case "l2_5": game.setL2_5(linea); break; 
		}
		boolean cardremoved = false;
		if (data.getPlayer() == 1) {
			if(game.getPlayer1().getCarta1()!=null){if (game.getPlayer1().getCarta1().getId() == data.getCard_id() && !cardremoved) {game.getPlayer1().setCarta1(null); cardremoved = true;}}
			if(game.getPlayer1().getCarta2()!=null){if (game.getPlayer1().getCarta2().getId() == data.getCard_id() && !cardremoved) {game.getPlayer1().setCarta2(null); cardremoved = true;}}
			if(game.getPlayer1().getCarta3()!=null){if (game.getPlayer1().getCarta3().getId() == data.getCard_id() && !cardremoved) {game.getPlayer1().setCarta3(null); cardremoved = true;}}
			if(game.getPlayer1().getCarta4()!=null){if (game.getPlayer1().getCarta4().getId() == data.getCard_id() && !cardremoved) {game.getPlayer1().setCarta4(null); cardremoved = true;}}
			if(game.getPlayer1().getCarta5()!=null){if (game.getPlayer1().getCarta5().getId() == data.getCard_id() && !cardremoved) {game.getPlayer1().setCarta5(null); cardremoved = true;}}
			game.setPlayer1(gs.save(game.getPlayer1()));
		} else {
			if(game.getPlayer2().getCarta1()!=null){if (game.getPlayer2().getCarta1().getId() == data.getCard_id() && !cardremoved) {game.getPlayer2().setCarta1(null); cardremoved = true;}}
			if(game.getPlayer2().getCarta2()!=null){if (game.getPlayer2().getCarta2().getId() == data.getCard_id() && !cardremoved) {game.getPlayer2().setCarta2(null); cardremoved = true;}}
			if(game.getPlayer2().getCarta3()!=null){if (game.getPlayer2().getCarta3().getId() == data.getCard_id() && !cardremoved) {game.getPlayer2().setCarta3(null); cardremoved = true;}}
			if(game.getPlayer2().getCarta4()!=null){if (game.getPlayer2().getCarta4().getId() == data.getCard_id() && !cardremoved) {game.getPlayer2().setCarta4(null); cardremoved = true;}}
			if(game.getPlayer2().getCarta5()!=null){if (game.getPlayer2().getCarta5().getId() == data.getCard_id() && !cardremoved) {game.getPlayer2().setCarta5(null); cardremoved = true;}}
			game.setPlayer2(gs.save(game.getPlayer2()));
		}
		gs.save(game, true);
		return Response.success("donete");
	}
	
	@PostMapping("/switch")
	public Response switchturn(@ModelAttribute GameAction data) {
		GameDTO game = gs.findById(data.getGame_id());
		System.out.println(game.getTurno());
		if (game.getTurno() == 1) {
			game.setTurno(2);
		} else {
			game.setTurno(1);
		}
		gs.save(game, true);
		return Response.success("donete");
	}
	
	
	
}
