package com.javi.deckup.service;

import java.util.List;

import com.javi.deckup.model.dto.MensajeDTO;

public interface ChatService {
	
	MensajeDTO save(MensajeDTO mensaje);
	
	List<MensajeDTO> obtenerMensajesPrivados(Long usuarioId1, Long usuarioId2);

}
