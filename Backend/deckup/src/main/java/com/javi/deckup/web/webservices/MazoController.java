package com.javi.deckup.web.webservices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.MazoDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.MazoService;
import com.javi.deckup.service.UsuarioService;

@RestController
@RequestMapping("/mazos")
public class MazoController {
	
	@Autowired
	MazoService ms;
	
	@Autowired
	UsuarioService us;

	@GetMapping("/all")
	public List<MazoDTO> getAll(@ModelAttribute UsuarioDTO data) {
		UsuarioDTO user = us.findByToken(data.getAuth());
		return ms.findAllByUser(user.getId());
	}
	
	
}
