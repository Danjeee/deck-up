package com.javi.deckup.model.dto;

import java.io.Serializable;

import com.javi.deckup.repository.entity.Carta;
import com.javi.deckup.repository.entity.PlayerCards;
import com.javi.deckup.repository.entity.Usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerCardsDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private Integer cant;
	
	private UsuarioDTO usuario;
	
	private CartaDTO carta;
	
	public static PlayerCardsDTO convertToDTO(PlayerCards input) {
		return PlayerCardsDTO.builder()
							  .id(input.getId())
							  .cant(input.getCant())
							  .usuario(UsuarioDTO.convertToDTO(input.getUsuario()))
							  .carta(CartaDTO.convertToDTO(input.getCarta()))
							  .build();
	}
	
	public static PlayerCards convertToEntity(PlayerCardsDTO input, Carta carta, Usuario usuario) {
		return PlayerCards.builder()
							  .id(input.getId())
							  .cant(input.getCant())
							  .usuario(usuario)
							  .carta(carta)
							  .build();
	}
}
