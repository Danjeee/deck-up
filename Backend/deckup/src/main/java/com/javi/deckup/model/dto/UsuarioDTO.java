package com.javi.deckup.model.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import com.javi.deckup.repository.entity.Usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String username;
	
	private String email;
	
	private String pfp;
	
	private String password;
	
	private Integer currency;
	
	private Timestamp nextPayment;
	
	private List<RolDTO> rolesDTO;
	
	// No pongo la lista de codigos y cartas porque es una carga innecesaria de datos en la aplicación
	
	public static UsuarioDTO convertToDTO(Usuario input) {
		return UsuarioDTO.builder()
						  .id(input.getId())
						  .username(input.getUsername())
						  .email(input.getUsername())
						  .password(null) // No le paso la password al DTO para más seguridad
						  .currency(input.getCurrency())
						  .nextPayment(input.getNextPayment())
						  .rolesDTO(input.getRoles().stream().map(r -> RolDTO.convertToDTO(r, input)).collect(Collectors.toList()))
						  .build();
	}
	
	public static Usuario convertToEntity(UsuarioDTO input) {
		return Usuario.builder()
						  .id(input.getId())
						  .username(input.getUsername())
						  .email(input.getUsername())
						  .password(input.getPassword()) // Sin embargo, si se la paso a la entidad para poder operar con ella
						  .currency(input.getCurrency())
						  .nextPayment(input.getNextPayment())
						  .roles(input.getRolesDTO().stream().map(r -> RolDTO.convertToEntity(r, input)).collect(Collectors.toList()))
						  .build();
	}
	
}
