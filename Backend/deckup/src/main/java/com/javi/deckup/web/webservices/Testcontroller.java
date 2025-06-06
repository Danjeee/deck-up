package com.javi.deckup.web.webservices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.CodigoDTO;
import com.javi.deckup.model.dto.PlayerCardsDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.repository.entity.PlayerCards;
import com.javi.deckup.service.CartaService;
import com.javi.deckup.service.CodigoService;
import com.javi.deckup.service.PlayerCardsService;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.EmailService;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class Testcontroller {

	@Autowired
	UsuarioService us;
	

	@Autowired
	CartaService cs;
	
	@Autowired
	CodigoService cds;
	
	@Autowired
	PlayerCardsService ps;
	
	@Autowired
	private EmailService es;

	@GetMapping("/")
	public List<UsuarioDTO> getMethodName() {
		return us.findAll();
	}
	
	@GetMapping("/cards")
	public List<CartaDTO> getMethodName2() {
		return cs.findAll();
	}
	
	@GetMapping("/codes")
	public List<CodigoDTO> getMethodName3() {
		return cds.findAll();
	}
	@GetMapping("/cards/{idUser}")
	public List<PlayerCardsDTO> getMethodName4(@PathVariable("idUser") Long id) {
		return ps.getAllByUser(id);
	}
	
	
}
