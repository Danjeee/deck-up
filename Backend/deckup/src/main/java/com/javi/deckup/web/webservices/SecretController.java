package com.javi.deckup.web.webservices;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.CodigoDTO;
import com.javi.deckup.model.dto.PlayerCardsDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.CodigoService;
import com.javi.deckup.service.PlayerCardsService;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.Response;
import com.javi.deckup.utils.UserAction;

@RestController
@RequestMapping("/code")
public class SecretController {
	
	@Autowired
	CodigoService cs;
	
	@Autowired
	UsuarioService us;
	
	@Autowired
	PlayerCardsService ps;

	@PostMapping("/")
	public Response codecheck(@ModelAttribute UserAction data) {
		CodigoDTO code = cs.findByCodigo(data.getCode());
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		
		if (user == null) {
			return Response.error("Ha habido un problema con tu sesión, intentalo de nuevo más tarde");
		} else {
			user.setAuth(data.getUser_auth());
		}
		if (code == null) {
			return Response.error("El codigo no existe");
		}
		
		if (code.getExpiration_date() != null && code.getExpiration_date().before(Date.from(Instant.now()))) {
			return Response.error("El codigo ha expirado");
		}
		
		if (code.getUses_left() == 0) {
			return Response.error("No quedan usos disponibles para este codigo");
		}
		
		if (cs.hasUserUsed(user.getId(), code.getId())) {
			return Response.error("Ya has reclamado este codigo");
		}
		List<CartaDTO> tengoquehacerestoparanoanadirunatributomasalaclaseresponse = new ArrayList<>();
		if (code.getCarta() != null) {
			tengoquehacerestoparanoanadirunatributomasalaclaseresponse.add(code.getCarta());
		} else {
			tengoquehacerestoparanoanadirunatributomasalaclaseresponse = null;
		}
		user.setCurrency(user.getCurrency()+code.getCurrency());
		us.save(user);
		if (code.getCarta() != null) {
			ps.giveCard(user, code.getCarta(), code.getCard_cant());
		}
		if (code.getUses_left()>0) {
			code.setUses_left(code.getUses_left()-1);
		}
		cs.save(code, user);
		return Response.builder().status(200).tit("Codigo canjeado correctamente").msg(code.getCard_cant().toString()).cant(code.getCurrency()).cartas(tengoquehacerestoparanoanadirunatributomasalaclaseresponse).build();
	}
	
	
}
