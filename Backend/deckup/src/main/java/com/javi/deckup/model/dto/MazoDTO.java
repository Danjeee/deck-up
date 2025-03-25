package com.javi.deckup.model.dto;

import com.javi.deckup.repository.entity.Mazo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MazoDTO {

	private Long id;
	
	private CartaDTO carta1;
	private CartaDTO carta2;
	private CartaDTO carta3;
	private CartaDTO carta4;
	private CartaDTO carta5;
	private CartaDTO carta6;
	private CartaDTO carta7;
	private CartaDTO carta8;
	
	private UsuarioDTO usuario;
	
	public static MazoDTO convertToDTO(Mazo input) {
		if (input == null) {return null;}
		return MazoDTO.builder()
					  .id(input.getId())
					  .carta1(CartaDTO.convertToDTO(input.getCarta1()))
					  .carta2(CartaDTO.convertToDTO(input.getCarta2()))
					  .carta3(CartaDTO.convertToDTO(input.getCarta3()))
					  .carta4(CartaDTO.convertToDTO(input.getCarta4()))
					  .carta5(CartaDTO.convertToDTO(input.getCarta5()))
					  .carta6(CartaDTO.convertToDTO(input.getCarta6()))
					  .carta7(CartaDTO.convertToDTO(input.getCarta7()))
					  .carta8(CartaDTO.convertToDTO(input.getCarta8()))
					  .usuario(UsuarioDTO.convertToDTO(input.getUsuario()))
					  .build();
	}
	
	public static Mazo convertToEntity(MazoDTO input) {
		if (input == null) {return null;}
		return Mazo.builder()
					  .id(input.getId())
					  .carta1(CartaDTO.convertToEntity(input.getCarta1(), null, null, null, null))
					  .carta2(CartaDTO.convertToEntity(input.getCarta2(), null, null, null, null))
					  .carta3(CartaDTO.convertToEntity(input.getCarta3(), null, null, null, null))
					  .carta4(CartaDTO.convertToEntity(input.getCarta4(), null, null, null, null))
					  .carta5(CartaDTO.convertToEntity(input.getCarta5(), null, null, null, null))
					  .carta6(CartaDTO.convertToEntity(input.getCarta6(), null, null, null, null))
					  .carta7(CartaDTO.convertToEntity(input.getCarta7(), null, null, null, null))
					  .carta8(CartaDTO.convertToEntity(input.getCarta8(), null, null, null, null))
					  .usuario(UsuarioDTO.convertToEntity(input.getUsuario()))
					  .build();
	}
	
}
