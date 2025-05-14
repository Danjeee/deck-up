package com.javi.deckup.web.webservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.TradeDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
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
		return trade;
	}
	@PostMapping("/new")
	public Response create(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		if (user == null) {
			return Response.error("Ha habido un error en tu sesión, intentalo más tarde");
		}
		String code = CodeGenerator.generateNewCode(8);
		TradeDTO trade = TradeDTO.builder().player1(user).code(code).status("activo").build();
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
		if (!trade.getStatus().equals("activo")) {
			return Response.error("Este intercambio ya ha finalizado");
		}
		trade.setPlayer2(user);
		trade = ts.save(trade, "joined");
		return Response.success(trade.getId() + "/" + trade.getCode());
	}
}
