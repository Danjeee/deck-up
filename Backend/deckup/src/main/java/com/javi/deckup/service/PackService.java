package com.javi.deckup.service;

import com.javi.deckup.model.dto.PaqueteDTO;

public interface PackService {

	PaqueteDTO findById(Integer artifact_id);

	PaqueteDTO findByPacksInStore(Integer artifact_id);

}
