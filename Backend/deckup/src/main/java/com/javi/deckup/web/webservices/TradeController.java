package com.javi.deckup.web.webservices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.MazoDTO;
import com.javi.deckup.model.dto.PlayerCardsDTO;
import com.javi.deckup.model.dto.TradeCardsDTO;
import com.javi.deckup.model.dto.TradeDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.PlayerCardsService;
import com.javi.deckup.service.TradeService;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.CodeGenerator;
import com.javi.deckup.utils.Response;
import com.javi.deckup.utils.UserAction;

@RestController
@RequestMapping("/trades")
public class TradeController {

	@Autowired
	TradeService ts;
	
	@Autowired
	UsuarioService us;
	
	@Autowired
	PlayerCardsService ps;
	
	
	@PostMapping("/{id}")
	public TradeDTO get(@ModelAttribute UserAction data, @PathVariable("id") Long id) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		if (user == null) {
			return null;
		}
		TradeDTO trade = ts.findById(id);
		if (trade == null) {
			return null;
		}
		if (!trade.getStatus().equals("activo")) {
			return null;
		}
		if (trade.getPlayer1().getId() != user.getId() && trade.getPlayer2().getId() != user.getId()) {
			return null;
		}
		System.out.println(trade);
		return trade;
	}
	@PostMapping("/add")
	public TradeDTO add(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		if (user == null) {
			return null;
		}
		TradeDTO trade = ts.findById(data.getArtifact_long());
		if (trade == null) {
			return null;
		}
		PlayerCardsDTO pc = ps.findByCard(data.getArtifact_aux(), user.getId());
		if (pc == null) {
			return null;
		}
		if (pc.getCant() < data.getArtifact_id()) {
			return null;
		}
		int cant = 0;
		TradeCardsDTO tc = ts.findCardByTradeAndPlayerAndCard(trade,user,pc.getCarta());
		if (tc != null) {
			cant = tc.getCant();
		}
		//pa despue
		if ((pc.getCant()+cant - data.getArtifact_id() > 1) || ((pc.getCant()+cant - data.getArtifact_id() > 0) && !cartaEnMazo(pc.getCarta(), user))) {
			if (tc == null) {
				 tc = TradeCardsDTO.builder().cant(data.getArtifact_id()).carta(pc.getCarta()).trade(trade).usuario(user).build();
			} else {
				tc.setCant(tc.getCant() + data.getArtifact_id());
			}

			List<TradeCardsDTO> allcards = ts.getAllCards(trade);
			TradeCardsDTO tcaux = ts.save(tc);
			tc.setId(tcaux.getId());
			if (allcards.contains(tc)) {
				allcards.set(allcards.indexOf(tc), tc);
			} else {
				allcards.add(tc);
			}
			trade.setCartas(allcards);
			ts.sendWsTo(trade);
			return trade;
		} else {
			return null;
		}
	}
	
	private Boolean cartaEnMazo(CartaDTO carta, UsuarioDTO user) {
		return us.usertienecartaenmazo(carta.getId(), user.getId()) != null;
	}
	
	@PostMapping("/accept")
	public Response accept(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		if (user == null) {
			return Response.error("Ha habido un problema al recuperar la sesión");
		}
		TradeDTO trade = ts.findById(data.getUser_id());
		if (trade == null) {
			return Response.error("Ha habido un problema al recuperar el intercambio");
		}
		if (trade.getPlayer1().getId() == user.getId()) {
			trade.setP1c(true);
		} else {
			trade.setP2c(true);
		}
		ts.save(trade, true);
		return Response.success("donete");
	}
	
	@PostMapping("/msg")
	public Response msg(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		if (user == null) {
			return Response.error("Ha habido un problema al recuperar la sesión");
		}
		TradeDTO trade = ts.findById(data.getUser_id());
		if (trade == null) {
			return Response.error("Ha habido un problema al recuperar el intercambio");
		}
		PlayerCardsDTO pc = ps.findById(data.getArtifact_long());
		if (pc == null) {
			ts.sendWsTo(trade, user.getUsername() + " quiere "+data.getArtifact_aux()+" gemas");
		} else {
			ts.sendWsTo(trade, user.getUsername() + " quiere "+pc.getCarta().getNombre());
		}
		return Response.success("donete");
	}
	
	@PostMapping("/past")
	public List<TradeDTO> past(@ModelAttribute UserAction data){
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		if (user == null) {
			return null;
		}
		return ts.getPast(user.getId());
		
	}
	
	@PostMapping("/sfinish")
	public Response sfinish(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		if (user == null) {
			return Response.error("Ha habido un problema al recuperar la sesión");
		}
		TradeDTO trade = ts.findById(data.getUser_id());
		if (trade == null) {
			return Response.error("Ha habido un problema al recuperar el intercambio");
		}
		ts.sendWsTo(trade, "p2c");
		return Response.success("donete");
	}
	
	@PostMapping("/finish")
	public Response finish(@ModelAttribute UserAction data) {
		TradeDTO trade = ts.findById(data.getUser_id());
		if (trade == null) {
			return Response.error("Ha habido un problema al recuperar el intercambio");
		}
		trade.setStatus("finished");

		ts.save(trade);
		for (TradeCardsDTO tc : trade.getCartas()) {
			if (tc.getUsuario().getId() == trade.getPlayer1().getId()) {
				ps.giveCard(trade.getPlayer2(), tc.getCarta(), tc.getCant());
				ps.rmvCard(trade.getPlayer1(), tc.getCarta(), tc.getCant());
			} else {
				ps.giveCard(trade.getPlayer1(), tc.getCarta(), tc.getCant());
				ps.rmvCard(trade.getPlayer2(), tc.getCarta(), tc.getCant());
			}
		}
		if (trade.getP1curr() == null) {
			trade.setP1curr(0);
		}
		if (trade.getP2curr() == null) {
			trade.setP2curr(0);
		}
		us.trademoney(trade.getPlayer1(),trade.getPlayer2(), trade.getP1curr(), trade.getP2curr());
		ts.sendWsTo(trade, "finish");
		return Response.success("donete");
	}
	
	@PostMapping("/unaccept")
	public Response unaccept(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		if (user == null) {
			return Response.error("Ha habido un problema al recuperar la sesión");
		}
		TradeDTO trade = ts.findById(data.getUser_id());
		if (trade == null) {
			return Response.error("Ha habido un problema al recuperar el intercambio");
		}
		if (trade.getPlayer1().getId() == user.getId()) {
			trade.setP1c(false);
		} else {
			trade.setP2c(false);
		}
		ts.save(trade, true);
		return Response.success("donete");
	}
	
	@PostMapping("/addgems")
	public TradeDTO addGems(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		if (user == null) {
			return null;
		}
		TradeDTO trade = ts.findById(data.getArtifact_long());
		if (trade == null) {
			return null;
		}
		if (user.getCurrency() < data.getArtifact_id()) {
			return null;
		}
		if (trade.getPlayer1().getId() == user.getId()) {
			trade.setP1curr(data.getArtifact_id());
		} else {
			trade.setP2curr(data.getArtifact_id());
		}
		ts.save(trade ,true);
		return trade;
	}
	
	@PostMapping("/remove")
	public TradeDTO remove(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		if (user == null) {
			return null;
		}
		TradeDTO trade = ts.findById(data.getArtifact_long());
		if (trade == null) {
			return null;
		}
		TradeCardsDTO pc = ts.findTCByCard(data.getUser_id());
		if (pc == null) {
			return null;
		} else {
			ts.removeTC(pc);
		}

		List<TradeCardsDTO> allcards = ts.getAllCards(trade);
		trade.setCartas(allcards);
		if (allcards.contains(pc)) {
			allcards.remove(pc);
		}
		ts.sendWsTo(trade);
		return trade;
	}
	@PostMapping("/new")
	public Response create(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		if (user == null) {
			return Response.error("Ha habido un error en tu sesión, intentalo más tarde");
		}
		String code = CodeGenerator.generateNewCode(8);
		TradeDTO trade = TradeDTO.builder().player1(user).code(code).status("waiting").build();
		trade = ts.save(trade);
		return Response.success(trade.getId() + "/" + trade.getCode());
	}
	@PostMapping("/leave")
	public Response leave(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		TradeDTO trade = ts.findById(data.getUser_id());
		if (user == null) {
			return Response.error("Ha habido un error en tu sesión, intentalo más tarde");
		}
		if (trade == null) {
			return Response.error("El intercambio no existe");
		}
		if (trade.getStatus().equals("activo")) {
			trade.setStatus("cancelado");
			ts.save(trade, "leave");
		}
		return Response.success("donete");
	}
	@PostMapping("/join")
	public Response join(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		TradeDTO trade = ts.findByCode(data.getCode());
		if (user == null) {
			return Response.error("Ha habido un error en tu sesión, intentalo más tarde");
		}
		if (trade == null) {
			return Response.error("Esta sala de intercambio no existe");
		}
		if (!trade.getStatus().equals("waiting")) {
			return Response.error("Este intercambio ya ha finalizado");
		}
		trade.setStatus("activo");
		trade.setPlayer2(user);
		trade = ts.save(trade, "joined");
		return Response.success(trade.getId() + "/" + trade.getCode());
	}
}
