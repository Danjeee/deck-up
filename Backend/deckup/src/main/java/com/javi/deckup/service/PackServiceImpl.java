package com.javi.deckup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.PaqueteDTO;
import com.javi.deckup.model.dto.TiendaDTO;
import com.javi.deckup.repository.dao.PackRepository;
import com.javi.deckup.repository.entity.Paquete;

@Service
public class PackServiceImpl implements PackService {

	@Autowired
	PackRepository pr;
	
	@Autowired
	TiendaService ts;

	@Override
	public PaqueteDTO findById(Integer id) {
		Paquete pack = pr.findById(id).orElse(null);
		return pack == null ? null : PaqueteDTO.convertToDTO(pack, true);
	}

	@Override
	public PaqueteDTO findByPacksInStore(Integer id) {
		TiendaDTO tienda = ts.findById(1);
		if (tienda.getPaq1().getId() == id) {
			return PaqueteDTO.convertToDTO(pr.findById(id).get(), true);
		}
		if (tienda.getPaq2().getId() == id) {
			return PaqueteDTO.convertToDTO(pr.findById(id).get(), true);
		}
		if (tienda.getPaq3().getId() == id) {
			return PaqueteDTO.convertToDTO(pr.findById(id).get(), true);
		}
		return null;
	}
	
}
