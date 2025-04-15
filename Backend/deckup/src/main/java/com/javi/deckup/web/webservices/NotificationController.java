package com.javi.deckup.web.webservices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.NotificationDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.NotificationService;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.Response;
import com.javi.deckup.utils.UserAction;

@RestController
@RequestMapping("/notifs")
public class NotificationController {
	
	@Autowired
	UsuarioService us;
	
	@Autowired
	NotificationService ns;

	@PostMapping("/getUnreaded")
	public List<NotificationDTO> getUnreaded(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth(), true);
		if (user == null) {
			return null;
		}
		List<NotificationDTO> all = ns.findAllUnreaded(user.getId());
		for (NotificationDTO i : all) {
			if (i.getCurrency() != null) {
				user.setCurrency(user.getCurrency() + i.getCurrency());
			}
		}
		us.save(user);
		return all;
	}
	
	@PostMapping("/readAll")
	public Response readAll(@ModelAttribute UserAction data) {
		UsuarioDTO user = us.findByToken(data.getUser_auth());
		if (user == null) {
			return null;
		}
		ns.readAll(user.getId());
		return Response.success("donete");
	}
	
}
