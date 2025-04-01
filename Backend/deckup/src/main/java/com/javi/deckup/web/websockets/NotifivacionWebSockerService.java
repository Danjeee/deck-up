package com.javi.deckup.web.websockets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.MensajeDTO;
import com.javi.deckup.model.dto.SolicitudAmistadDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.UsuarioService;

@Service
public class NotifivacionWebSockerService {
	
	@Autowired
	UsuarioService us;
	
	private final SimpMessageSendingOperations messagingTemplate;

    public NotifivacionWebSockerService(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void enviarSolicitudAmistad(SolicitudAmistadDTO solicitud) {
        messagingTemplate.convertAndSendToUser(
                String.valueOf(solicitud.getAmigo().getId()),
                "/topic/requests",
                solicitud);
    }

    public void enviarMensaje(MensajeDTO mensaje) {
    	String username = us.findUsername(mensaje.getDestinoId());
        messagingTemplate.convertAndSendToUser(
        		username,
                "/topic/unreaded",
                mensaje);
    }

}
