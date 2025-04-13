package com.javi.deckup.web.webservices;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.GameDTO;
import com.javi.deckup.model.dto.LineaDTO;
import com.javi.deckup.model.dto.PlayerStatusDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.repository.entity.PlayerStatus;
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
			if (card.getMana() > game.getPlayer1().getMana()) {
				return Response.error("No tienes mana");
			} else {
				game.getPlayer1().setMana(game.getPlayer1().getMana() - card.getMana());
			}
			game.setPlayer1(gs.save(game.getPlayer1()));
		} else {
			if(game.getPlayer2().getCarta1()!=null){if (game.getPlayer2().getCarta1().getId() == data.getCard_id() && !cardremoved) {game.getPlayer2().setCarta1(null); cardremoved = true;}}
			if(game.getPlayer2().getCarta2()!=null){if (game.getPlayer2().getCarta2().getId() == data.getCard_id() && !cardremoved) {game.getPlayer2().setCarta2(null); cardremoved = true;}}
			if(game.getPlayer2().getCarta3()!=null){if (game.getPlayer2().getCarta3().getId() == data.getCard_id() && !cardremoved) {game.getPlayer2().setCarta3(null); cardremoved = true;}}
			if(game.getPlayer2().getCarta4()!=null){if (game.getPlayer2().getCarta4().getId() == data.getCard_id() && !cardremoved) {game.getPlayer2().setCarta4(null); cardremoved = true;}}
			if(game.getPlayer2().getCarta5()!=null){if (game.getPlayer2().getCarta5().getId() == data.getCard_id() && !cardremoved) {game.getPlayer2().setCarta5(null); cardremoved = true;}}
			if (card.getMana() > game.getPlayer2().getMana()) {
				return Response.error("No tienes mana");
			} else {
				game.getPlayer2().setMana(game.getPlayer2().getMana() - card.getMana());
			}
			game.setPlayer2(gs.save(game.getPlayer2()));
		}
		gs.save(game, true);
		return Response.success("donete");
	}
	
	@PostMapping("/switch")
	public Response switchturn(@ModelAttribute GameAction data) {
		GameDTO game = gs.findById(data.getGame_id());
		System.out.println(game.getTurno());
		switch (game.getTurno()) {
		case 1: game.setTurno(2); gs.save(game, true); break;
		case 2: game.setTurno(3); gs.save(game, true); break;
		case 3: 
			if (data.getPlayer() == 1) {
				game.setP1_c(true);
			} else {
				game.setP2_c(true);
			}
			gs.save(game);
			if (game.getP1_c() != null && game.getP2_c() != null) {
				if (game.getP1_c() && game.getP2_c()) {
					game = fight(game);
					PlayerStatusDTO p1 = game.getPlayer1();
					p1.setMana(p1.getMana()+1);
					PlayerStatusDTO p2 = game.getPlayer2();
					p2.setMana(p2.getMana()+1);
					p1 = GameAction.drawCard(p1);
					p2 = GameAction.drawCard(p2);
					gs.save(p1);
					gs.save(p2);
					game.setTurno(1);
					game.setP1_c(null);
					game.setP2_c(null);
					gs.save(game, true);
				}
			}
			
			break;
		}
		
		return Response.success("donete");
	}
	
	private GameDTO fight(GameDTO game) {
		GameDTO game_aux = GameDTO.builder()
								  .l1_1(game.getL1_1())
								  .l1_2(game.getL1_2())
								  .l1_3(game.getL1_3())
								  .l1_4(game.getL1_4())
								  .l1_5(game.getL1_5())
								  .l2_1(game.getL2_1())
								  .l2_2(game.getL2_2())
								  .l2_3(game.getL2_3())
								  .l2_4(game.getL2_4())
								  .l2_5(game.getL2_5())
								  .build();
		LineaDTO linea = null;
		LineaDTO linea_own = null;
		PlayerStatusDTO player1 = game.getPlayer1();
		PlayerStatusDTO player2 = game.getPlayer2();
		// Linea 1
		if (game_aux.getL1_1() != null) {
			linea_own = game_aux.getL1_1();
			if (game.getL2_1() != null) {
				linea = game.getL2_1();
				linea.setVida(linea.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
				if (linea.getVida() <= 0) {
					game.setL2_1(null);
				} else {
					gs.save(linea);
				}
			} else {
				player2.setVida(player2.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
			}
		}
		
		// Linea 2
		if (game_aux.getL1_2() != null) {

			linea_own = game_aux.getL1_2();
			if (game.getL2_2() != null) {
				linea = game.getL2_2();
				linea.setVida(linea.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
				if (linea.getVida() <= 0) {
					game.setL2_2(null);
				} else {
					gs.save(linea);
				}
			} else {
				player2.setVida(player2.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
			}
		}
		
		// Linea 3
		if (game_aux.getL1_3() != null) {
			linea_own = game_aux.getL1_3();
			if (game.getL2_3() != null) {
				linea = game.getL2_3();
				linea.setVida(linea.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
				if (linea.getVida() <= 0) {
					game.setL2_3(null);
				} else {
					gs.save(linea);
				}
			} else {
				player2.setVida(player2.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
			}
		}
		
		//Linea 4
		if (game_aux.getL1_4() != null) {
			linea_own = game_aux.getL1_4();
			if (game.getL2_4() != null) {
				linea = game.getL2_4();
				linea.setVida(linea.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
				if (linea.getVida() <= 0) {
					System.out.println(game.getL1_4());
					System.out.println(game_aux.getL1_4());
					System.out.println(game.getL2_4());
					System.out.println(game_aux.getL2_4());
					game.setL2_4(null);
					System.out.println(game.getL1_4());
					System.out.println(game_aux.getL1_4());
					System.out.println(game.getL2_4());
					System.out.println(game_aux.getL2_4());
				} else {
					gs.save(linea);
				}
			} else {
				player2.setVida(player2.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
			}
		}
		
		//Linea 5
		if (game_aux.getL1_5() != null) {
			linea_own = game_aux.getL1_5();
			if (game.getL2_5() != null) {
				linea = game.getL2_5();
				linea.setVida(linea.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
				if (linea.getVida() <= 0) {
					game.setL2_5(null);
				} else {
					gs.save(linea);
				}
			} else {
				player2.setVida(player2.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
			}
		}
		
		//Linea 2-1
		if (game_aux.getL2_1() != null) {
			linea_own = game_aux.getL2_1();
			if (game.getL1_1() != null) {
				linea = game.getL1_1();
				linea.setVida(linea.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
				if (linea.getVida() <= 0) {
					game.setL1_1(null);
				} else {
					gs.save(linea);
				}
			} else {
				player1.setVida(player1.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
			}
		}
		
		//Linea 2-2
		if (game_aux.getL2_2() != null) {
			linea_own = game_aux.getL2_2();
			if (game.getL1_2() != null) {
				linea = game.getL1_2();
				linea.setVida(linea.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
				if (linea.getVida() <= 0) {
					game.setL1_2(null);
				} else {
					gs.save(linea);
				}
			} else {
				player1.setVida(player1.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
			}
		}
		
		//Linea 2-3
		if (game_aux.getL2_3() != null) {
			linea_own = game_aux.getL2_3();
			if (game.getL1_3() != null) {
				linea = game.getL1_3();
				linea.setVida(linea.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
				if (linea.getVida() <= 0) {
					game.setL1_3(null);
				} else {
					gs.save(linea);
				}
			} else {
				player1.setVida(player1.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
			}
		}
		
		//Linea 2-4
		if (game_aux.getL2_4() != null) {
			linea_own = game_aux.getL2_4();
			if (game.getL1_4() != null) {
				linea = game.getL1_4();
				linea.setVida(linea.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
				if (linea.getVida() <= 0) {
					System.out.println("muere");
					game.setL1_4(null);
				} else {
					System.out.println("vive");
					gs.save(linea);
				}
			} else {
				player1.setVida(player1.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
			}
		}
		
		//Linea 2-5
		if (game_aux.getL2_5() != null) {
			linea_own = game_aux.getL2_5();
			if (game.getL1_5() != null) {
				linea = game.getL1_5();
				linea.setVida(linea.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
				if (linea.getVida() <= 0) {
					game.setL1_5(null);
				} else {
					gs.save(linea);
				}
			} else {
				player1.setVida(player1.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
			}
		}
		
		return game;
	}
	
	
	
}
