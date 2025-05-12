package com.javi.deckup.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.TiendaDTO;
import com.javi.deckup.repository.dao.CartaRepository;
import com.javi.deckup.repository.dao.TiendaRepository;
import com.javi.deckup.repository.entity.Carta;
import com.javi.deckup.repository.entity.Tienda;

@Service
public class TiendaServiceImpl implements TiendaService {

	@Autowired
	TiendaRepository tr;
	@Autowired
	CartaRepository cr;

	@Override
	public TiendaDTO findById(Integer i) {
		return TiendaDTO.convertToDTO(tr.findById(i).orElse(null));
	}

	@Override
	public void change() {
		Tienda tienda =  tr.findById(1).orElse(null);
		tienda.setCarta1(cr.findCommon().get(0));
		tienda.setCarta2(cr.findCommonOrRare(List.of(tienda.getCarta1().getId())).get(0));
		tienda.setCarta3(cr.findRareOrEpic(List.of(tienda.getCarta1().getId(), tienda.getCarta2().getId())).get(0));
		tienda.setCarta4(cr.findRareOrEpic(List.of(tienda.getCarta1().getId(), tienda.getCarta2().getId(), tienda.getCarta3().getId())).get(0));
		tienda.setCarta5(cr.findEpicOrLeg(List.of(tienda.getCarta1().getId(), tienda.getCarta2().getId(), tienda.getCarta3().getId(), tienda.getCarta4().getId())).get(0));
		tr.save(tienda);
	}
	
}
