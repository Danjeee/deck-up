package com.javi.deckup.web.websockets;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.javi.deckup.model.dto.MensajeDTO;
import com.javi.deckup.service.ChatService;
import com.javi.deckup.service.UsuarioService;

@Controller
public class ChatWebSocketController {
	@Autowired
    ChatService cs;

    @Autowired
    UsuarioService us;


    @MessageMapping("/chat/{roomId}")
    @SendTo("/chat/{roomId}")
    public MensajeDTO chat(@DestinationVariable String roomId, MensajeDTO mensajeDTO) {
    	MensajeDTO mensaje = MensajeDTO.builder()
                .contenido(mensajeDTO.getContenido())
                .usuarioId(mensajeDTO.getUsuarioId())
                .destinoId(mensajeDTO.getDestinoId())
                .fechaEnvio(Timestamp.from(Instant.now()))
                .build();
    	return cs.save(mensaje);
    }
}