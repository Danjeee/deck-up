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
import com.javi.deckup.service.NotificationService;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.GameAction;
import com.javi.deckup.utils.Response;
import com.javi.deckup.utils.UserAction;

import java.util.ArrayList;
import java.util.List;

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

	@PostMapping("/put")
	public Response put(@ModelAttribute GameAction data) {
		GameDTO game = gs.findById(data.getGame_id());
		CartaDTO card = cs.findById(data.getCard_id());
		LineaDTO linea = gs.save(GameAction.nuevaLinea(card, game));
		switch (data.getLinea()) {
		case "l1_1":
			game.setL1_1(linea);
			break;
		case "l1_2":
			game.setL1_2(linea);
			break;
		case "l1_3":
			game.setL1_3(linea);
			break;
		case "l1_4":
			game.setL1_4(linea);
			break;
		case "l1_5":
			game.setL1_5(linea);
			break;
		case "l2_1":
			game.setL2_1(linea);
			break;
		case "l2_2":
			game.setL2_2(linea);
			break;
		case "l2_3":
			game.setL2_3(linea);
			break;
		case "l2_4":
			game.setL2_4(linea);
			break;
		case "l2_5":
			game.setL2_5(linea);
			break;
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
		gs.save(game, true);
		return Response.success("donete");
	}

	@PostMapping("/selfspell")
	public Response selfspell(@ModelAttribute GameAction data) {
		GameDTO game = gs.findById(data.getGame_id());
		CartaDTO card = cs.findById(data.getCard_id());
		System.out.println(card);
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
		gs.save(game, true);
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
					game.getPlayer1().setVida(game.getPlayer1().getVida() - card.getHabilidadDTO().getDmg());
					if (game.getPlayer1().getVida() <= 0) {
						game = fight(game);
						return Response.success("donete");
					}
					gs.save(game.getPlayer1());
				} else {
					game.getPlayer2().setVida(game.getPlayer2().getVida() - card.getHabilidadDTO().getDmg());
					if (game.getPlayer2().getVida() <= 0) {
						game = fight(game);
						return Response.success("donete");
					}
					gs.save(game.getPlayer2());
				}
			} else {
				linea.setVida(linea.getVida() - card.getHabilidadDTO().getDmg());
				if (linea.getVida() <= 0) {
					gs.deleteLinea(linea);
					if (game.getL1_1() != null) {
						if (game.getL1_1().getId() == linea.getId()) {
							game.setL1_1(null);
						}
					}
					if (game.getL1_2() != null) {
						if (game.getL1_2().getId() == linea.getId()) {
							game.setL1_2(null);
						}
					}
					if (game.getL1_3() != null) {
						if (game.getL1_3().getId() == linea.getId()) {
							game.setL1_3(null);
						}
					}
					if (game.getL1_4() != null) {
						if (game.getL1_4().getId() == linea.getId()) {
							game.setL1_4(null);
						}
					}
					if (game.getL1_5() != null) {
						if (game.getL1_5().getId() == linea.getId()) {
							game.setL1_5(null);
						}
					}
					if (game.getL2_1() != null) {
						if (game.getL2_1().getId() == linea.getId()) {
							game.setL2_1(null);
						}
					}
					if (game.getL2_2() != null) {
						if (game.getL2_2().getId() == linea.getId()) {
							game.setL2_2(null);
						}
					}
					if (game.getL2_3() != null) {
						if (game.getL2_3().getId() == linea.getId()) {
							game.setL2_3(null);
						}
					}
					if (game.getL2_4() != null) {
						if (game.getL2_4().getId() == linea.getId()) {
							game.setL2_4(null);
						}
					}
					if (game.getL2_5() != null) {
						if (game.getL2_5().getId() == linea.getId()) {
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
		gs.save(game, true);
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
		System.out.println(game.getTurno());
		switch (game.getTurno()) {
		case 1:
			game.setTurno(2);
			gs.save(game, true);
			break;
		case 2:
			game = turnDMGs(game);
			gs.save(game, true);
			game.setTurno(3);
			gs.save(game, true);
			break;
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

	// CRIT
	// private List<String> prefight(game) {
	// List<String> willcrit = new ArrayList<>();
	// }

	private GameDTO turnDMGs(GameDTO game) {
		GameDTO game_aux = GameDTO.builder().l1_1(game.getL1_1()).l1_2(game.getL1_2()).l1_3(game.getL1_3())
				.l1_4(game.getL1_4()).l1_5(game.getL1_5()).l2_1(game.getL2_1()).l2_2(game.getL2_2())
				.l2_3(game.getL2_3()).l2_4(game.getL2_4()).l2_5(game.getL2_5()).build();
		// Linea 1
		if (game.getL1_1() != null) {
			if (game.getL1_1().getPoisn() != null && game.getL1_1().getPoisn() > 0) {
				game.getL1_1().setVida(game.getL1_1().getVida() - 1);
				game.getL1_1().setPoisn(game.getL1_1().getPoisn() - 1);
				if (game.getL1_1().getVida() <= 0) {
					game.setL1_1(null);
				}
				if (game.getL1_1().getPoisn() <= 0) {
					game.getL1_1().setPoisn(null);
				}
			}
			if (game.getL1_1().getBleed() != null && game.getL1_1().getBleed() > 0) {
				game.getL1_1().setVida(game.getL1_1().getVida() - game.getL1_1().getBleed());
				if (game.getL1_1().getVida() <= 0) {
					game.setL1_1(null);
				}
			}
			if (game.getL1_1().getBurn() != null && game.getL1_1().getBurn() > 0) {
				game.getL1_1().setVida(game.getL1_1().getVida() - 1);
				game.getL1_1().setBurn(game.getL1_1().getBurn() - 1);
				if (game.getL1_1().getVida() <= 0) {
					game.setL1_1(null);
				}
				if (game.getL1_1().getBurn() <= 0) {
					game.getL1_1().setBurn(null);
				}
			}
			if (game.getL1_1() != null) { // Vuelve a verificar por si se eliminó por los estados
				gs.save(game.getL1_1());
			}
		}

		// Linea 2
		if (game.getL1_2() != null) {
			if (game.getL1_2().getPoisn() != null && game.getL1_2().getPoisn() > 0) {
				game.getL1_2().setVida(game.getL1_2().getVida() - 1);
				game.getL1_2().setPoisn(game.getL1_2().getPoisn() - 1);
				if (game.getL1_2().getVida() <= 0) {
					game.setL1_2(null);
				}
				if (game.getL1_2().getPoisn() <= 0) {
					game.getL1_2().setPoisn(null);
				}
			}
			if (game.getL1_2().getBleed() != null && game.getL1_2().getBleed() > 0) {
				game.getL1_2().setVida(game.getL1_2().getVida() - game.getL1_2().getBleed());
				if (game.getL1_2().getVida() <= 0) {
					game.setL1_2(null);
				}
			}
			if (game.getL1_2().getBurn() != null && game.getL1_2().getBurn() > 0) {
				game.getL1_2().setVida(game.getL1_2().getVida() - 1);
				game.getL1_2().setBurn(game.getL1_2().getBurn() - 1);
				if (game.getL1_2().getVida() <= 0) {
					game.setL1_2(null);
				}
				if (game.getL1_2().getBurn() <= 0) {
					game.getL1_2().setBurn(null);
				}
			}
			if (game.getL1_2() != null) {
				gs.save(game.getL1_2());
			}
		}

		// Linea 3
		if (game.getL1_3() != null) {
			if (game.getL1_3().getPoisn() != null && game.getL1_3().getPoisn() > 0) {
				game.getL1_3().setVida(game.getL1_3().getVida() - 1);
				game.getL1_3().setPoisn(game.getL1_3().getPoisn() - 1);
				if (game.getL1_3().getVida() <= 0) {
					game.setL1_3(null);
				}
				if (game.getL1_3().getPoisn() <= 0) {
					game.getL1_3().setPoisn(null);
				}
			}
			if (game.getL1_3().getBleed() != null && game.getL1_3().getBleed() > 0) {
				game.getL1_3().setVida(game.getL1_3().getVida() - game.getL1_3().getBleed());
				if (game.getL1_3().getVida() <= 0) {
					game.setL1_3(null);
				}
			}
			if (game.getL1_3().getBurn() != null && game.getL1_3().getBurn() > 0) {
				game.getL1_3().setVida(game.getL1_3().getVida() - 1);
				game.getL1_3().setBurn(game.getL1_3().getBurn() - 1);
				if (game.getL1_3().getVida() <= 0) {
					game.setL1_3(null);
				}
				if (game.getL1_3().getBurn() <= 0) {
					game.getL1_3().setBurn(null);
				}
			}
			if (game.getL1_3() != null) {
				gs.save(game.getL1_3());
			}
		}

		// Linea 4
		if (game.getL1_4() != null) {
			if (game.getL1_4().getPoisn() != null && game.getL1_4().getPoisn() > 0) {
				game.getL1_4().setVida(game.getL1_4().getVida() - 1);
				game.getL1_4().setPoisn(game.getL1_4().getPoisn() - 1);
				if (game.getL1_4().getVida() <= 0) {
					game.setL1_4(null);
				}
				if (game.getL1_4().getPoisn() <= 0) {
					game.getL1_4().setPoisn(null);
				}
			}
			if (game.getL1_4().getBleed() != null && game.getL1_4().getBleed() > 0) {
				game.getL1_4().setVida(game.getL1_4().getVida() - game.getL1_4().getBleed());
				if (game.getL1_4().getVida() <= 0) {
					game.setL1_4(null);
				}
			}
			if (game.getL1_4().getBurn() != null && game.getL1_4().getBurn() > 0) {
				game.getL1_4().setVida(game.getL1_4().getVida() - 1);
				game.getL1_4().setBurn(game.getL1_4().getBurn() - 1);
				if (game.getL1_4().getVida() <= 0) {
					game.setL1_4(null);
				}
				if (game.getL1_4().getBurn() <= 0) {
					game.getL1_4().setBurn(null);
				}
			}
			if (game.getL1_4() != null) {
				gs.save(game.getL1_4());
			}
		}

		// Linea 5
		if (game.getL1_5() != null) {
			if (game.getL1_5().getPoisn() != null && game.getL1_5().getPoisn() > 0) {
				game.getL1_5().setVida(game.getL1_5().getVida() - 1);
				game.getL1_5().setPoisn(game.getL1_5().getPoisn() - 1);
				if (game.getL1_5().getVida() <= 0) {
					game.setL1_5(null);
				}
				if (game.getL1_5().getPoisn() <= 0) {
					game.getL1_5().setPoisn(null);
				}
			}
			if (game.getL1_5().getBleed() != null && game.getL1_5().getBleed() > 0) {
				game.getL1_5().setVida(game.getL1_5().getVida() - game.getL1_5().getBleed());
				if (game.getL1_5().getVida() <= 0) {
					game.setL1_5(null);
				}
			}
			if (game.getL1_5().getBurn() != null && game.getL1_5().getBurn() > 0) {
				game.getL1_5().setVida(game.getL1_5().getVida() - 1);
				game.getL1_5().setBurn(game.getL1_5().getBurn() - 1);
				if (game.getL1_5().getVida() <= 0) {
					game.setL1_5(null);
				}
				if (game.getL1_5().getBurn() <= 0) {
					game.getL1_5().setBurn(null);
				}
			}
			if (game.getL1_5() != null) {
				gs.save(game.getL1_5());
			}
		}

		// Linea 2-1
		if (game.getL2_1() != null) {
			if (game.getL2_1().getPoisn() != null && game.getL2_1().getPoisn() > 0) {
				game.getL2_1().setVida(game.getL2_1().getVida() - 1);
				game.getL2_1().setPoisn(game.getL2_1().getPoisn() - 1);
				if (game.getL2_1().getVida() <= 0) {
					game.setL2_1(null);
				}
				if (game.getL2_1().getPoisn() <= 0) {
					game.getL2_1().setPoisn(null);
				}
			}
			if (game.getL2_1().getBleed() != null && game.getL2_1().getBleed() > 0) {
				game.getL2_1().setVida(game.getL2_1().getVida() - game.getL2_1().getBleed());
				if (game.getL2_1().getVida() <= 0) {
					game.setL2_1(null);
				}
			}
			if (game.getL2_1().getBurn() != null && game.getL2_1().getBurn() > 0) {
				game.getL2_1().setVida(game.getL2_1().getVida() - 1);
				game.getL2_1().setBurn(game.getL2_1().getBurn() - 1);
				if (game.getL2_1().getVida() <= 0) {
					game.setL2_1(null);
				}
				if (game.getL2_1().getBurn() <= 0) {
					game.getL2_1().setBurn(null);
				}
			}
			if (game.getL2_1() != null) {
				gs.save(game.getL2_1());
			}
		}

		// Linea 2-2
		if (game.getL2_2() != null) {
			if (game.getL2_2().getPoisn() != null && game.getL2_2().getPoisn() > 0) {
				game.getL2_2().setVida(game.getL2_2().getVida() - 1);
				game.getL2_2().setPoisn(game.getL2_2().getPoisn() - 1);
				if (game.getL2_2().getVida() <= 0) {
					game.setL2_2(null);
				}
				if (game.getL2_2().getPoisn() <= 0) {
					game.getL2_2().setPoisn(null);
				}
			}
			if (game.getL2_2().getBleed() != null && game.getL2_2().getBleed() > 0) {
				game.getL2_2().setVida(game.getL2_2().getVida() - game.getL2_2().getBleed());
				if (game.getL2_2().getVida() <= 0) {
					game.setL2_2(null);
				}
			}
			if (game.getL2_2().getBurn() != null && game.getL2_2().getBurn() > 0) {
				game.getL2_2().setVida(game.getL2_2().getVida() - 1);
				game.getL2_2().setBurn(game.getL2_2().getBurn() - 1);
				if (game.getL2_2().getVida() <= 0) {
					game.setL2_2(null);
				}
				if (game.getL2_2().getBurn() <= 0) {
					game.getL2_2().setBurn(null);
				}
			}
			if (game.getL2_2() != null) {
				gs.save(game.getL2_2());
			}
		}

		// Linea 2-3
		if (game.getL2_3() != null) {
			if (game.getL2_3().getPoisn() != null && game.getL2_3().getPoisn() > 0) {
				game.getL2_3().setVida(game.getL2_3().getVida() - 1);
				game.getL2_3().setPoisn(game.getL2_3().getPoisn() - 1);
				if (game.getL2_3().getVida() <= 0) {
					game.setL2_3(null);
				}
				if (game.getL2_3().getPoisn() <= 0) {
					game.getL2_3().setPoisn(null);
				}
			}
			if (game.getL2_3().getBleed() != null && game.getL2_3().getBleed() > 0) {
				game.getL2_3().setVida(game.getL2_3().getVida() - game.getL2_3().getBleed());
				if (game.getL2_3().getVida() <= 0) {
					game.setL2_3(null);
				}
			}
			if (game.getL2_3().getBurn() != null && game.getL2_3().getBurn() > 0) {
				game.getL2_3().setVida(game.getL2_3().getVida() - 1);
				game.getL2_3().setBurn(game.getL2_3().getBurn() - 1);
				if (game.getL2_3().getVida() <= 0) {
					game.setL2_3(null);
				}
				if (game.getL2_3().getBurn() <= 0) {
					game.getL2_3().setBurn(null);
				}
			}
			if (game.getL2_3() != null) {
				gs.save(game.getL2_3());
			}
		}

		// Linea 2-4
		if (game.getL2_4() != null) {
			if (game.getL2_4().getPoisn() != null && game.getL2_4().getPoisn() > 0) {
				game.getL2_4().setVida(game.getL2_4().getVida() - 1);
				game.getL2_4().setPoisn(game.getL2_4().getPoisn() - 1);
				if (game.getL2_4().getVida() <= 0) {
					game.setL2_4(null);
				}
				if (game.getL2_4().getPoisn() <= 0) {
					game.getL2_4().setPoisn(null);
				}
			}
			if (game.getL2_4().getBleed() != null && game.getL2_4().getBleed() > 0) {
				game.getL2_4().setVida(game.getL2_4().getVida() - game.getL2_4().getBleed());
				if (game.getL2_4().getVida() <= 0) {
					game.setL2_4(null);
				}
			}
			if (game.getL2_4().getBurn() != null && game.getL2_4().getBurn() > 0) {
				game.getL2_4().setVida(game.getL2_4().getVida() - 1);
				game.getL2_4().setBurn(game.getL2_4().getBurn() - 1);
				if (game.getL2_4().getVida() <= 0) {
					game.setL2_4(null);
				}
				if (game.getL2_4().getBurn() <= 0) {
					game.getL2_4().setBurn(null);
				}
			}
			if (game.getL2_4() != null) {
				gs.save(game.getL2_4());
			}
		}

		// Linea 2-5
		if (game.getL2_5() != null) {
			if (game.getL2_5().getPoisn() != null && game.getL2_5().getPoisn() > 0) {
				game.getL2_5().setVida(game.getL2_5().getVida() - 1);
				game.getL2_5().setPoisn(game.getL2_5().getPoisn() - 1);
				if (game.getL2_5().getVida() <= 0) {
					game.setL2_5(null);
				}
				if (game.getL2_5().getPoisn() <= 0) {
					game.getL2_5().setPoisn(null);
				}
			}
			if (game.getL2_5().getBleed() != null && game.getL2_5().getBleed() > 0) {
				game.getL2_5().setVida(game.getL2_5().getVida() - game.getL2_5().getBleed());
				if (game.getL2_5().getVida() <= 0) {
					game.setL2_5(null);
				}
			}
			if (game.getL2_5().getBurn() != null && game.getL2_5().getBurn() > 0) {
				game.getL2_5().setVida(game.getL2_5().getVida() - 1);
				game.getL2_5().setBurn(game.getL2_5().getBurn() - 1);
				if (game.getL2_5().getVida() <= 0) {
					game.setL2_5(null);
				}
				if (game.getL2_5().getBurn() <= 0) {
					game.getL2_5().setBurn(null);
				}
			}
			if (game.getL2_5() != null) {
				gs.save(game.getL2_5());
			}
		}
		return game;
	}

	private GameDTO fight(GameDTO game) {
		GameDTO game_aux = GameDTO.builder().l1_1(game.getL1_1()).l1_2(game.getL1_2()).l1_3(game.getL1_3())
				.l1_4(game.getL1_4()).l1_5(game.getL1_5()).l2_1(game.getL2_1()).l2_2(game.getL2_2())
				.l2_3(game.getL2_3()).l2_4(game.getL2_4()).l2_5(game.getL2_5()).build();
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
		LineaDTO linea = null;
		LineaDTO linea_own = null;
		PlayerStatusDTO player1 = game.getPlayer1();
		PlayerStatusDTO player2 = game.getPlayer2();
		// Linea 1
		if (game_aux.getL1_1() != null) {
			linea_own = game_aux.getL1_1();
			if (linea_own.getStun() == null || linea_own.getStun() <= 0) {
				if (game.getL2_1() != null) {
					linea = game.getL2_1();
					linea.setVida(linea.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
					if (linea_own.getCarta().getHabilidadDTO().getPrcnt() != null) {
						linea.setVida((int)Math.ceil(linea.getVida() - linea.getCarta().getVida()*(linea_own.getCarta().getHabilidadDTO().getPrcnt()/100)));
					}
					if (linea_own.getCarta().getHabilidadDTO().getLeth() != null && linea.getVida() > 0) {
						linea.setVida((int)Math.ceil(linea.getVida() - (linea.getCarta().getVida()-linea.getVida())*(linea_own.getCarta().getHabilidadDTO().getPrcnt()/100)));
					}
					if (linea.getVida() <= 0) {
						game.setL2_1(null);
					} else {
						if (linea_own.getCarta().getHabilidadDTO().getFreeze() != null && linea.getStun() == null) {
							linea.setStun(linea_own.getCarta().getHabilidadDTO().getFreeze());
							linea.setStun_name(linea_own.getCarta().getHabilidadDTO().getFreezeName());
						}
						if (linea_own.getCarta().getHabilidadDTO().getBurn() != null) {
							linea.setBurn(linea_own.getCarta().getHabilidadDTO().getBurn());
						}
						if (linea_own.getCarta().getHabilidadDTO().getPoisn() != null) {
							linea.setPoisn(linea_own.getCarta().getHabilidadDTO().getPoisn());
						}
						if (linea_own.getCarta().getHabilidadDTO().getBleed() != null && (linea.getBleed() == null || linea.getBleed() < linea_own.getCarta().getHabilidadDTO().getBleed() ) ) {
							linea.setBleed(linea_own.getCarta().getHabilidadDTO().getBleed());
						}
						if (linea_own.getCarta().getHabilidadDTO().getPrcntDwn() != null && linea.getPrcnt_dwn() < linea_own.getCarta().getHabilidadDTO().getPrcntDwn()) {
							linea.setPrcnt_dwn(linea_own.getCarta().getHabilidadDTO().getPrcntDwn());
						}
						gs.save(linea);
					}
				} else {
					player2.setVida(player2.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
				}
			} else {
				linea_own.setStun(linea_own.getStun() - 1);
				if (linea_own.getStun() <= 0) {
					linea_own.setStun(null);
				}
				gs.save(linea_own);
			}
		}

		// Linea 2
		if (game_aux.getL1_2() != null) {
			linea_own = game_aux.getL1_2();
			if (linea_own.getStun() == null || linea_own.getStun() <= 0) {
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
			} else {
				linea_own.setStun(linea_own.getStun() - 1);
				if (linea_own.getStun() <= 0) {
					linea_own.setStun(null);
				}
				gs.save(linea_own);
			}
		}

		// Linea 3
		if (game_aux.getL1_3() != null) {
			linea_own = game_aux.getL1_3();
			if (linea_own.getStun() == null || linea_own.getStun() <= 0) {
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
			} else {
				linea_own.setStun(linea_own.getStun() - 1);
				if (linea_own.getStun() <= 0) {
					linea_own.setStun(null);
				}
				gs.save(linea_own);
			}
		}

		// Linea 4
		if (game_aux.getL1_4() != null) {
			linea_own = game_aux.getL1_4();
			if (linea_own.getStun() == null || linea_own.getStun() <= 0) {
				if (game.getL2_4() != null) {
					linea = game.getL2_4();
					linea.setVida(linea.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
					if (linea.getVida() <= 0) {
						game.setL2_4(null);
					} else {
						gs.save(linea);
					}
				} else {
					player2.setVida(player2.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
				}
			} else {
				linea_own.setStun(linea_own.getStun() - 1);
				if (linea_own.getStun() <= 0) {
					linea_own.setStun(null);
				}
				gs.save(linea_own);
			}
		}

		// Linea 5
		if (game_aux.getL1_5() != null) {
			linea_own = game_aux.getL1_5();
			if (linea_own.getStun() == null || linea_own.getStun() <= 0) {
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
			} else {
				linea_own.setStun(linea_own.getStun() - 1);
				if (linea_own.getStun() <= 0) {
					linea_own.setStun(null);
				}
				gs.save(linea_own);
			}
		}

		// Linea 2-1
		if (game_aux.getL2_1() != null) {
			linea_own = game_aux.getL2_1();
			if (linea_own.getStun() == null || linea_own.getStun() <= 0) {
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
			} else {
				linea_own.setStun(linea_own.getStun() - 1);
				if (linea_own.getStun() <= 0) {
					linea_own.setStun(null);
				}
				gs.save(linea_own);
			}
		}

		// Linea 2-2
		if (game_aux.getL2_2() != null) {
			linea_own = game_aux.getL2_2();
			if (linea_own.getStun() == null || linea_own.getStun() <= 0) {
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
			} else {
				linea_own.setStun(linea_own.getStun() - 1);
				if (linea_own.getStun() <= 0) {
					linea_own.setStun(null);
				}
				gs.save(linea_own);
			}
		}

		// Linea 2-3
		if (game_aux.getL2_3() != null) {
			linea_own = game_aux.getL2_3();
			if (linea_own.getStun() == null || linea_own.getStun() <= 0) {
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
			} else {
				linea_own.setStun(linea_own.getStun() - 1);
				if (linea_own.getStun() <= 0) {
					linea_own.setStun(null);
				}
				gs.save(linea_own);
			}
		}

		// Linea 2-4
		if (game_aux.getL2_4() != null) {
			linea_own = game_aux.getL2_4();
			if (linea_own.getStun() == null || linea_own.getStun() <= 0) {
				if (game.getL1_4() != null) {
					linea = game.getL1_4();
					linea.setVida(linea.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
					if (linea.getVida() <= 0) {
						game.setL1_4(null);
					} else {
						gs.save(linea);
					}
				} else {
					player1.setVida(player1.getVida() - linea_own.getCarta().getHabilidadDTO().getDmg());
				}
			} else {
				linea_own.setStun(linea_own.getStun() - 1);
				if (linea_own.getStun() <= 0) {
					linea_own.setStun(null);
				}
				gs.save(linea_own);
			}
		}

		// Linea 2-5
		if (game_aux.getL2_5() != null) {
			linea_own = game_aux.getL2_5();
			if (linea_own.getStun() == null || linea_own.getStun() <= 0) {
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
			} else {
				linea_own.setStun(linea_own.getStun() - 1);
				if (linea_own.getStun() <= 0) {
					linea_own.setStun(null);
				}
				gs.save(linea_own);
			}
		}

		return game;
	}

}
