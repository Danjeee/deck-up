package com.javi.deckup.web.webservices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
		TradeCardsDTO tc = ts.findCardByTradeAndPlayerAndCard(trade,user,pc.getCarta());
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
		trade.setStatus("cancelado");
		ts.save(trade, "leave");
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
