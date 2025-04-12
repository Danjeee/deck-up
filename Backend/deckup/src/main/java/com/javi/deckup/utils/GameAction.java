package com.javi.deckup.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.GameDTO;
import com.javi.deckup.model.dto.LineaDTO;
import com.javi.deckup.model.dto.MazoDTO;
import com.javi.deckup.model.dto.PlayerStatusDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameAction {
	private Long game_id;
	private Integer card_id;
	private String linea;
	private Integer player;
	
	public static LineaDTO nuevaLinea(CartaDTO card, GameDTO game) {
		return LineaDTO.builder()
					   .carta(card)
					   .vida(card.getVida())
					   .game(game)
					   .build();
	}

	public static PlayerStatusDTO drawCard(PlayerStatusDTO p1) {
		boolean drawn = false;
		if (p1.getCarta1() == null && !drawn) {p1.setCarta1(drawRandom(p1.getUsuario().getMazo())); drawn = true;}
		if (p1.getCarta2() == null && !drawn) {p1.setCarta2(drawRandom(p1.getUsuario().getMazo())); drawn = true;}
		if (p1.getCarta3() == null && !drawn) {p1.setCarta3(drawRandom(p1.getUsuario().getMazo())); drawn = true;}
		if (p1.getCarta4() == null && !drawn) {p1.setCarta4(drawRandom(p1.getUsuario().getMazo())); drawn = true;}
		if (p1.getCarta5() == null && !drawn) {p1.setCarta5(drawRandom(p1.getUsuario().getMazo())); drawn = true;}
		return p1;
	}

	private static CartaDTO drawRandom(MazoDTO mazo) {
		List<CartaDTO> cartas = new ArrayList<>();
		cartas.add(mazo.getCarta1());
		cartas.add(mazo.getCarta2());
		cartas.add(mazo.getCarta3());
		cartas.add(mazo.getCarta4());
		cartas.add(mazo.getCarta5());
		cartas.add(mazo.getCarta6());
		cartas.add(mazo.getCarta7());
		cartas.add(mazo.getCarta8());
		Random rand = new Random();
		return cartas.get(rand.nextInt(cartas.size()-1));
	}
}
