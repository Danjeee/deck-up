package com.javi.deckup.web.webservices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.MazoDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.MazoService;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.MazosReturn;
import com.javi.deckup.utils.Response;
import com.javi.deckup.utils.UserAction;

import org.springframework.web.bind.annotation.RequestBody;


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
	
	@GetMapping("/{id}")
	public MazoDTO getById(@PathVariable("id") Long id) {
		return ms.findById(id);
	}
	
	@PostMapping("/save")
	public Response save(@RequestBody MazoDTO mazo) {
		UsuarioDTO user = us.findByToken(mazo.getUsuario().getAuth());
		if (user != null) {
			if (mazo.getNombre() == null || mazo.getNombre() == "") {
				mazo.setNombre("Mazo "+ms.count(user.getId()));
			}
			mazo.setUsuario(user);
			mazo = ms.save(mazo);
			if (user.getMazo() == null) {
				us.setMazo(user.getId(),mazo.getId());
			}
			return Response.success("Mazo guardado correctamente");
		}
		return Response.error("Ha habido un error en la sesión, intentalo de nuevo");
	}
	
	@PostMapping("/delete")
	public Response delete(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		if (user != null) {
			MazoDTO mazo = ms.findById(data.getUser_id());
			if (mazo == null) {
				return Response.error("Este mazo no existe");
			}
			if (mazo.getUsuario().getId() != user.getId()) {
				return Response.error("Este mazo no te pertenece");
			}
			if (user.getMazo() != null) {
				if (mazo.getId() == user.getMazo().getId()) {
					return Response.error("No puedes borrar tu mazo seleccionado");
				}
			}
			ms.deleteById(mazo.getId());
			return Response.success("Mazo guardado correctamente");
		}
		return Response.error("Ha habido un error en la sesión, intentalo de nuevo");
	}
	
	
	@PostMapping("/select")
	public Response select(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		if (user != null) {
			MazoDTO mazo = ms.findById(data.getUser_id());
			if (mazo == null) {
				return Response.error("El mazo no existe");
			}
			if (mazo.getUsuario().getId() != user.getId()) {
				return Response.error("El mazo no te pertenece");
			}
			ms.select(mazo);
			return Response.success("Mazo cambiado correctamente");
		}
		return Response.error("Ha habido un error en la sesión, intentalo de nuevo");
	}
	
	
}
