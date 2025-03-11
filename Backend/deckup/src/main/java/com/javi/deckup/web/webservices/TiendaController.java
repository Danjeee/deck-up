package com.javi.deckup.web.webservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.TiendaDTO;
import com.javi.deckup.service.TiendaService;

@RestController
@RequestMapping("/tienda")
public class TiendaController {
	
	@Autowired
	TiendaService ts;

	@GetMapping("/get")
	public TiendaDTO get() {
		return ts.findById(1);
	}
	
	
}
