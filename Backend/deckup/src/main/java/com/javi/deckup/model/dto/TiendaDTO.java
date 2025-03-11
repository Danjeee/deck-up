package com.javi.deckup.model.dto;

import com.javi.deckup.repository.entity.Tienda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TiendaDTO {

	private Integer id;
	
	private CartaDTO carta1;
	private CartaDTO carta2;
	private CartaDTO carta3;
	private CartaDTO carta4;
	private CartaDTO carta5;
	
	private PaqueteDTO paq1;
	private PaqueteDTO paq2;
	private PaqueteDTO paq3;
	
	public static TiendaDTO convertToDTO(Tienda input) {
		return TiendaDTO.builder()
						.id(input.getId())
						.carta1(CartaDTO.convertToDTO(input.getCarta1()))
						.carta2(CartaDTO.convertToDTO(input.getCarta2()))
						.carta3(CartaDTO.convertToDTO(input.getCarta3()))
						.carta4(CartaDTO.convertToDTO(input.getCarta4()))
						.carta5(CartaDTO.convertToDTO(input.getCarta5()))
						.paq1(PaqueteDTO.convertToDTO(input.getPaq1()))
						.paq2(PaqueteDTO.convertToDTO(input.getPaq2()))
						.paq3(PaqueteDTO.convertToDTO(input.getPaq3()))
						.build();
	}
	
}
