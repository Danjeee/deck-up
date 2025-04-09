package com.javi.deckup.model.dto;

import com.javi.deckup.repository.entity.Carta;
import com.javi.deckup.repository.entity.Game;
import com.javi.deckup.repository.entity.PlayerStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerStatusDTO {
	
	private Long id;
    private UsuarioDTO usuario;
    private GameDTO game;
    private Integer vida;
    private Integer mana;
    private CartaDTO carta1;
    private CartaDTO carta2;
    private CartaDTO carta3;
    private CartaDTO carta4;
    private CartaDTO carta5;

	public static PlayerStatusDTO convertToDTO(PlayerStatus input) {
		if (input == null) {
			return null;
		}
		return PlayerStatusDTO.builder()
							  .id(input.getId())
							  .usuario(UsuarioDTO.convertToDTO(input.getUsuario()))
							  .game(GameDTO.builder().id(input.getGame().getId()).build())
							  .vida(input.getVida())
							  .mana(input.getMana())
							  .carta1(CartaDTO.convertToDTO(input.getCarta1()))
							  .carta2(CartaDTO.convertToDTO(input.getCarta2()))
							  .carta3(CartaDTO.convertToDTO(input.getCarta3()))
							  .carta4(CartaDTO.convertToDTO(input.getCarta4()))
							  .carta5(CartaDTO.convertToDTO(input.getCarta5()))
							  .build();
	}
	
	public static PlayerStatus convertToEntity(PlayerStatusDTO input) {
		if (input == null) {
			return null;
		}
		Carta carta1 = (input.getCarta1() == null ? null : Carta.builder().id(input.getCarta1().getId()).build());
		Carta carta2 = (input.getCarta2() == null ? null : Carta.builder().id(input.getCarta2().getId()).build());
		Carta carta3 = (input.getCarta3() == null ? null : Carta.builder().id(input.getCarta3().getId()).build());
		Carta carta4 = (input.getCarta4() == null ? null : Carta.builder().id(input.getCarta4().getId()).build());
		Carta carta5 = (input.getCarta5() == null ? null : Carta.builder().id(input.getCarta5().getId()).build());
		return PlayerStatus.builder()
				  .id(input.getId())
				  .usuario(UsuarioDTO.convertToEntity(input.getUsuario()))
				  .game(Game.builder().id(input.getGame().getId()).build())
				  .vida(input.getVida())
				  .mana(input.getMana())
				  .carta1(carta1)
				  .carta2(carta2)
				  .carta3(carta3)
				  .carta4(carta4)
				  .carta5(carta5)
				  .build();
	}
}
