package com.javi.deckup.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.PlayerCardsDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.repository.dao.CartaRepository;
import com.javi.deckup.repository.dao.PlayerCardsRepository;
import com.javi.deckup.repository.dao.UsuarioRepository;
import com.javi.deckup.repository.entity.PlayerCards;

@Service
public class PlayerCardsServiceImpl implements PlayerCardsService{

	@Autowired
	PlayerCardsRepository pr;
	
	@Autowired
	UsuarioRepository ur;
	
	@Autowired
	CartaRepository cr;
	
	@Override
	public List<PlayerCardsDTO> getAllByUser(Long id) {
		return pr.findAllByUser(id).stream().map(c -> PlayerCardsDTO.convertToDTO(c)).collect(Collectors.toList());
	}

	@Override
	public void giveCard(UsuarioDTO user, CartaDTO carta, Integer cant) {
		boolean has = false;
		Long id = 0L;
		for (PlayerCards i : pr.findAllByUser(user.getId())) {
			if (i.getCarta().getId() == carta.getId()){
				has = true;
				id = i.getId();
			}
		}
		if (has) {
			PlayerCards pc = pr.findById(id).get();
			pc.setCant(pc.getCant() + cant);
			pr.save(pc);
		} else {
			PlayerCards pc = new PlayerCards();
			pc.setUsuario(ur.findById(user.getId()).get());
			pc.setCarta(cr.findById(carta.getId()).get());
			pc.setCant(cant);
			pr.save(pc);
		}
	}

	@Override
	public PlayerCardsDTO findByCard(Integer artifact_aux, Long idPlayer) {
		PlayerCards pc = pr.findByCard(artifact_aux, idPlayer).orElse(null);
		return pc == null ? null : PlayerCardsDTO.convertToDTO(pc);
	}

}
