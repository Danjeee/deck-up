package com.javi.deckup.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.PlayerCardsDTO;
import com.javi.deckup.repository.dao.PlayerCardsRepository;

@Service
public class PlayerCardsServiceImpl implements PlayerCardsService{

	@Autowired
	PlayerCardsRepository pr;
	
	@Override
	public List<PlayerCardsDTO> getAllByUser(Long id) {
		return pr.findAllByUser(id).stream().map(c -> PlayerCardsDTO.convertToDTO(c)).collect(Collectors.toList());
	}

}
