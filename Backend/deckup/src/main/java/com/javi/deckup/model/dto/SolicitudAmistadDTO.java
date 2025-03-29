package com.javi.deckup.model.dto;

import com.javi.deckup.repository.entity.SolicitudAmistad;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudAmistadDTO {

	private Long id;
	
	private UsuarioDTO usuario;
	
	private UsuarioDTO amigo;
	
	private Boolean aceptada;
	
	public static SolicitudAmistadDTO convertToDTO(SolicitudAmistad input) {
		return SolicitudAmistadDTO.builder()
								  .id(input.getId())
								  .usuario(UsuarioDTO.convertToDTO(input.getUsuario()))
								  .amigo(UsuarioDTO.convertToDTO(input.getAmigo()))
								  .aceptada(input.getAceptada())
								  .build();
	}
	
	public static SolicitudAmistad convertToEntity(SolicitudAmistadDTO input) {
		return SolicitudAmistad.builder()
								  .id(input.getId())
								  .usuario(UsuarioDTO.convertToEntity(input.getUsuario()))
								  .amigo(UsuarioDTO.convertToEntity(input.getAmigo()))
								  .aceptada(input.getAceptada())
								  .build();
	}
	
}
