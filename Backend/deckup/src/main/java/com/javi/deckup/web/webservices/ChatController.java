package com.javi.deckup.web.webservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.model.dto.MensajeDTO;
import com.javi.deckup.service.ChatService;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/getMsgs/{usuarioId1}-{usuarioId2}")
    public List<MensajeDTO> obtenerMensajesPrivados(@PathVariable Long usuarioId1, @PathVariable Long usuarioId2) {
        return chatService.obtenerMensajesPrivados(usuarioId1, usuarioId2);
    }
} 