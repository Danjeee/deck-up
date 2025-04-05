package com.javi.deckup.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.MensajeDTO;
import com.javi.deckup.model.dto.SolicitudAmistadDTO;
import com.javi.deckup.repository.dao.SolicitudAmistadRepository;
import com.javi.deckup.repository.dao.UsuarioRepository;
import com.javi.deckup.repository.entity.SolicitudAmistad;
import com.javi.deckup.repository.entity.Usuario;
import com.javi.deckup.web.websockets.NotificacionWSC;

@Service
public class SolicitudAmistadServiceImpl implements SolicitudAmistadService{
	
	@Autowired
	SolicitudAmistadRepository sr;
	
	@Autowired
	UsuarioRepository ur;

	@Autowired
	NotificacionWSC ws;
	
	@Override
	public List<SolicitudAmistadDTO> findAllAccepted(String token) {
		Usuario user = ur.findByAuth(token).orElse(null);
		if (user == null)
		{
			return null;
		} else {
			return sr.findAllAccepted(user.getId()).stream().map(s -> SolicitudAmistadDTO.convertToDTO(s)).collect(Collectors.toList());
		}
	}

	@Override
	public List<SolicitudAmistadDTO> findAllUnaccepted(String token) {
		Usuario user = ur.findByAuth(token).orElse(null);
		if (user == null)
		{
			return null;
		} else {
			return sr.findAllUnaccepted(user.getId()).stream().map(s -> SolicitudAmistadDTO.convertToDTO(s)).collect(Collectors.toList());
		}
	}

	@Override
	public void send(String token, Long idUserToSend) {
		Usuario user = ur.findByAuth(token).orElse(null);
		Usuario newfriend = ur.findById(idUserToSend).orElse(null);
		if (user != null && newfriend != null && sr.findByUserAndFriend(newfriend.getId(), user.getId()) != null) {
			SolicitudAmistad solicitud_rev = SolicitudAmistad.builder()
					 									 .usuario(newfriend)
					 									 .amigo(user)
					 									 .aceptada(false)
					 									 .build();
			ws.enviarMensaje(MensajeDTO.builder().destinoId(newfriend.getId()).contenido("Tienes una nueva solicitud de amistad").build());
			sr.save(solicitud_rev);
		}
		
	}

	@Override
	public void accept(String token, Long idUserToSend) {
		Usuario user = ur.findByAuth(token).orElse(null);
		Usuario newfriend = ur.findById(idUserToSend).orElse(null);
		if (user != null && newfriend != null) {
			SolicitudAmistad s1 = sr.findByUserAndFriend(user.getId(), newfriend.getId()).orElse(null);
			if (s1 != null) {
				SolicitudAmistad s2 = SolicitudAmistad.builder().aceptada(true).usuario(s1.getAmigo()).amigo(s1.getUsuario()).build();
				ws.enviarMensaje(MensajeDTO.builder().destinoId(newfriend.getId()).contenido("Han aceptado tu solicitud de amistad").build());
				s1.setAceptada(true);
				sr.save(s1);
				sr.save(s2);
			}
		}
	}

	@Override
	public void decline(String token, Long idUserToSend) {
		Usuario user = ur.findByAuth(token).orElse(null);
		Usuario newfriend = ur.findById(idUserToSend).orElse(null);
		if (user != null && newfriend != null) {
			SolicitudAmistad s1 = sr.findByUserAndFriend(user.getId(), newfriend.getId()).orElse(null);
			if (s1 != null) {
				ws.enviarMensaje(MensajeDTO.builder().destinoId(newfriend.getId()).contenido("Han rechazado tu solicitud de amistad").build());
				sr.deleteById(s1.getId());
			}
		}
	}

	@Override
	public void delete(String user_auth, Long user_id) {
		Usuario user = ur.findByAuth(user_auth).orElse(null);
		Usuario newfriend = ur.findById(user_id).orElse(null);
		if (user != null && newfriend != null) {
			SolicitudAmistad s1 = sr.findByUserAndFriend(user.getId(), newfriend.getId()).orElse(null);
			SolicitudAmistad s2 = sr.findByUserAndFriend(newfriend.getId(), user.getId()).orElse(null);
			if (s1 != null && s2 != null) {
				ws.enviarMensaje(MensajeDTO.builder().destinoId(user_id).contenido("Te ha eliminado uno de tus amigos").build());
				sr.deleteById(s1.getId());
				sr.deleteById(s2.getId());
			}
		}
		
	}

	@Override
	public boolean hasARequestFrom(String user_auth, Long user_id) {
		Usuario user = ur.findByAuth(user_auth).orElse(null);
		if (sr.findByUserAndFriend(user.getId(), user_id).orElse(null) != null || sr.findByUserAndFriend(user_id, user.getId()).orElse(null) != null) {
			return false;
		}
		return true;
	}

}
