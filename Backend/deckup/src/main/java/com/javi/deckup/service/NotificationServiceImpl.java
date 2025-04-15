package com.javi.deckup.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.NotificationDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.repository.dao.NotificationRepository;
import com.javi.deckup.repository.entity.Notification;

@Service
public class NotificationServiceImpl implements NotificationService{

	@Autowired
	NotificationRepository nr;
	
	@Override
	public List<NotificationDTO> findAllUnreaded(Long idPlayer) {
		return nr.findAllUnreaded(idPlayer).stream().map(n -> NotificationDTO.convertToDTO(n)).collect(Collectors.toList());
	}

	@Override
	public void save(NotificationDTO notif) {
		nr.save(NotificationDTO.convertToEntity(notif));
	}

	@Override
	public void win(UsuarioDTO usuario) {
		nr.save(Notification.builder()
							.user(UsuarioDTO.convertToEntity(usuario))
							.currency(300)
							.claimed(false)
							.title("Ganador")
							.msg("Has ganado una batalla y recibido 300 gemas por ello").build());
	}

	@Override
	public void readAll(Long id) {
		nr.readAll(id);
	}

}
