package com.javi.deckup.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.service.CartaService;
import com.javi.deckup.service.UsuarioService;

import lombok.Data;

@Service
public class GameEvents {
	
	@Autowired
	UsuarioService us;
	
	@Autowired
	CartaService cs;

	public CartaDTO drawCard(Long idPlayer) {
		UsuarioDTO user = us.findById(idPlayer);
		List<CartaDTO> cartas = new ArrayList<>();
		cartas.add(cs.findById(user.getMazo().getCarta1().getId()));
		cartas.add(cs.findById(user.getMazo().getCarta2().getId()));
		cartas.add(cs.findById(user.getMazo().getCarta3().getId()));
		cartas.add(cs.findById(user.getMazo().getCarta4().getId()));
		cartas.add(cs.findById(user.getMazo().getCarta5().getId()));
		cartas.add(cs.findById(user.getMazo().getCarta6().getId()));
		cartas.add(cs.findById(user.getMazo().getCarta7().getId()));
		cartas.add(cs.findById(user.getMazo().getCarta8().getId()));
		Random rand = new Random();
		return cartas.get(rand.nextInt(cartas.size()-1));
	};
	
	public Integer drawCard(Long idPlayer, String type) {
		UsuarioDTO user = us.findById(idPlayer);
		List<CartaDTO> cartas = new ArrayList<>();
		cartas.add(cs.findById(user.getMazo().getCarta1().getId()));
		cartas.add(cs.findById(user.getMazo().getCarta2().getId()));
		cartas.add(cs.findById(user.getMazo().getCarta3().getId()));
		cartas.add(cs.findById(user.getMazo().getCarta4().getId()));
		cartas.add(cs.findById(user.getMazo().getCarta5().getId()));
		cartas.add(cs.findById(user.getMazo().getCarta6().getId()));
		cartas.add(cs.findById(user.getMazo().getCarta7().getId()));
		cartas.add(cs.findById(user.getMazo().getCarta8().getId()));
		Random rand = new Random();
		return cartas.get(rand.nextInt(cartas.size()-1)).getId();
	};
	
}
