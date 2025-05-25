package com.javi.deckup.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.HabilidadDTO;
import com.javi.deckup.repository.dao.HabilidadRepository;

@Service
public class HabilidadServiceImpl implements HabilidadService {
	
	@Autowired 
	HabilidadRepository hr;

	@Override
	public List<HabilidadDTO> getAll() {
		return hr.findAll().stream().map(h -> HabilidadDTO.convertToDTO(h)).collect(Collectors.toList());
	}

	@Override
	public void save(HabilidadDTO hab) {
		// TODO Auto-generated method stub
		
	}

}
