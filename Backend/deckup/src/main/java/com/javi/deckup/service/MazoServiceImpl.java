package com.javi.deckup.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.MazoDTO;
import com.javi.deckup.repository.dao.MazoRepository;

@Service
public class MazoServiceImpl implements MazoService{

	@Autowired
	MazoRepository mr;
	
	@Override
	public List<MazoDTO> findAllByUser(Long id) {
		return mr.findAllByUser(id).stream().map(m -> MazoDTO.convertToDTO(m)).collect(Collectors.toList());
	}

	@Override
	public void save(MazoDTO mazo) {
		mr.save(MazoDTO.convertToEntity(mazo));
		
	}

}
