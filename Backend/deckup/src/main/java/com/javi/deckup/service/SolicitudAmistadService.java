package com.javi.deckup.service;

import java.util.List;

import com.javi.deckup.model.dto.SolicitudAmistadDTO;

public interface SolicitudAmistadService {

	List<SolicitudAmistadDTO> findAllAccepted(String token);
	
	List<SolicitudAmistadDTO> findAllUnaccepted(String token);
	
	void send(String token, Long idUserToSend);
	
	void accept(String token, Long idUserToSend);
	
	void decline(String token, Long idUserToSend);

	void delete(String user_auth, Long user_id);

	boolean hasARequestFrom(String user_auth, Long user_id);
	
}
