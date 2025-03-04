package com.javi.deckup.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.CodigoDTO;
import com.javi.deckup.repository.dao.CodigoRepository;

@Service
public class CodigoServiceImpl implements CodigoService {

	@Autowired
	CodigoRepository cr;
	
	@Override
	public List<CodigoDTO> findAll() {
		return cr.findAll().stream().map(c -> CodigoDTO.convertToDTO(c)).collect(Collectors.toList());
	}

}
