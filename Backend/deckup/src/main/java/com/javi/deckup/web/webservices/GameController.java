package com.javi.deckup.web.webservices;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.GameDTO;
import com.javi.deckup.model.dto.HabilidadDTO;
import com.javi.deckup.model.dto.LineaDTO;
import com.javi.deckup.model.dto.PlayerStatusDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.repository.entity.PlayerStatus;
import com.javi.deckup.service.CartaService;
import com.javi.deckup.service.GameService;
import com.javi.deckup.service.NotificationService;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.GameAction;
import com.javi.deckup.utils.Response;
import com.javi.deckup.utils.UserAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

	@Autowired
	NotificationService ns;

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

	private void burn(LineaDTO linea, int cant) {
		if (linea != null) {
			if (linea.getBurn() == null || linea.getBurn() == 0) {
				linea.setBurn(cant);
				System.out.println(linea);
			}
		}
	}

	private void stun(LineaDTO linea, int cant, String name) {
		if (linea != null) {
			if (linea.getStun() == null || linea.getStun() == 0) {
				linea.setStun(cant);
				linea.setStun_name(name);
			}
		}
	}

	private void poison(LineaDTO linea, int cant) {
		if (linea != null) {
			if (linea.getPoisn() == null || linea.getPoisn() == 0) {
				linea.setPoisn(cant);
			}
		}
	}

	private boolean damage(LineaDTO linea, int cant) {
		boolean dies = false;
		if (linea != null) {
			linea.setVida(linea.getVida() - cant);
			if (linea.getVida() <= 0) {
				dies = true;
			}
		}
		return dies;
	}

	private void heal(LineaDTO linea, int cant) {
		if (linea != null && linea.getCarta() != null && linea.getCarta().getVida() != null) {
			linea.setVida(linea.getVida() + cant);
			if (linea.getVida() > linea.getCarta().getVida()) {
				linea.setVida(linea.getCarta().getVida());
			}
		}
	}
	
	private boolean execDieFunc(LineaDTO enemy, GameDTO game, LineaDTO you, int player, HabilidadDTO hab) {
		if (hab.getEspecial() == null) {
			return false;
		}
		if (hab.getEspecial().charAt(0) != 'T') {
			return false;
		}
		boolean dies = false;
		String command = hab.getEspecial();
		int dur = 1;
		if (Character.isDigit(command.charAt(command.length() - 1))) {
			dur = Character.getNumericValue(command.charAt(command.length() - 1));
			command = command.substring(0, command.length() - 1);
		}

		switch (command) {
		case "TF": // check
			stun(enemy, dur, hab.getFreezeName());
			gs.save(enemy);
			break;
		case "TB": // check
			burn(enemy, dur);
			gs.save(enemy);
			break;
		case "TK": // check
			eliminarLinea(game, enemy);
			break;
		case "TP": // check
			poison(enemy, dur);
			gs.save(enemy);
			break;
		case "TD": // check
			dies = damage(enemy, dur);
			gs.save(enemy);
			break; // check
		case "TY":
			if (player == 2) {
				game.getPlayer2().setVida(game.getPlayer2().getVida() - dur);
				gs.save(game.getPlayer2());
			} else {
				game.getPlayer1().setVida(game.getPlayer1().getVida() - dur);
				gs.save(game.getPlayer1());
			}
			break;
		case "TFA": // check
			player = (player == 1) ? 2:1; 
			freezeAll(player, game, dur, hab.getFreezeName());
			break;
		case "TBA": // check
			player = (player == 1) ? 2:1; 
			setAllOnFire(player, game, dur);
			break;
		case "TKA": // check
			player = (player == 1) ? 2:1; 
			killAll(player, game);
			break;
		case "TPA": // check
			player = (player == 1) ? 2:1; 
			poisonAll(player, game, dur);
			break;
		case "TDA": // check
			player = (player == 1) ? 2:1; 
			damageAll(player, game, dur);
			break;
		}
		if (command.contains("A")) {
			saveAllLines(game, you);
		}
		return dies;
	}

	private void savePlayerLines(GameDTO game, int player) {
		player = (player == 1) ? 2:1; 
		if (player == 1) {
			gs.save(game.getL1_1());
			gs.save(game.getL1_2());
			gs.save(game.getL1_3());
			gs.save(game.getL1_4());
			gs.save(game.getL1_5());
		} else {
			gs.save(game.getL2_1());
			gs.save(game.getL2_2());
			gs.save(game.getL2_3());
			gs.save(game.getL2_4());
			gs.save(game.getL2_5());
		}
	}

	private boolean execDeployFunc(LineaDTO enemy, GameDTO game, LineaDTO you, int player, HabilidadDTO hab) {
		boolean dies = false;
		String command = hab.getEspecial();
		int dur = 1;
		if (Character.isDigit(command.charAt(command.length() - 1))) {
			dur = Character.getNumericValue(command.charAt(command.length() - 1));
			command = command.substring(0, command.length() - 1);
		}
		switch (command) {
		case "DF":
			stun(enemy, dur, hab.getFreezeName());
			gs.save(enemy);
			break;
		case "DB":
			burn(enemy, dur);
			gs.save(enemy);
			break;
		case "DK":
			dies = true;
			break;
		case "DP":
			poison(enemy, dur);
			gs.save(enemy);
			break;
		case "DD":
			dies = damage(enemy, dur);
			gs.save(enemy);
			break;
		case "DW":
			you.setWillcrit(true);
			gs.save(you);
			break;
		case "DFA":
			freezeAll(player, game, dur, hab.getFreezeName());
			break;
		case "DBA":
			setAllOnFire(player, game, dur);
			break;
		case "DKA":
			killAll(player, game);
			break;
		case "DPA":
			poisonAll(player, game, dur);
			break;
		case "DDA":
			damageAll(player, game, dur);
			break;
		case "DHC":
			healAll(player, game, dur);
			saveAllLines(game, true);
			break;
		case "DHA":
			healAll(player, game, dur);
			if (player == 1) {
				game.getPlayer1().setVida(game.getPlayer1().getVida() + dur);
				if (game.getPlayer1().getVida() > 40) {game.getPlayer1().setVida(40);}
				gs.save(game.getPlayer2());
			} else {
				game.getPlayer2().setVida(game.getPlayer2().getVida() + dur);
				if (game.getPlayer2().getVida() > 40) {game.getPlayer2().setVida(40);}
				gs.save(game.getPlayer2());
			}
			break;
		case "DHP":
			if (player == 1) {
				game.getPlayer1().setVida(game.getPlayer1().getVida() + dur);
				if (game.getPlayer1().getVida() > 40) {game.getPlayer1().setVida(40);}
				gs.save(game.getPlayer2());
			} else {
				game.getPlayer2().setVida(game.getPlayer2().getVida() + dur);
				if (game.getPlayer2().getVida() > 40) {game.getPlayer2().setVida(40);}
				gs.save(game.getPlayer2());
			}
			break;
		}
		return dies;
	}

	private void healAll(int player, GameDTO game, int cant) {
		if (player == 1) {
			heal(game.getL1_1(), cant);
			heal(game.getL1_2(), cant);
			heal(game.getL1_3(), cant);
			heal(game.getL1_4(), cant);
			heal(game.getL1_5(), cant);
		} else {
			heal(game.getL2_1(), cant);
			heal(game.getL2_2(), cant);
			heal(game.getL2_3(), cant);
			heal(game.getL2_4(), cant);
			heal(game.getL2_5(), cant);
		}

	}

	private void damageAll(int player, GameDTO game, int cant) {
		if (player == 1) {
		    if (damage(game.getL2_1(), cant)) {
		        if (game.getL2_1() != null) {
		            execDieFunc(game.getL1_1(), game, game.getL2_1(), player, game.getL2_1().getCarta().getHabilidadDTO());
		        }
		        game.setL2_1(null);
		    }
		    if (damage(game.getL2_2(), cant)) {
		        if (game.getL2_2() != null) {
		            execDieFunc(game.getL1_2(), game, game.getL2_2(), player, game.getL2_2().getCarta().getHabilidadDTO());
		        }
		        game.setL2_2(null);
		    }
		    if (damage(game.getL2_3(), cant)) {
		        if (game.getL2_3() != null) {
		            execDieFunc(game.getL1_3(), game, game.getL2_3(), player, game.getL2_3().getCarta().getHabilidadDTO());
		        }
		        game.setL2_3(null);
		    }
		    if (damage(game.getL2_4(), cant)) {
		        if (game.getL2_4() != null) {
		            execDieFunc(game.getL1_4(), game, game.getL2_4(), player, game.getL2_4().getCarta().getHabilidadDTO());
		        }
		        game.setL2_4(null);
		    }
		    if (damage(game.getL2_5(), cant)) {
		        if (game.getL2_5() != null) {
		            execDieFunc(game.getL1_5(), game, game.getL2_5(), player, game.getL2_5().getCarta().getHabilidadDTO());
		        }
		        game.setL2_5(null);
		    }
		} else {
		    if (damage(game.getL1_1(), cant)) {
		        if (game.getL1_1() != null) {
		            execDieFunc(game.getL2_1(), game, game.getL1_1(), player, game.getL1_1().getCarta().getHabilidadDTO());
		        }
		        game.setL1_1(null);
		    }
		    if (damage(game.getL1_2(), cant)) {
		        if (game.getL1_2() != null) {
		            execDieFunc(game.getL2_2(), game, game.getL1_2(), player, game.getL1_2().getCarta().getHabilidadDTO());
		        }
		        game.setL1_2(null);
		    }
		    if (damage(game.getL1_3(), cant)) {
		        if (game.getL1_3() != null) {
		            execDieFunc(game.getL2_3(), game, game.getL1_3(), player, game.getL1_3().getCarta().getHabilidadDTO());
		        }
		        game.setL1_3(null);
		    }
		    if (damage(game.getL1_4(), cant)) {
		        if (game.getL1_4() != null) {
		            execDieFunc(game.getL2_4(), game, game.getL1_4(), player, game.getL1_4().getCarta().getHabilidadDTO());
		        }
		        game.setL1_4(null);
		    }
		    if (damage(game.getL1_5(), cant)) {
		        if (game.getL1_5() != null) {
		            execDieFunc(game.getL2_5(), game, game.getL1_5(), player, game.getL1_5().getCarta().getHabilidadDTO());
		        }
		        game.setL1_5(null);
		    }
		}

	}

	private void poisonAll(int player, GameDTO game, int cant) {
		if (player == 1) {
			poison(game.getL2_1(), cant);
			poison(game.getL2_2(), cant);
			poison(game.getL2_3(), cant);
			poison(game.getL2_4(), cant);
			poison(game.getL2_5(), cant);
		} else {
			poison(game.getL1_1(), cant);
			poison(game.getL1_2(), cant);
			poison(game.getL1_3(), cant);
			poison(game.getL1_4(), cant);
			poison(game.getL1_5(), cant);
		}
	}

	private void killAll(int player, GameDTO game) {

		GameDTO game_aux = clone(game);
		if (player == 1) {
		    game.setL2_1(null);
		    game.setL2_2(null);
		    game.setL2_3(null);
		    game.setL2_4(null);
		    game.setL2_5(null);
		    if (game_aux.getL2_1() != null && game_aux.getL2_1().getCarta().getHabilidadDTO() != null &&
		        game_aux.getL2_1().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
		        execDieFunc(game.getL1_1(), game, game_aux.getL2_1(), player, game_aux.getL2_1().getCarta().getHabilidadDTO());
		    }
		    if (game_aux.getL2_2() != null && game_aux.getL2_2().getCarta().getHabilidadDTO() != null &&
		        game_aux.getL2_2().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
		        execDieFunc(game.getL1_2(), game, game_aux.getL2_2(), player, game_aux.getL2_2().getCarta().getHabilidadDTO());
		    }
		    if (game_aux.getL2_3() != null && game_aux.getL2_3().getCarta().getHabilidadDTO() != null &&
		        game_aux.getL2_3().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
		        execDieFunc(game.getL1_3(), game, game_aux.getL2_3(), player, game_aux.getL2_3().getCarta().getHabilidadDTO());
		    }
		    if (game_aux.getL2_4() != null && game_aux.getL2_4().getCarta().getHabilidadDTO() != null &&
		        game_aux.getL2_4().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
		        execDieFunc(game.getL1_4(), game, game_aux.getL2_4(), player, game_aux.getL2_4().getCarta().getHabilidadDTO());
		    }
		    if (game_aux.getL2_5() != null && game_aux.getL2_5().getCarta().getHabilidadDTO() != null &&
		        game_aux.getL2_5().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
		        execDieFunc(game.getL1_5(), game, game_aux.getL2_5(), player, game_aux.getL2_5().getCarta().getHabilidadDTO());
		    }
		} else {
		    game.setL1_1(null);
		    game.setL1_2(null);
		    game.setL1_3(null);
		    game.setL1_4(null);
		    game.setL1_5(null);
		    if (game_aux.getL1_1() != null && game_aux.getL1_1().getCarta().getHabilidadDTO() != null &&
		        game_aux.getL1_1().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
		        execDieFunc(game.getL2_1(), game, game_aux.getL1_1(), player, game_aux.getL1_1().getCarta().getHabilidadDTO());
		    }
		    if (game_aux.getL1_2() != null && game_aux.getL1_2().getCarta().getHabilidadDTO() != null &&
		        game_aux.getL1_2().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
		        execDieFunc(game.getL2_2(), game, game_aux.getL1_2(), player, game_aux.getL1_2().getCarta().getHabilidadDTO());
		    }
		    if (game_aux.getL1_3() != null && game_aux.getL1_3().getCarta().getHabilidadDTO() != null &&
		        game_aux.getL1_3().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
		        execDieFunc(game.getL2_3(), game, game_aux.getL1_3(), player, game_aux.getL1_3().getCarta().getHabilidadDTO());
		    }
		    if (game_aux.getL1_4() != null && game_aux.getL1_4().getCarta().getHabilidadDTO() != null &&
		        game_aux.getL1_4().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
		        execDieFunc(game.getL2_4(), game, game_aux.getL1_4(), player, game_aux.getL1_4().getCarta().getHabilidadDTO());
		    }
		    if (game_aux.getL1_5() != null && game_aux.getL1_5().getCarta().getHabilidadDTO() != null &&
		        game_aux.getL1_5().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
		        execDieFunc(game.getL2_5(), game, game_aux.getL1_5(), player, game_aux.getL1_5().getCarta().getHabilidadDTO());
		    }
		}
	}

	private void freezeAll(int player, GameDTO game, int cant, String name) {
		if (player == 1) {
			stun(game.getL2_1(), cant, name);
			stun(game.getL2_2(), cant, name);
			stun(game.getL2_3(), cant, name);
			stun(game.getL2_4(), cant, name);
			stun(game.getL2_5(), cant, name);
		} else {
			stun(game.getL1_1(), cant, name);
			stun(game.getL1_2(), cant, name);
			stun(game.getL1_3(), cant, name);
			stun(game.getL1_4(), cant, name);
			stun(game.getL1_5(), cant, name);
		}
	}

	private void setAllOnFire(int player, GameDTO game, int cant) {
		if (player == 1) {
			burn(game.getL2_1(), cant);
			burn(game.getL2_2(), cant);
			burn(game.getL2_3(), cant);
			burn(game.getL2_4(), cant);
			burn(game.getL2_5(), cant);
		} else {
			burn(game.getL1_1(), cant);
			burn(game.getL1_2(), cant);
			burn(game.getL1_3(), cant);
			burn(game.getL1_4(), cant);
			burn(game.getL1_5(), cant);
		}
	}

	@PostMapping("/put")
	public Response put(@ModelAttribute GameAction data) {
		GameDTO game = gs.findById(data.getGame_id());
		CartaDTO card = cs.findById(data.getCard_id());
		LineaDTO linea = gs.save(GameAction.nuevaLinea(card, game));
		LineaDTO enemy = null;
		switch (data.getLinea()) {
		case "l1_1":
			game.setL1_1(linea);
			enemy = game.getL2_1();
			break;
		case "l1_2":
			game.setL1_2(linea);
			enemy = game.getL2_2();
			break;
		case "l1_3":
			game.setL1_3(linea);
			enemy = game.getL2_3();
			break;
		case "l1_4":
			game.setL1_4(linea);
			enemy = game.getL2_4();
			break;
		case "l1_5":
			game.setL1_5(linea);
			enemy = game.getL2_5();
			break;
		case "l2_1":
			game.setL2_1(linea);
			enemy = game.getL1_1();
			break;
		case "l2_2":
			game.setL2_2(linea);
			enemy = game.getL1_2();
			break;
		case "l2_3":
			game.setL2_3(linea);
			enemy = game.getL1_3();
			break;
		case "l2_4":
			game.setL2_4(linea);
			enemy = game.getL1_4();
			break;
		case "l2_5":
			game.setL2_5(linea);
			enemy = game.getL1_5();
			break;
		}
		if (card.getHabilidadDTO().getEspecial() != null) {
			if (card.getHabilidadDTO().getEspecial().charAt(0) == 'D') {
				execDeployFunc(enemy, game, linea, data.getPlayer(), card.getHabilidadDTO());
				if (card.getHabilidadDTO().getEspecial().contains("A")) {
					saveAllLines(game, true);
				}
			}
		}
		boolean cardremoved = false;
		if (data.getPlayer() == 1) {
			if (game.getPlayer1().getCarta1() != null) {
				if (game.getPlayer1().getCarta1().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer1().setCarta1(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer1().getCarta2() != null) {
				if (game.getPlayer1().getCarta2().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer1().setCarta2(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer1().getCarta3() != null) {
				if (game.getPlayer1().getCarta3().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer1().setCarta3(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer1().getCarta4() != null) {
				if (game.getPlayer1().getCarta4().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer1().setCarta4(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer1().getCarta5() != null) {
				if (game.getPlayer1().getCarta5().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer1().setCarta5(null);
					cardremoved = true;
				}
			}
			if (card.getMana() > game.getPlayer1().getMana()) {
				return Response.error("No tienes mana");
			} else {
				game.getPlayer1().setMana(game.getPlayer1().getMana() - card.getMana());
			}
			game.setPlayer1(gs.save(game.getPlayer1()));
		} else {
			if (game.getPlayer2().getCarta1() != null) {
				if (game.getPlayer2().getCarta1().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer2().setCarta1(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer2().getCarta2() != null) {
				if (game.getPlayer2().getCarta2().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer2().setCarta2(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer2().getCarta3() != null) {
				if (game.getPlayer2().getCarta3().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer2().setCarta3(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer2().getCarta4() != null) {
				if (game.getPlayer2().getCarta4().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer2().setCarta4(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer2().getCarta5() != null) {
				if (game.getPlayer2().getCarta5().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer2().setCarta5(null);
					cardremoved = true;
				}
			}
			if (card.getMana() > game.getPlayer2().getMana()) {
				return Response.error("No tienes mana");
			} else {
				game.getPlayer2().setMana(game.getPlayer2().getMana() - card.getMana());
			}
			game.setPlayer2(gs.save(game.getPlayer2()));
		}
		gs.save(game, data.getLinea() + "/put");
		return Response.success("donete");
	}

	@PostMapping("/selfspell")
	public Response selfspell(@ModelAttribute GameAction data) {
		GameDTO game = gs.findById(data.getGame_id());
		CartaDTO card = cs.findById(data.getCard_id());
		LineaDTO linea = null;

		switch (data.getLinea()) {
		case "l1_1":
			linea = game.getL1_1();
			break;
		case "l1_2":
			linea = game.getL1_2();
			break;
		case "l1_3":
			linea = game.getL1_3();
			break;
		case "l1_4":
			linea = game.getL1_4();
			break;
		case "l1_5":
			linea = game.getL1_5();
			break;
		case "l2_1":
			linea = game.getL2_1();
			break;
		case "l2_2":
			linea = game.getL2_2();
			break;
		case "l2_3":
			linea = game.getL2_3();
			break;
		case "l2_4":
			linea = game.getL2_4();
			break;
		case "l2_5":
			linea = game.getL2_5();
			break;
		default:
			break;
		}
		if (game.getPlayer(data.getPlayer()).getMana() >= card.getMana()) {
			if (linea == null) {
				data.setLinea("player" + data.getPlayer());
				if (data.getPlayer() == 1) {
					game.getPlayer1().setVida(game.getPlayer1().getVida() + card.getHabilidadDTO().getHeal());
					if (game.getPlayer1().getVida() > 40) {
						game.getPlayer1().setVida(40);
					}
				} else {
					game.getPlayer2().setVida(game.getPlayer2().getVida() + card.getHabilidadDTO().getHeal());
					if (game.getPlayer2().getVida() > 40) {
						game.getPlayer2().setVida(40);
					}
				}
			} else {
				linea.setVida(linea.getVida() + card.getHabilidadDTO().getHeal());
				if (linea.getVida() > linea.getCarta().getVida()) {
					linea.setVida(linea.getCarta().getVida());
				}
				gs.save(linea);
			}
		}

		boolean cardremoved = false;
		if (data.getPlayer() == 1) {
			if (game.getPlayer1().getCarta1() != null) {
				if (game.getPlayer1().getCarta1().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer1().setCarta1(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer1().getCarta2() != null) {
				if (game.getPlayer1().getCarta2().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer1().setCarta2(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer1().getCarta3() != null) {
				if (game.getPlayer1().getCarta3().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer1().setCarta3(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer1().getCarta4() != null) {
				if (game.getPlayer1().getCarta4().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer1().setCarta4(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer1().getCarta5() != null) {
				if (game.getPlayer1().getCarta5().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer1().setCarta5(null);
					cardremoved = true;
				}
			}
			if (card.getMana() > game.getPlayer1().getMana()) {
				return Response.error("No tienes mana");
			} else {
				game.getPlayer1().setMana(game.getPlayer1().getMana() - card.getMana());
			}
			game.setPlayer1(gs.save(game.getPlayer1()));
		} else {
			if (game.getPlayer2().getCarta1() != null) {
				if (game.getPlayer2().getCarta1().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer2().setCarta1(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer2().getCarta2() != null) {
				if (game.getPlayer2().getCarta2().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer2().setCarta2(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer2().getCarta3() != null) {
				if (game.getPlayer2().getCarta3().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer2().setCarta3(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer2().getCarta4() != null) {
				if (game.getPlayer2().getCarta4().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer2().setCarta4(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer2().getCarta5() != null) {
				if (game.getPlayer2().getCarta5().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer2().setCarta5(null);
					cardremoved = true;
				}
			}
			if (card.getMana() > game.getPlayer2().getMana()) {
				return Response.error("No tienes mana");
			} else {
				game.getPlayer2().setMana(game.getPlayer2().getMana() - card.getMana());
			}
			game.setPlayer2(gs.save(game.getPlayer2()));
		}
		gs.save(game, data.getLinea() + "/heal");
		return Response.success("donete");
	}

	@PostMapping("/spellthrow")
	public Response spellthrow(@ModelAttribute GameAction data) {
		GameDTO game = gs.findById(data.getGame_id());
		CartaDTO card = cs.findById(data.getCard_id());
		LineaDTO linea = null;

		switch (data.getLinea()) {
		case "l1_1":
			linea = game.getL1_1();
			break;
		case "l1_2":
			linea = game.getL1_2();
			break;
		case "l1_3":
			linea = game.getL1_3();
			break;
		case "l1_4":
			linea = game.getL1_4();
			break;
		case "l1_5":
			linea = game.getL1_5();
			break;
		case "l2_1":
			linea = game.getL2_1();
			break;
		case "l2_2":
			linea = game.getL2_2();
			break;
		case "l2_3":
			linea = game.getL2_3();
			break;
		case "l2_4":
			linea = game.getL2_4();
			break;
		case "l2_5":
			linea = game.getL2_5();
			break;
		default:
			break;
		}
		if (game.getPlayer(data.getPlayer()).getMana() >= card.getMana()) {
			if (linea == null) {
				if (data.getPlayer() == 2) {
					data.setLinea("player1");
					game.getPlayer1().setVida(game.getPlayer1().getVida() - card.getHabilidadDTO().getDmg());
					if (game.getPlayer1().getVida() <= 0) {
						game = fight(game);
						return Response.success("donete");
					}
					gs.save(game.getPlayer1());
				} else {
					data.setLinea("player2");
					game.getPlayer2().setVida(game.getPlayer2().getVida() - card.getHabilidadDTO().getDmg());
					if (game.getPlayer2().getVida() <= 0) {
						game = fight(game);
						return Response.success("donete");
					}
					gs.save(game.getPlayer2());
				}
			} else {
				HabilidadDTO hab = card.getHabilidadDTO();

				if (hab.getFreeze() != null && linea.getStun() == null) {
					linea.setStun(hab.getFreeze());
					linea.setStun_name(hab.getFreezeName());
				}
				if (hab.getBurn() != null) {
					linea.setBurn(hab.getBurn());
				}
				if (hab.getPoisn() != null) {
					linea.setPoisn(hab.getPoisn());
				}
				if (hab.getBleed() != null && (linea.getBleed() == null || linea.getBleed() < hab.getBleed())) {
					linea.setBleed(hab.getBleed());
				}
				if (hab.getPrcntDwn() != null && linea.getPrcnt_dwn() < hab.getPrcntDwn()) {
					linea.setPrcnt_dwn(hab.getPrcntDwn());
				}
				linea.setVida(linea.getVida() - card.getHabilidadDTO().getDmg());
				if (hab.getEspecial() != null) {
					if (hab.getEspecial().equals("K")) {
						linea.setVida(-1);
					}
				}
				if (linea.getVida() <= 0) {
					gs.deleteLinea(linea);
					if (game.getL1_1() != null) {
						if (game.getL1_1().getId() == linea.getId()) {
							if (game.getL1_1().getCarta().getHabilidadDTO().getEspecial() != null &&
								game.getL1_1().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
								execDieFunc(game.getL2_1(), game, game.getL1_1(), 2, game.getL1_1().getCarta().getHabilidadDTO());
							}
							game.setL1_1(null);
						}
					}
					if (game.getL1_2() != null) {
						if (game.getL1_2().getId() == linea.getId()) {
							if (game.getL1_2().getCarta().getHabilidadDTO().getEspecial() != null &&
								game.getL1_2().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
								execDieFunc(game.getL2_2(), game, game.getL1_2(), 2, game.getL1_2().getCarta().getHabilidadDTO());
							}
							game.setL1_2(null);
						}
					}
					if (game.getL1_3() != null) {
						if (game.getL1_3().getId() == linea.getId()) {
							if (game.getL1_3().getCarta().getHabilidadDTO().getEspecial() != null &&
								game.getL1_3().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
								execDieFunc(game.getL2_3(), game, game.getL1_3(), 2, game.getL1_3().getCarta().getHabilidadDTO());
							}
							game.setL1_3(null);
						}
					}
					if (game.getL1_4() != null) {
						if (game.getL1_4().getId() == linea.getId()) {
							if (game.getL1_4().getCarta().getHabilidadDTO().getEspecial() != null &&
								game.getL1_4().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
								execDieFunc(game.getL2_4(), game, game.getL1_4(), 2, game.getL1_4().getCarta().getHabilidadDTO());
							}
							game.setL1_4(null);
						}
					}
					if (game.getL1_5() != null) {
						if (game.getL1_5().getId() == linea.getId()) {
							if (game.getL1_5().getCarta().getHabilidadDTO().getEspecial() != null &&
								game.getL1_5().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
								execDieFunc(game.getL2_5(), game, game.getL1_5(), 2, game.getL1_5().getCarta().getHabilidadDTO());
							}
							game.setL1_5(null);
						}
					}
					if (game.getL2_1() != null) {
						if (game.getL2_1().getId() == linea.getId()) {
							if (game.getL2_1().getCarta().getHabilidadDTO().getEspecial() != null &&
								game.getL2_1().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
								execDieFunc(game.getL1_1(), game, game.getL2_1(), 1, game.getL2_1().getCarta().getHabilidadDTO());
							}
							game.setL2_1(null);
						}
					}
					if (game.getL2_2() != null) {
						if (game.getL2_2().getId() == linea.getId()) {
							if (game.getL2_2().getCarta().getHabilidadDTO().getEspecial() != null &&
								game.getL2_2().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
								execDieFunc(game.getL1_2(), game, game.getL2_2(), 1, game.getL2_2().getCarta().getHabilidadDTO());
							}
							game.setL2_2(null);
						}
					}
					if (game.getL2_3() != null) {
						if (game.getL2_3().getId() == linea.getId()) {
							if (game.getL2_3().getCarta().getHabilidadDTO().getEspecial() != null &&
								game.getL2_3().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
								execDieFunc(game.getL1_3(), game, game.getL2_3(), 1, game.getL2_3().getCarta().getHabilidadDTO());
							}
							game.setL2_3(null);
						}
					}
					if (game.getL2_4() != null) {
						if (game.getL2_4().getId() == linea.getId()) {
							if (game.getL2_4().getCarta().getHabilidadDTO().getEspecial() != null &&
								game.getL2_4().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
								execDieFunc(game.getL1_4(), game, game.getL2_4(), 1, game.getL2_4().getCarta().getHabilidadDTO());
							}
							game.setL2_4(null);
						}
					}
					if (game.getL2_5() != null) {
						if (game.getL2_5().getId() == linea.getId()) {
							if (game.getL2_5().getCarta().getHabilidadDTO().getEspecial() != null &&
								game.getL2_5().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
								execDieFunc(game.getL1_5(), game, game.getL2_5(), 1, game.getL2_5().getCarta().getHabilidadDTO());
							}
							game.setL2_5(null);
						}
					}
				} else {
					gs.save(linea);
				}
			}
		}

		boolean cardremoved = false;
		if (data.getPlayer() == 1) {
			if (game.getPlayer1().getCarta1() != null) {
				if (game.getPlayer1().getCarta1().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer1().setCarta1(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer1().getCarta2() != null) {
				if (game.getPlayer1().getCarta2().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer1().setCarta2(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer1().getCarta3() != null) {
				if (game.getPlayer1().getCarta3().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer1().setCarta3(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer1().getCarta4() != null) {
				if (game.getPlayer1().getCarta4().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer1().setCarta4(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer1().getCarta5() != null) {
				if (game.getPlayer1().getCarta5().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer1().setCarta5(null);
					cardremoved = true;
				}
			}
			if (card.getMana() > game.getPlayer1().getMana()) {
				return Response.error("No tienes mana");
			} else {
				game.getPlayer1().setMana(game.getPlayer1().getMana() - card.getMana());
			}
			game.setPlayer1(gs.save(game.getPlayer1()));
		} else {
			if (game.getPlayer2().getCarta1() != null) {
				if (game.getPlayer2().getCarta1().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer2().setCarta1(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer2().getCarta2() != null) {
				if (game.getPlayer2().getCarta2().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer2().setCarta2(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer2().getCarta3() != null) {
				if (game.getPlayer2().getCarta3().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer2().setCarta3(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer2().getCarta4() != null) {
				if (game.getPlayer2().getCarta4().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer2().setCarta4(null);
					cardremoved = true;
				}
			}
			if (game.getPlayer2().getCarta5() != null) {
				if (game.getPlayer2().getCarta5().getId() == data.getCard_id() && !cardremoved) {
					game.getPlayer2().setCarta5(null);
					cardremoved = true;
				}
			}
			if (card.getMana() > game.getPlayer2().getMana()) {
				return Response.error("No tienes mana");
			} else {
				game.getPlayer2().setMana(game.getPlayer2().getMana() - card.getMana());
			}
			game.setPlayer2(gs.save(game.getPlayer2()));
		}
		gs.save(game, data.getLinea() + "/dmg");
		return Response.success("donete");
	}

	@PostMapping("/disconnect")
	public Response dc(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		GameDTO game = gs.findById(data.getUser_id());
		if (game.getStatus().equals("activo")) {
			if (game.getPlayer1().getUsuario().getId() == user.getId()) {
				game.getPlayer1().setVida(0);
			} else {
				game.getPlayer2().setVida(0);
			}
			game = fight(game);
			gs.save(game, true);
		}
		return Response.success("donete");
	}

	@PostMapping("/switch")
	public Response switchturn(@ModelAttribute GameAction data) {
		GameDTO game = gs.findById(data.getGame_id());
		switch (game.getTurno()) {
		case 1:
			game.setTurno(2);
			gs.save(game, true);
			break;
		case 2:
			String tdmgs = takedmg(game);
			game = turnDMGs(game);
			game.setTurno(3);
			gs.save(game, "turndmg" + tdmgs);
			break;
		case 3:
			if (game.getP2_c() == null) {
				if (data.getPlayer() == 1) {
					game.setP1_c(true);
					gs.save(game, true);
				} else {
					game.setP2_c(true);
					gs.save(game);
				}
			}
			if (game.getP1_c() != null && game.getP2_c() != null) {
				if (game.getP1_c() && game.getP2_c()) {
					game = fight(game);
					if (game.getPlayer1().getVida() <= 0) {
						game = fight(game);
					}
					if (game.getPlayer2().getVida() <= 0) {
						game = fight(game);
					}
					if (game.getStatus().equals("activo")) {
						PlayerStatusDTO p1 = game.getPlayer1();
						p1.setMana(p1.getMana() + 1);
						PlayerStatusDTO p2 = game.getPlayer2();
						p2.setMana(p2.getMana() + 1);
						p1 = GameAction.drawCard(p1);
						p2 = GameAction.drawCard(p2);
						gs.save(p1);
						gs.save(p2);
						game.setTurno(1);
						game.setP1_c(null);
						game.setP2_c(null);
					}
					gs.save(game, true);
				}
			}

			break;
		}

		return Response.success("donete");
	}
	
	private String checktdmgs(String tstring,LineaDTO linea) {
		boolean alr = false;
		if (linea != null) {
			if (linea.getBleed() != null && linea.getBleed() > 0 && !alr) {
				alr = true;
				tstring += "/"+ linea.getId();
			}
			if (linea.getBurn() != null && linea.getBurn() > 0 && !alr) {
				alr = true;
				tstring += "/"+ linea.getId();
			}
			if (linea.getPoisn() != null && linea.getPoisn() > 0 && !alr) {
				alr = true;
				tstring += "/"+ linea.getId();
			}
			
		}
		return tstring;
	}
	
	private String takedmg(GameDTO game) {
		String dmgs = "";
		dmgs = checktdmgs(dmgs, game.getL1_1());
		dmgs = checktdmgs(dmgs, game.getL1_2());
		dmgs = checktdmgs(dmgs, game.getL1_3());
		dmgs = checktdmgs(dmgs, game.getL1_4());
		dmgs = checktdmgs(dmgs, game.getL1_5());
		dmgs = checktdmgs(dmgs, game.getL2_1());
		dmgs = checktdmgs(dmgs, game.getL2_2());
		dmgs = checktdmgs(dmgs, game.getL2_3());
		dmgs = checktdmgs(dmgs, game.getL2_4());
		dmgs = checktdmgs(dmgs, game.getL2_5());
		return dmgs;
	}

	// Función para aplicar Poison a una línea
	private void aplicarPoison(LineaDTO linea, GameDTO game) {
		if (linea != null && linea.getPoisn() != null && linea.getPoisn() > 0) {
			linea.setVida(linea.getVida() - 1);
			linea.setPoisn(linea.getPoisn() - 1);
			if (linea.getVida() <= 0) {
				linea.setVida(1);
			}
			if (linea.getPoisn() <= 0) {
				linea.setPoisn(null);
			}
			gs.save(linea);
		}
	}

	// Función para aplicar Bleed a una línea
	private void aplicarBleed(LineaDTO linea, GameDTO game) {
		if (linea != null && linea.getBleed() != null && linea.getBleed() > 0) {
			linea.setVida(linea.getVida() - linea.getBleed());
			if (linea.getVida() <= 0) {
				eliminarLinea(game, linea);
			} else {
				gs.save(linea);
			}
		}
	}

	// Función para aplicar Burn a una línea
	private void aplicarBurn(LineaDTO linea, GameDTO game) {
		if (linea != null && linea.getBurn() != null && linea.getBurn() > 0) {
			linea.setVida(linea.getVida() - 1);
			linea.setBurn(linea.getBurn() - 1);
			if (linea.getVida() <= 0) {
				eliminarLinea(game, linea);
			} else {
				gs.save(linea);
			}
			if (linea.getBurn() <= 0) {
				linea.setBurn(null);
			}
		}
	}

	// Función para procesar los criticos

	private void aplicarCrit(LineaDTO linea) {
		if (linea != null) {
			if (linea.getCarta().getHabilidadDTO().getCrit() != null) {
				if (linea.getCarta().getHabilidadDTO().getCrit() > 0) {
					Random rand = new Random();
					if (rand.nextInt(100) < linea.getCarta().getHabilidadDTO().getCrit()) {
						linea.setWillcrit(true);
						gs.save(linea);
					}
				}
			}
		}
	}

	// Función para eliminar una línea cuando su vida llega a 0
	private void eliminarLinea(GameDTO game, LineaDTO linea) {
		if (linea != null) {
			if (linea == game.getL1_1()) {
				if (game.getL1_1().getCarta().getHabilidadDTO().getEspecial() != null &&
					game.getL1_1().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
					execDieFunc(game.getL2_1(), game, linea, 1, linea.getCarta().getHabilidadDTO());
				}
				game.setL1_1(null);
			} else if (linea == game.getL1_2()) {
				if (game.getL1_2().getCarta().getHabilidadDTO().getEspecial() != null &&
					game.getL1_2().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
					execDieFunc(game.getL2_2(), game, linea, 1, linea.getCarta().getHabilidadDTO());
				}
				game.setL1_2(null);
			} else if (linea == game.getL1_3()) {
				if (game.getL1_3().getCarta().getHabilidadDTO().getEspecial() != null &&
					game.getL1_3().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
					execDieFunc(game.getL2_3(), game, linea, 1, linea.getCarta().getHabilidadDTO());
				}
				game.setL1_3(null);
			} else if (linea == game.getL1_4()) {
				if (game.getL1_4().getCarta().getHabilidadDTO().getEspecial() != null &&
					game.getL1_4().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
					execDieFunc(game.getL2_4(), game, linea, 1, linea.getCarta().getHabilidadDTO());
				}
				game.setL1_4(null);
			} else if (linea == game.getL1_5()) {
				if (game.getL1_5().getCarta().getHabilidadDTO().getEspecial() != null &&
					game.getL1_5().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
					execDieFunc(game.getL2_5(), game, linea, 1, linea.getCarta().getHabilidadDTO());
				}
				game.setL1_5(null);
			} else if (linea == game.getL2_1()) {
				if (game.getL2_1().getCarta().getHabilidadDTO().getEspecial() != null &&
					game.getL2_1().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
					execDieFunc(game.getL1_1(), game, linea, 2, linea.getCarta().getHabilidadDTO());
				}
				game.setL2_1(null);
			} else if (linea == game.getL2_2()) {
				if (game.getL2_2().getCarta().getHabilidadDTO().getEspecial() != null &&
					game.getL2_2().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
					execDieFunc(game.getL1_2(), game, linea, 2, linea.getCarta().getHabilidadDTO());
				}
				game.setL2_2(null);
			} else if (linea == game.getL2_3()) {
				if (game.getL2_3().getCarta().getHabilidadDTO().getEspecial() != null &&
					game.getL2_3().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
					execDieFunc(game.getL1_3(), game, linea, 2, linea.getCarta().getHabilidadDTO());
				}
				game.setL2_3(null);
			} else if (linea == game.getL2_4()) {
				if (game.getL2_4().getCarta().getHabilidadDTO().getEspecial() != null &&
					game.getL2_4().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
					execDieFunc(game.getL1_4(), game, linea, 2, linea.getCarta().getHabilidadDTO());
				}
				game.setL2_4(null);
			} else if (linea == game.getL2_5()) {
				if (game.getL2_5().getCarta().getHabilidadDTO().getEspecial() != null &&
					game.getL2_5().getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
					execDieFunc(game.getL1_5(), game, linea, 2, linea.getCarta().getHabilidadDTO());
				}
				game.setL2_5(null);
			}
		}
	}

	// Función principal para ejecutar los efectos en todas las líneas
	private GameDTO turnDMGs(GameDTO game_aux) {
		GameDTO game = game_aux;
		// Ejecutar en todas las líneas de L1 y L2
		aplicarPoison(game.getL1_1(), game);
		aplicarPoison(game.getL1_2(), game);
		aplicarPoison(game.getL1_3(), game);
		aplicarPoison(game.getL1_4(), game);
		aplicarPoison(game.getL1_5(), game);

		aplicarPoison(game.getL2_1(), game);
		aplicarPoison(game.getL2_2(), game);
		aplicarPoison(game.getL2_3(), game);
		aplicarPoison(game.getL2_4(), game);
		aplicarPoison(game.getL2_5(), game);

		// Aplicar Bleed
		aplicarBleed(game.getL1_1(), game);
		aplicarBleed(game.getL1_2(), game);
		aplicarBleed(game.getL1_3(), game);
		aplicarBleed(game.getL1_4(), game);
		aplicarBleed(game.getL1_5(), game);

		aplicarBleed(game.getL2_1(), game);
		aplicarBleed(game.getL2_2(), game);
		aplicarBleed(game.getL2_3(), game);
		aplicarBleed(game.getL2_4(), game);
		aplicarBleed(game.getL2_5(), game);

		// Aplicar Burn
		aplicarBurn(game.getL1_1(), game);
		aplicarBurn(game.getL1_2(), game);
		aplicarBurn(game.getL1_3(), game);
		aplicarBurn(game.getL1_4(), game);
		aplicarBurn(game.getL1_5(), game);

		aplicarBurn(game.getL2_1(), game);
		aplicarBurn(game.getL2_2(), game);
		aplicarBurn(game.getL2_3(), game);
		aplicarBurn(game.getL2_4(), game);
		aplicarBurn(game.getL2_5(), game);

		aplicarCrit(game.getL1_1());
		aplicarCrit(game.getL1_2());
		aplicarCrit(game.getL1_3());
		aplicarCrit(game.getL1_4());
		aplicarCrit(game.getL1_5());
		aplicarCrit(game.getL2_1());
		aplicarCrit(game.getL2_2());
		aplicarCrit(game.getL2_3());
		aplicarCrit(game.getL2_4());
		aplicarCrit(game.getL2_5());
		// Guardar cambios si es necesario
		return game;
	}
	
	private void killLine(String posicionOponente, GameDTO game) {
		switch (posicionOponente) {
		case "1_1":
			game.setL1_1(null);
			break;
		case "1_2":
			game.setL1_2(null);
			break;
		case "1_3":
			game.setL1_3(null);
			break;
		case "1_4":
			game.setL1_4(null);
			break;
		case "1_5":
			game.setL1_5(null);
			break;
		case "2_1":
			game.setL2_1(null);
			break;
		case "2_2":
			game.setL2_2(null);
			break;
		case "2_3":
			game.setL2_3(null);
			break;
		case "2_4":
			game.setL2_4(null);
			break;
		case "2_5":
			game.setL2_5(null);
			break;
		}
	}

	private void procesarLinea(LineaDTO linea_own, LineaDTO linea, String posicionOponente, GameDTO game,
			GameDTO game_aux, PlayerStatusDTO player2, LineaDTO linea_aux) {
		if (linea != null) {
			if (linea_own.getCarta().getHabilidadDTO().getLeth() != null && linea.getVida() > 0) {
				linea.setVida(linea.getVida() - ((linea.getCarta().getVida()
						- linea.getVida() * linea_own.getCarta().getHabilidadDTO().getLeth()) / 100));
			}
			if (linea_own.getWillcrit() != null && linea_own.getWillcrit() == true) {
				linea.setVida(linea.getVida() - (linea_own.getCarta().getHabilidadDTO().getDmg()
						+ (linea_own.getCarta().getHabilidadDTO().getDmg()
								* linea_own.getCarta().getHabilidadDTO().getCritMult() / 100)));
				linea_own.setWillcrit(false);
			} else {
				linea.setVida(linea.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
			}

			if (linea_own.getCarta().getHabilidadDTO().getPrcnt() != null) {
				linea.setVida(linea.getVida()
						- ((linea.getCarta().getVida() * linea_own.getCarta().getHabilidadDTO().getPrcnt()) / 100));
			}

			if (linea_own.getCarta().getHabilidadDTO().getEspecial() != null) {
				if (linea_own.getCarta().getHabilidadDTO().getEspecial().equals("K")) {
					linea.setVida(-1);
				}
			}

			if (linea.getVida() <= 0) {
				if (linea.getCarta().getHabilidadDTO().getEspecial() != null) {
					if (linea.getCarta().getHabilidadDTO().getEspecial().charAt(0) == 'T') {
						int player = 0;
						String pos = "";
						if (posicionOponente.startsWith("1")) {
							player = 2;
							pos = "2";
						} else {
							player = 1;
							pos = "1";
						}
						execDieFunc(linea_aux, game, linea, player, linea.getCarta().getHabilidadDTO());
						if (linea_aux != null) {
							if (linea_aux.getVida() <= 0) {
								pos += "_";
								pos += posicionOponente.charAt(posicionOponente.length()-1);
								killLine(pos, game);
							}
						}
					}
				}
				switch (posicionOponente) {
				case "1_1":
					game.setL1_1(null);
					break;
				case "1_2":
					game.setL1_2(null);
					break;
				case "1_3":
					game.setL1_3(null);
					break;
				case "1_4":
					game.setL1_4(null);
					break;
				case "1_5":
					game.setL1_5(null);
					break;
				case "2_1":
					game.setL2_1(null);
					break;
				case "2_2":
					game.setL2_2(null);
					break;
				case "2_3":
					game.setL2_3(null);
					break;
				case "2_4":
					game.setL2_4(null);
					break;
				case "2_5":
					game.setL2_5(null);
					break;
				}
			} else {
				// Aplicar efectos
				HabilidadDTO hab = linea_own.getCarta().getHabilidadDTO();

				if (hab.getFreeze() != null && linea.getStun() == null) {
					linea.setStun(hab.getFreeze());
					linea.setStun_name(hab.getFreezeName());
				}
				if (hab.getBurn() != null) {
					linea.setBurn(hab.getBurn());
				}
				if (hab.getPoisn() != null) {
					linea.setPoisn(hab.getPoisn());
				}
				if (hab.getBleed() != null && (linea.getBleed() == null || linea.getBleed() < hab.getBleed())) {
					linea.setBleed(hab.getBleed());
				}
				if (hab.getPrcntDwn() != null && linea.getPrcnt_dwn() < hab.getPrcntDwn()) {
					linea.setPrcnt_dwn(hab.getPrcntDwn());
				}
			}
		} else {
			if (linea_own.getCarta().getHabilidadDTO().getLeth() != null && player2.getVida() > 0) {
				player2.setVida(player2.getVida()
						- (((40 - player2.getVida()) * linea_own.getCarta().getHabilidadDTO().getLeth()) / 100));
			}
			if (linea_own.getWillcrit() != null && linea_own.getWillcrit() == true) {
				player2.setVida(player2.getVida() - (linea_own.getCarta().getHabilidadDTO().getDmg()
						+ ((linea_own.getCarta().getHabilidadDTO().getDmg()
								* linea_own.getCarta().getHabilidadDTO().getCritMult()) / 100)));
				linea_own.setWillcrit(false);
			} else {
				player2.setVida(player2.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
			}
			if (linea_own.getCarta().getHabilidadDTO().getPrcnt() != null) {
				player2.setVida(player2.getVida() - ((40 * linea_own.getCarta().getHabilidadDTO().getPrcnt()) / 100));
			}
		}
	}

	private void ejecutarLinea(String posicion, GameDTO game, GameDTO game_aux, PlayerStatusDTO player2) {
		LineaDTO linea_own = null;
		LineaDTO linea_aux = null;
		LineaDTO linea = null;

		switch (posicion) {
		case "1_1":
			linea_own = game_aux.getL1_1();
			linea_aux = game.getL1_1();
			linea = game.getL2_1();
			break;
		case "1_2":
			linea_own = game_aux.getL1_2();
			linea = game.getL2_2();
			linea_aux = game.getL1_2();
			break;
		case "1_3":
			linea_own = game_aux.getL1_3();
			linea = game.getL2_3();
			linea_aux = game.getL1_3();
			break;
		case "1_4":
			linea_own = game_aux.getL1_4();
			linea = game.getL2_4();
			linea_aux = game.getL1_4();
			break;
		case "1_5":
			linea_own = game_aux.getL1_5();
			linea = game.getL2_5();
			linea_aux = game.getL1_5();
			break;
		case "2_1":
			linea_own = game_aux.getL2_1();
			linea = game.getL1_1();
			linea_aux = game.getL2_1();
			break;
		case "2_2":
			linea_own = game_aux.getL2_2();
			linea = game.getL1_2();
			linea_aux = game.getL2_2();
			break;
		case "2_3":
			linea_own = game_aux.getL2_3();
			linea = game.getL1_3();
			linea_aux = game.getL2_3();
			break;
		case "2_4":
			linea_own = game_aux.getL2_4();
			linea = game.getL1_4();
			linea_aux = game.getL2_4();
			break;
		case "2_5":
			linea_own = game_aux.getL2_5();
			linea = game.getL1_5();
			linea_aux = game.getL2_5();
			break;
		}

		if (linea_own != null) {
			if (linea_own.getStun() == null || linea_own.getStun() <= 0) {
				procesarLinea(linea_own, linea,
						posicion.startsWith("1") ? "2_" + posicion.split("_")[1] : "1_" + posicion.split("_")[1], game,
						game_aux, player2, linea_aux);
			}
		}
	}

	private GameDTO clone(GameDTO game) { // Hago esto porque java es mas tiquismiquis y no existe macho que palo
		// Copiar L1_1 a L1_5
		LineaDTO l1_1 = null;
		if (game.getL1_1() != null) {
			l1_1 = LineaDTO.builder().vida(game.getL1_1().getVida()).carta(game.getL1_1().getCarta())
					.stun(game.getL1_1().getStun()).stun_name(game.getL1_1().getStun_name())
					.burn(game.getL1_1().getBurn()).bleed(game.getL1_1().getBleed())
					.prcnt_dwn(game.getL1_1().getPrcnt_dwn()).prcnt_up(game.getL1_1().getPrcnt_up())
					.willcrit(game.getL1_1().getWillcrit()).build();
		}

		LineaDTO l1_2 = null;
		if (game.getL1_2() != null) {
			l1_2 = LineaDTO.builder().vida(game.getL1_2().getVida()).carta(game.getL1_2().getCarta())
					.stun(game.getL1_2().getStun()).stun_name(game.getL1_2().getStun_name())
					.burn(game.getL1_2().getBurn()).bleed(game.getL1_2().getBleed())
					.prcnt_dwn(game.getL1_2().getPrcnt_dwn()).prcnt_up(game.getL1_2().getPrcnt_up())
					.willcrit(game.getL1_2().getWillcrit()).build();
		}

		LineaDTO l1_3 = null;
		if (game.getL1_3() != null) {
			l1_3 = LineaDTO.builder().vida(game.getL1_3().getVida()).carta(game.getL1_3().getCarta())
					.stun(game.getL1_3().getStun()).stun_name(game.getL1_3().getStun_name())
					.burn(game.getL1_3().getBurn()).bleed(game.getL1_3().getBleed())
					.prcnt_dwn(game.getL1_3().getPrcnt_dwn()).prcnt_up(game.getL1_3().getPrcnt_up())
					.willcrit(game.getL1_3().getWillcrit()).build();
		}

		LineaDTO l1_4 = null;
		if (game.getL1_4() != null) {
			l1_4 = LineaDTO.builder().vida(game.getL1_4().getVida()).carta(game.getL1_4().getCarta())
					.stun(game.getL1_4().getStun()).stun_name(game.getL1_4().getStun_name())
					.burn(game.getL1_4().getBurn()).bleed(game.getL1_4().getBleed())
					.prcnt_dwn(game.getL1_4().getPrcnt_dwn()).prcnt_up(game.getL1_4().getPrcnt_up())
					.willcrit(game.getL1_4().getWillcrit()).build();
		}

		LineaDTO l1_5 = null;
		if (game.getL1_5() != null) {
			l1_5 = LineaDTO.builder().vida(game.getL1_5().getVida()).carta(game.getL1_5().getCarta())
					.stun(game.getL1_5().getStun()).stun_name(game.getL1_5().getStun_name())
					.burn(game.getL1_5().getBurn()).bleed(game.getL1_5().getBleed())
					.prcnt_dwn(game.getL1_5().getPrcnt_dwn()).prcnt_up(game.getL1_5().getPrcnt_up())
					.willcrit(game.getL1_5().getWillcrit()).build();
		}

		LineaDTO l2_1 = null;
		if (game.getL2_1() != null) {
			l2_1 = LineaDTO.builder().vida(game.getL2_1().getVida()).carta(game.getL2_1().getCarta())
					.stun(game.getL2_1().getStun()).stun_name(game.getL2_1().getStun_name())
					.burn(game.getL2_1().getBurn()).bleed(game.getL2_1().getBleed())
					.prcnt_dwn(game.getL2_1().getPrcnt_dwn()).prcnt_up(game.getL2_1().getPrcnt_up())
					.willcrit(game.getL2_1().getWillcrit()).build();
		}

		LineaDTO l2_2 = null;
		if (game.getL2_2() != null) {
			l2_2 = LineaDTO.builder().vida(game.getL2_2().getVida()).carta(game.getL2_2().getCarta())
					.stun(game.getL2_2().getStun()).stun_name(game.getL2_2().getStun_name())
					.burn(game.getL2_2().getBurn()).bleed(game.getL2_2().getBleed())
					.prcnt_dwn(game.getL2_2().getPrcnt_dwn()).prcnt_up(game.getL2_2().getPrcnt_up())
					.willcrit(game.getL2_2().getWillcrit()).build();
		}

		LineaDTO l2_3 = null;
		if (game.getL2_3() != null) {
			l2_3 = LineaDTO.builder().vida(game.getL2_3().getVida()).carta(game.getL2_3().getCarta())
					.stun(game.getL2_3().getStun()).stun_name(game.getL2_3().getStun_name())
					.burn(game.getL2_3().getBurn()).bleed(game.getL2_3().getBleed())
					.prcnt_dwn(game.getL2_3().getPrcnt_dwn()).prcnt_up(game.getL2_3().getPrcnt_up())
					.willcrit(game.getL2_3().getWillcrit()).build();
		}

		LineaDTO l2_4 = null;
		if (game.getL2_4() != null) {
			l2_4 = LineaDTO.builder().vida(game.getL2_4().getVida()).carta(game.getL2_4().getCarta())
					.stun(game.getL2_4().getStun()).stun_name(game.getL2_4().getStun_name())
					.burn(game.getL2_4().getBurn()).bleed(game.getL2_4().getBleed())
					.prcnt_dwn(game.getL2_4().getPrcnt_dwn()).prcnt_up(game.getL2_4().getPrcnt_up())
					.willcrit(game.getL2_4().getWillcrit()).build();
		}

		LineaDTO l2_5 = null;
		if (game.getL2_5() != null) {
			l2_5 = LineaDTO.builder().vida(game.getL2_5().getVida()).carta(game.getL2_5().getCarta())
					.stun(game.getL2_5().getStun()).stun_name(game.getL2_5().getStun_name())
					.burn(game.getL2_5().getBurn()).bleed(game.getL2_5().getBleed())
					.prcnt_dwn(game.getL2_5().getPrcnt_dwn()).prcnt_up(game.getL2_5().getPrcnt_up())
					.willcrit(game.getL2_5().getWillcrit()).build();
		}

		GameDTO newgame = GameDTO.builder().l1_1(l1_1).l1_2(l1_2).l1_3(l1_3).l1_4(l1_4).l1_5(l1_5).l2_1(l2_1).l2_2(l2_2)
				.l2_3(l2_3).l2_4(l2_4).l2_5(l2_5).build();
		return newgame;
	}

	private GameDTO fight(GameDTO game) {
		GameDTO game_aux = clone(game);
		if (game.getPlayer1().getVida() <= 0) {
			game.setStatus("winner: " + game.getPlayer2().getUsuario().getUsername());
			game.setL1_1(null);
			game.setL1_2(null);
			game.setL1_3(null);
			game.setL1_4(null);
			game.setL1_5(null);
			game.setL2_1(null);
			game.setL2_2(null);
			game.setL2_3(null);
			game.setL2_4(null);
			game.setL2_5(null);
			gs.save(game);
			gs.deleteAllLines(game.getId());
			ns.win(game.getPlayer2().getUsuario());
			return game;
		}
		if (game.getPlayer2().getVida() <= 0) {
			game.setStatus("winner: " + game.getPlayer1().getUsuario().getUsername());
			game.setL1_1(null);
			game.setL1_2(null);
			game.setL1_3(null);
			game.setL1_4(null);
			game.setL1_5(null);
			game.setL2_1(null);
			game.setL2_2(null);
			game.setL2_3(null);
			game.setL2_4(null);
			game.setL2_5(null);
			gs.save(game);
			gs.deleteAllLines(game.getId());
			ns.win(game.getPlayer1().getUsuario());
			return game;
		}
		ejecutarLinea("1_1", game, game_aux, game.getPlayer2());
		ejecutarLinea("1_2", game, game_aux, game.getPlayer2());
		ejecutarLinea("1_3", game, game_aux, game.getPlayer2());
		ejecutarLinea("1_4", game, game_aux, game.getPlayer2());
		ejecutarLinea("1_5", game, game_aux, game.getPlayer2());
		ejecutarLinea("2_1", game, game_aux, game.getPlayer1());
		ejecutarLinea("2_2", game, game_aux, game.getPlayer1());
		ejecutarLinea("2_3", game, game_aux, game.getPlayer1());
		ejecutarLinea("2_4", game, game_aux, game.getPlayer1());
		ejecutarLinea("2_5", game, game_aux, game.getPlayer1());

		saveAllLines(game);

		return game;
	}

	private void cleanAndSave(LineaDTO linea) {
		if (linea == null)
			return;

		if (linea.getWillcrit() != null) {
			linea.setWillcrit(null);
		}

		if (linea.getStun() != null) {
			linea.setStun(linea.getStun() - 1);
			if (linea.getStun() <= 0) {
				linea.setStun(null);
				linea.setStun_name(null);
			}
		}

		if (linea.getCarta().getHabilidadDTO().getHeal() != 0 && linea.getCarta().getHabilidadDTO().getHeal() != null) {
			Integer vida = linea.getVida() + linea.getCarta().getHabilidadDTO().getHeal();
			linea.setVida(vida > linea.getCarta().getVida() ? linea.getCarta().getVida() : vida);
		}

		gs.save(linea);
	}

	private void saveAllLines(GameDTO game) {
		cleanAndSave(game.getL1_1());
		cleanAndSave(game.getL1_2());
		cleanAndSave(game.getL1_3());
		cleanAndSave(game.getL1_4());
		cleanAndSave(game.getL1_5());
		cleanAndSave(game.getL2_1());
		cleanAndSave(game.getL2_2());
		cleanAndSave(game.getL2_3());
		cleanAndSave(game.getL2_4());
		cleanAndSave(game.getL2_5());
	}
	private void saveAllLines(GameDTO game, boolean noclean) {
		if (noclean) {
			if (game.getL1_1() != null) {gs.save(game.getL1_1());}
			if (game.getL1_1() != null) { gs.save(game.getL1_1()); }
			if (game.getL1_2() != null) { gs.save(game.getL1_2()); }
			if (game.getL1_3() != null) { gs.save(game.getL1_3()); }
			if (game.getL1_4() != null) { gs.save(game.getL1_4()); }
			if (game.getL1_5() != null) { gs.save(game.getL1_5()); }

			if (game.getL2_1() != null) { gs.save(game.getL2_1()); }
			if (game.getL2_2() != null) { gs.save(game.getL2_2()); }
			if (game.getL2_3() != null) { gs.save(game.getL2_3()); }
			if (game.getL2_4() != null) { gs.save(game.getL2_4()); }
			if (game.getL2_5() != null) { gs.save(game.getL2_5()); }
		} else {
			cleanAndSave(game.getL1_1());
			cleanAndSave(game.getL1_2());
			cleanAndSave(game.getL1_3());
			cleanAndSave(game.getL1_4());
			cleanAndSave(game.getL1_5());
			cleanAndSave(game.getL2_1());
			cleanAndSave(game.getL2_2());
			cleanAndSave(game.getL2_3());
			cleanAndSave(game.getL2_4());
			cleanAndSave(game.getL2_5());
		}
	}
	private void saveAllLines(GameDTO game, LineaDTO nosave) {
			if (game.getL1_1() != nosave) {gs.save(game.getL1_1());}
			if (game.getL1_1() != nosave) { gs.save(game.getL1_1()); }
			if (game.getL1_2() != nosave) { gs.save(game.getL1_2()); }
			if (game.getL1_3() != nosave) { gs.save(game.getL1_3()); }
			if (game.getL1_4() != nosave) { gs.save(game.getL1_4()); }
			if (game.getL1_5() != nosave) { gs.save(game.getL1_5()); }

			if (game.getL2_1() != nosave) { gs.save(game.getL2_1()); }
			if (game.getL2_2() != nosave) { gs.save(game.getL2_2()); }
			if (game.getL2_3() != nosave) { gs.save(game.getL2_3()); }
			if (game.getL2_4() != nosave) { gs.save(game.getL2_4()); }
			if (game.getL2_5() != nosave) { gs.save(game.getL2_5()); }
	}

}
