package com.javi.deckup.web.websockets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.MensajeDTO;
import com.javi.deckup.model.dto.SolicitudAmistadDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.UsuarioService;

@Controller
public class NotificacionWSC {
	
	@Autowired
	UsuarioService us;
	
	private final SimpMessageSendingOperations messagingTemplate;

    public NotificacionWSC(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void enviarMensaje(MensajeDTO mensaje) {
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + mensaje.getDestinoId(),
                mensaje.getContenido());
    }
    
    public void norificarMatch(MensajeDTO mensaje) {
        messagingTemplate.convertAndSend(
                "/matchmaking/" + mensaje.getDestinoId(),
                mensaje.getContenido());
    }

	public void gameStatusChange(MensajeDTO game) {
		 messagingTemplate.convertAndSend(
	                "/game/" + game.getDestinoId(),
	                game.getContenido());
	}

}
