package com.javi.deckup.utils;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.GameDTO;
import com.javi.deckup.model.dto.LineaDTO;

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
}
