package com.javi.deckup.web.webservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.MensajeDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.ChatService;
import com.javi.deckup.service.UsuarioService;
import com.javi.deckup.utils.Response;
import com.javi.deckup.utils.UserAction;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

	@Autowired
    private ChatService chatService;
	
	@Autowired
    private UsuarioService us;

    @GetMapping("/getMsgs/{usuarioId1}-{usuarioId2}")
    public List<MensajeDTO> obtenerMensajesPrivados(@PathVariable Long usuarioId1, @PathVariable Long usuarioId2) {
        return chatService.obtenerMensajesPrivados(usuarioId1, usuarioId2);
    }
    @PostMapping("/read/{friendId}")
    public Response read(@ModelAttribute UserAction data, @PathVariable("friendId") Long fId) {
    	UsuarioDTO user = us.findByToken(data.getUser_auth());
    	for (MensajeDTO i : chatService.findAllUnreadedFrom(user.getId(),fId)) {
			i.setLeido(true);
			System.out.println(i.getContenido());
			chatService.save(i);
		}
    	return Response.success("");
    }
    
} 