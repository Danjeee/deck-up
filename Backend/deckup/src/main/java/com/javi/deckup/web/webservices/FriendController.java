package com.javi.deckup.web.webservices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.SolicitudAmistadDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.SolicitudAmistadService;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.FriendList;
import com.javi.deckup.utils.Response;
import com.javi.deckup.utils.UserAction;

@RestController
@RequestMapping("/friendlist")
public class FriendController {
	
	@Autowired
	UsuarioService us;
	
	@Autowired
	SolicitudAmistadService ss;
	
	@PostMapping("/")
	public FriendList findAll(@ModelAttribute UserAction data) {
		List<SolicitudAmistadDTO> friends = ss.findAllAccepted(data.getUser_auth());
		List<SolicitudAmistadDTO> pending = ss.findAllUnaccepted(data.getUser_auth());
		return FriendList.builder().friends(friends).pending(pending).build();
	}
	
	@PostMapping("/accept")
	public Response accept(@ModelAttribute UserAction data) {
		try {
			ss.accept(data.getUser_auth(), data.getUser_id());
			return Response.success("Solicitud enviada correctamente");
		} catch (Exception e){
			return Response.error("Ha habido un error, intentalo más tarde");
		}
	}
	
	@PostMapping("/decline")
	public Response decline(@ModelAttribute UserAction data) {
		try {
			ss.decline(data.getUser_auth(), data.getUser_id());
			return Response.success("Solicitud rechazada correctamente");
		} catch (Exception e){
			return Response.error("Ha habido un error, intentalo más tarde");
		}
	}
	
	@PostMapping("/delete")
	public Response delete(@ModelAttribute UserAction data) {
		try {
			ss.delete(data.getUser_auth(), data.getUser_id());
			return Response.success("Amigo borrado correctamente");
		} catch (Exception e){
			return Response.error("Ha habido un error, intentalo más tarde");
		}
	}
	
	@PostMapping("/add")
	public Response add(@ModelAttribute UserAction data) {
		try {
			UsuarioDTO tosend = us.findByUsername(data.getCode());
			if (ss.hasARequestFrom(data.getUser_auth(), tosend.getId())) {
				ss.send(data.getUser_auth(), tosend.getId());
				return Response.success("Amigo añadido correctamente");
			} else {
				return Response.error("No puedes mandar otra solicitud a este usuario");
			}
		} catch (Exception e){
			return Response.error("El usuario no existe");
		}
	}
	

}
