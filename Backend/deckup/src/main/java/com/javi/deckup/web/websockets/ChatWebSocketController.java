package com.javi.deckup.web.websockets;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.javi.deckup.model.dto.MensajeDTO;
import com.javi.deckup.repository.entity.Mensaje;
import com.javi.deckup.service.ChatService;
import com.javi.deckup.service.UsuarioService;

@Controller
public class ChatWebSocketController {
	@Autowired
    ChatService chatService;

    @Autowired
    UsuarioService us;


    @MessageMapping("/chat/privado/{usuarioId1}-{usuarioId2}")
    @SendTo("/chat/privado/{usuarioId1}-{usuarioId2}")
    public MensajeDTO enviarMensajePrivado(@DestinationVariable Long usuarioId1,
                                        @DestinationVariable Long usuarioId2,
                                        MensajeDTO mensajeDTO) {
    	MensajeDTO mensaje = MensajeDTO.builder()
                .contenido(mensajeDTO.getContenido())
                .usuarioId(usuarioId1)
                .destinoId(usuarioId2)
                .fechaEnvio(Timestamp.from(Instant.now()))
                .build();
        return chatService.guardarMensaje(mensaje);
    }
}