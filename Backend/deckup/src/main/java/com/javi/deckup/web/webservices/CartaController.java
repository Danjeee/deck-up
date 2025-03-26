package com.javi.deckup.web.webservices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.PlayerCardsDTO;
import com.javi.deckup.model.dto.RarezaDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.CartaService;
import com.javi.deckup.service.PlayerCardsService;
import com.javi.deckup.service.RarezaService;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.UserAction;

@RestController
@RequestMapping("/cards")
public class CartaController {

	@Autowired
	CartaService cs;
	
	@Autowired
	PlayerCardsService ps;
	
	@Autowired
	UsuarioService us;
	
	@Autowired
	RarezaService rs;
	
	@GetMapping("/all")
	public List<CartaDTO> getAll() {
		return cs.findAll();
	}
	
	@GetMapping("/rarezas")
	public List<RarezaDTO> getRarezas() {
		return rs.findAll();
	}
	
	@PostMapping("/getByPlayer")
	public List<PlayerCardsDTO> getByPlayer(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		return ps.getAllByUser(user.getId());
	}
	
	
}
