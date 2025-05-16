package com.javi.deckup.model.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.javi.deckup.repository.entity.Carta;
import com.javi.deckup.repository.entity.Trade;
import com.javi.deckup.repository.entity.TradeCards;
import com.javi.deckup.repository.entity.Usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeCardsDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private Integer cant;
	
	private UsuarioDTO usuario;
	
	private CartaDTO carta;
	
	@JsonBackReference
	private TradeDTO trade;
	
	public static TradeCardsDTO convertToDTO(TradeCards input) {
		TradeDTO trade = null;
		if (input.getTrade() != null) {
			trade = TradeDTO.builder().id(input.getTrade().getId()).build();
		}
		return TradeCardsDTO.builder()
							  .id(input.getId())
							  .cant(input.getCant())
							  .usuario(UsuarioDTO.convertToDTO(input.getUsuario()))
							  .carta(CartaDTO.convertToDTO(input.getCarta()))
							  .trade(trade)
							  .build();
	}
	
	public static TradeCards convertToEntity(TradeCardsDTO input) {
		Trade trade = null;
		if (input.getTrade() != null) {
			trade = Trade.builder().id(input.getTrade().getId()).build();
		}
		Carta card = null;
		if (input.getTrade() != null) {
			card = Carta.builder().id(input.getCarta().getId()).build();
		}
		Usuario user = null;
		if (input.getUsuario() != null) {
			user = Usuario.builder().id(input.getUsuario().getId()).build();
		}
		return TradeCards.builder()
							  .id(input.getId())
							  .cant(input.getCant())
							  .usuario(user)
							  .carta(card)
							  .trade(trade)							  
							  .build();
	}
}
