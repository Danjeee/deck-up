package com.javi.deckup.model.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.javi.deckup.repository.entity.Mazo;
import com.javi.deckup.repository.entity.Usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MazoDTO {

	private Long id;
	private String nombre;
	
	private CartaDTO carta1;
	private CartaDTO carta2;
	private CartaDTO carta3;
	private CartaDTO carta4;
	private CartaDTO carta5;
	private CartaDTO carta6;
	private CartaDTO carta7;
	private CartaDTO carta8;
	
	@ToString.Exclude
	@JsonBackReference
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
					  .nombre(input.getNombre())
					  .usuario(UsuarioDTO.builder().id(input.getUsuario().getId()).build())
					  .build();
	}
	
	public static Mazo convertToEntity(MazoDTO input) {
		if (input == null) {return null;}
		return Mazo.builder()
					  .id(input.getId())
					  .carta1(CartaDTO.convertToEntity(input.getCarta1()))
					  .carta2(CartaDTO.convertToEntity(input.getCarta2()))
					  .carta3(CartaDTO.convertToEntity(input.getCarta3()))
					  .carta4(CartaDTO.convertToEntity(input.getCarta4()))
					  .carta5(CartaDTO.convertToEntity(input.getCarta5()))
					  .carta6(CartaDTO.convertToEntity(input.getCarta6()))
					  .carta7(CartaDTO.convertToEntity(input.getCarta7()))
					  .carta8(CartaDTO.convertToEntity(input.getCarta8()))
					  .nombre(input.getNombre())
					  .usuario(Usuario.builder().id(input.getUsuario().getId()).build())
					  .build();
	}
	
}
