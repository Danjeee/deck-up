package com.javi.deckup.model.dto;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import com.javi.deckup.repository.entity.Carta;
import com.javi.deckup.repository.entity.Codigo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodigoDTO {

	private Integer id;
	
	private String codigo;
	
	private Integer currency;
	
	private CartaDTO carta;
	
	private Integer card_cant;
	
	private Integer uses_left;
	
	private Timestamp expiration_date;
	
	private List<UsuarioDTO> usuarios;
	
	public static CodigoDTO convertToDTO(Codigo input) {
		return CodigoDTO.builder()
					  .id(input.getId())
					  .codigo(input.getCodigo())
					  .currency(input.getCurrency())
					  .carta(CartaDTO.convertToDTO(input.getCarta()))
					  .card_cant(input.getCard_cant())
					  .uses_left(input.getUses_left())
					  .expiration_date(input.getExpiration_date())
					  .usuarios(input.getUsuarios().stream().map(u -> UsuarioDTO.convertToDTO(u)).collect(Collectors.toList()))
					  .build();
	}
	
	public static Codigo convertToEntity(CodigoDTO input) { //Sin carta
		return Codigo.builder()
					  .id(input.getId())
					  .codigo(input.getCodigo())
					  .currency(input.getCurrency())
					  .carta(null)
					  .card_cant(0)
					  .uses_left(input.getUses_left())
					  .expiration_date(input.getExpiration_date())
					  .usuarios(input.getUsuarios().stream().map(u -> UsuarioDTO.convertToEntity(u)).collect(Collectors.toList()))
					  .build();
	}
	
	public static Codigo convertToEntity(CodigoDTO input, Carta carta) { //Con carta
		return Codigo.builder()
					  .id(input.getId())
					  .codigo(input.getCodigo())
					  .currency(input.getCurrency())
					  .carta(carta)
					  .card_cant(input.getCard_cant())
					  .uses_left(input.getUses_left())
					  .expiration_date(input.getExpiration_date())
					  .usuarios(input.getUsuarios().stream().map(u -> UsuarioDTO.convertToEntity(u)).collect(Collectors.toList()))
					  .build();
	}
}
