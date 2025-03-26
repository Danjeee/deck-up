package com.javi.deckup.web.webservices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.MazoDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.MazoService;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.MazosReturn;

@RestController
@RequestMapping("/decks")
public class MazoController {
	
	@Autowired
	MazoService ms;
	
	@Autowired
	UsuarioService us;

	@PostMapping("/all")
	public MazosReturn getAll(@ModelAttribute UsuarioDTO data) {
		UsuarioDTO user = us.findByToken(data.getAuth());
		return MazosReturn.builder().all(ms.findAllByUser(user.getId())).current(user.getMazo()).build();
	}
	
	
}
