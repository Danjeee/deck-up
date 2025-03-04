package com.javi.deckup.model.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.javi.deckup.repository.entity.Rol;
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
public class RolDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Long id;

	private String nombre;
	
	@ToString.Exclude
	@JsonIgnore
	private UsuarioDTO usuarioDTO;
	
	public static Rol convertToEntity(RolDTO input, UsuarioDTO usuarioDTO) {
		return Rol.builder()
					.id(input.getId())
					.nombre(input.getNombre())
					.usuario(Usuario.builder().id(usuarioDTO.getId()).build())
					.build();
	}
	
	public static RolDTO convertToDTO(Rol input, Usuario usuario) {
		return RolDTO.builder()
					.id(input.getId())
					.nombre(input.getNombre())
					.usuarioDTO(UsuarioDTO.builder().id(usuario.getId()).build())
					.build();
	}
	
}
