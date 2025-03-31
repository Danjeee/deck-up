package com.javi.deckup.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.MensajeDTO;
import com.javi.deckup.repository.dao.MensajeRepository;
import com.javi.deckup.repository.entity.Mensaje;

@Service
public class ChatServiceImpl implements ChatService{
	
	@Autowired
    private MensajeRepository mensajeRepository;

	@Override
	public MensajeDTO save(MensajeDTO mensaje) {
		Mensaje mens = mensajeRepository.save(MensajeDTO.convertToEntity(mensaje));
		return MensajeDTO.convertToDTO(mens);
	}

	@Override
	public List<MensajeDTO> obtenerMensajesPrivados(Long usuarioId1, Long usuarioId2) {
		return mensajeRepository.findMensajesPrivados(usuarioId1, usuarioId2).stream().map(m -> MensajeDTO.convertToDTO(m)).collect(Collectors.toList());
	}

	@Override
	public List<MensajeDTO> findAllUnreadedFrom(Long uId, Long fId) {
		return mensajeRepository.findAllUnreadedFrom(uId, fId).stream().map(m -> MensajeDTO.convertToDTO(m)).collect(Collectors.toList());
	}

}
