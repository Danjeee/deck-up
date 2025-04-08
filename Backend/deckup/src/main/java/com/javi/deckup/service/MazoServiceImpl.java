package com.javi.deckup.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.MazoDTO;
import com.javi.deckup.repository.dao.MazoRepository;
import com.javi.deckup.repository.dao.UsuarioRepository;
import com.javi.deckup.repository.entity.Mazo;
import com.javi.deckup.repository.entity.Usuario;

@Service
public class MazoServiceImpl implements MazoService{

	@Autowired
	MazoRepository mr;
	
	@Autowired
	UsuarioRepository ur;
	
	@Override
	public List<MazoDTO> findAllByUser(Long id) {
		return mr.findAllByUser(id).stream().map(m -> MazoDTO.convertToDTO(m)).collect(Collectors.toList());
	}

	@Override
	public void save(MazoDTO mazo) {
		mr.save(MazoDTO.convertToEntity(mazo));
		
	}

	@Override
	public Long count() {
		return mr.count();
	}

	@Override
	public MazoDTO findById(Long id) {
		Mazo mazo = mr.findById(id).orElse(null);
		return mazo == null ? null : MazoDTO.convertToDTO(mazo);
	}

	@Override
	public void deleteById(Long id) {
		mr.deleteById(id);
	}

	@Override
	public void select(MazoDTO mazo) {
		Usuario usuario = ur.findById(mazo.getUsuario().getId()).orElse(null);
		if (usuario != null) {
			usuario.setMazo(MazoDTO.convertToEntity(mazo));
			ur.save(usuario);
		}
	}

}
