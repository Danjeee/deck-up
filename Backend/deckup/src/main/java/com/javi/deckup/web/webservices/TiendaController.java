package com.javi.deckup.web.webservices;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.TiendaDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.CartaService;
import com.javi.deckup.service.TiendaService;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.Response;
import com.javi.deckup.utils.Session;
import com.javi.deckup.utils.UserAction;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/tienda")
public class TiendaController {
	
	@Autowired
	TiendaService ts;
	
	@Autowired
	CartaService cs;
	
	@Autowired
	UsuarioService us;

	@GetMapping("/get")
	public TiendaDTO get() {
		return ts.findById(1);
	}
	@PostMapping("/buy")
	public Response buy(@ModelAttribute UserAction data) {
		CartaDTO card = cs.findById(Math.toIntExact(data.getArtifact_id()));
		if (card == null) {
			return Response.error("No se ha encontrado la carta que quieres comprar", 404);
		}
		List<CartaDTO> cartasEnTienda = new ArrayList<>();
		TiendaDTO tienda = ts.findById(1);
		cartasEnTienda.add(tienda.getCarta1());
		cartasEnTienda.add(tienda.getCarta2());
		cartasEnTienda.add(tienda.getCarta3());
		cartasEnTienda.add(tienda.getCarta4());
		cartasEnTienda.add(tienda.getCarta5());
		if (!cartasEnTienda.contains(card)) {
			return Response.error("La carta que quieres comprar no se encuentra en la tienda");
		}
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		if (user == null) {
			return Response.error("Ha habido un error con la sesión, intentalo de nuevo");
		}
		if (user.getCurrency()<card.getPrecio()) {
			return Response.error("No tienes suficientes gemas");
		} else {
			us.buy(user, card);
		}
		return Response.success("Carta añadida a tu colección");
	}
	
	
	
}
