package com.javi.deckup.model.dto;

import com.javi.deckup.repository.entity.Trade;
import com.javi.deckup.repository.entity.Usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeDTO {

private Long id;
	private String code;
	private String status;
	
	private UsuarioDTO player1;
	private UsuarioDTO player2;
	
	private String p1cards;
	private String p2cards;
	
	private String p1curr;
	private String p2curr;
	
	private Boolean p1c;
	private Boolean p2c;
	
	public static TradeDTO convertToDTO(Trade input) {
		return TradeDTO.builder()
					   .id(input.getId())
					   .code(input.getCode())
					   .status(input.getStatus())
					   .player1(UsuarioDTO.convertToDTO(input.getPlayer1()))
					   .player2(UsuarioDTO.convertToDTO(input.getPlayer2()))
					   .p1cards(input.getP1cards())
					   .p2cards(input.getP2cards())
					   .p1curr(input.getP1curr())
					   .p2curr(input.getP2curr())
					   .p1c(input.getP1c())
					   .p2c(input.getP2c())
					   .build();
	}
	public static Trade convertToEntity(TradeDTO input) {
		Usuario user2 = null;
		if (input.getPlayer2() != null) {
			user2 = Usuario.builder().id(input.getPlayer2().getId()).build();
		}
		return Trade.builder()
					   .id(input.getId())
					   .code(input.getCode())
					   .status(input.getStatus())
					   .player1(Usuario.builder().id(input.getPlayer1().getId()).build())
					   .player2(user2)
					   .p1cards(input.getP1cards())
					   .p2cards(input.getP2cards())
					   .p1curr(input.getP1curr())
					   .p2curr(input.getP2curr())
					   .p1c(input.getP1c())
					   .p2c(input.getP2c())
					   .build();
	}
}
