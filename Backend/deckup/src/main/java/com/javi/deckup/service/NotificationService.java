package com.javi.deckup.service;

import java.util.List;

import com.javi.deckup.model.dto.NotificationDTO;
import com.javi.deckup.model.dto.UsuarioDTO;

public interface NotificationService {
		List<NotificationDTO> findAllUnreaded(Long idPlayer);
		
		void save(NotificationDTO notif);

		void win(UsuarioDTO usuario);

		void readAll(Long id);
}
