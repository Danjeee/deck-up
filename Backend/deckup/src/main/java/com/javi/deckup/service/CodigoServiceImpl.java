package com.javi.deckup.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.CodigoDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.repository.dao.CartaRepository;
import com.javi.deckup.repository.dao.CodigoRepository;
import com.javi.deckup.repository.dao.UsuarioRepository;
import com.javi.deckup.repository.entity.Carta;
import com.javi.deckup.repository.entity.Codigo;
import com.javi.deckup.repository.entity.Usuario;

@Service
public class CodigoServiceImpl implements CodigoService {

	@Autowired
	CodigoRepository cr;
	
	@Autowired
	CartaRepository crd;
	
	@Autowired
	UsuarioRepository ur;
	
	@Override
	public List<CodigoDTO> findAll() {
		return cr.findAll().stream().map(c -> CodigoDTO.convertToDTO(c)).collect(Collectors.toList());
	}

	@Override
	public CodigoDTO findByCodigo(String code) {
		Codigo codigo = cr.findByCodigo(code).orElse(null);
		return codigo == null ? null : CodigoDTO.convertToDTO(codigo);
	}

	@Override
	public boolean hasUserUsed(Long id, Integer idcode) {
		Codigo code = cr.isUsedByUser(id, idcode).orElse(null);
		return code != null;
	}

	@Override
	public void save(CodigoDTO code) {
		Carta carta = code.getCarta() == null ? null : crd.findById(code.getCarta().getId()).orElse(null);
		cr.save(CodigoDTO.convertToEntity(code, ur.findByClaimedCode(code.getId()), carta ));
	}
	
	@Override
	public void save(CodigoDTO code, UsuarioDTO user) {
		List<Usuario> users = ur.findByClaimedCode(code.getId());
		users.add(ur.findById(user.getId()).get());
		Carta carta = code.getCarta() == null ? null : crd.findById(code.getCarta().getId()).orElse(null);
		cr.save(CodigoDTO.convertToEntity(code, users, carta));
	}

}
