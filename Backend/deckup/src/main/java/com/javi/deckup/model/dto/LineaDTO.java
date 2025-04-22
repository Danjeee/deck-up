package com.javi.deckup.model.dto;

import com.javi.deckup.repository.entity.Carta;
import com.javi.deckup.repository.entity.Game;
import com.javi.deckup.repository.entity.Linea;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LineaDTO {
	
	private Long id;
    private CartaDTO carta;
    private Integer vida;
    private Integer stun;
    private String stun_name;
    private Integer burn;
    private Integer poisn;
    private Integer bleed;
    private String turn_dmg_name;
    private GameDTO game;

	
	public static LineaDTO convertToDTO(Linea input) {
		if (input == null) {
			return null;
		}
		return LineaDTO.builder()
					   .id(input.getId())
					   .carta(CartaDTO.convertToDTO(input.getCarta()))
					   .vida(input.getVida())
					   .stun(input.getStun())
					   .stun_name(input.getStun_name())
					   .bleed(input.getBleed())
					   .burn(input.getBurn())
					   .poisn(input.getPoisn())
					   .game(GameDTO.builder().id(input.getGame().getId()).build())
					   .build();
	}
	
	public static Linea convertToEntity(LineaDTO input) {
		if (input == null) {
			return null;
		}
		return Linea.builder()
					   .id(input.getId())
					   .carta(Carta.builder().id(input.getCarta().getId()).build())
					   .vida(input.getVida())
					   .stun(input.getStun())
					   .stun_name(input.getStun_name())
					   .bleed(input.getBleed())
					   .burn(input.getBurn())
					   .poisn(input.getPoisn())
					   .game(Game.builder().id(input.getGame().getId()).build())
					   .build();
	}
	
}
