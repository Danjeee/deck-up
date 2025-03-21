package com.javi.deckup.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.RarezaDTO;
import com.javi.deckup.repository.dao.RarezaRepository;

@Service
public class RarezaServiceImpl implements RarezaService{

	@Autowired
	RarezaRepository rs;
	
	@Override
	public List<RarezaDTO> findAll() {
		return rs.findAll().stream().map(r -> RarezaDTO.convertToDTO(r)).collect(Collectors.toList());
	}

}
