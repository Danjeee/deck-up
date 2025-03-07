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
	
	private String auth;

	private Long id;
	
	private String username;
	
	private String email;
	
	private String pfp;
	
	private String password;
	
	private Integer currency;
	
	private boolean estado;
	
	private Timestamp nextPayment;
	
	private List<RolDTO> rolesDTO;
	
	// No pongo la lista de codigos y cartas porque es una carga innecesaria de datos en la aplicación
	
	public static UsuarioDTO convertToDTO(Usuario input) {
		return UsuarioDTO.builder()
						  .id(input.getId())
						  .username(input.getUsername())
						  .email(input.getEmail())
						  .password(null) // No le paso la password al DTO para más seguridad
						  .auth(null)
						  .currency(input.getCurrency())
						  .nextPayment(input.getNextPayment())
						  .pfp(input.getPfp())
						  .estado(input.isEstado())
						  .rolesDTO(input.getRoles() == null ? null : input.getRoles().stream().map(r -> RolDTO.convertToDTO(r, input)).collect(Collectors.toList()))
						  .build();
	}
	
	public static UsuarioDTO convertToDTO(Usuario input, boolean wantPass) {
		return UsuarioDTO.builder()
						  .id(input.getId())
						  .username(input.getUsername())
						  .email(input.getEmail())
						  .password(wantPass ? input.getPassword() : null) // Le paso la contraseña SOLO si se pide
						  .auth(wantPass ? input.getAuth() : null)
						  .currency(input.getCurrency())
						  .nextPayment(input.getNextPayment())
						  .pfp(input.getPfp())
						  .estado(input.isEstado())
						  .rolesDTO(input.getRoles() == null ? null : input.getRoles().stream().map(r -> RolDTO.convertToDTO(r, input)).collect(Collectors.toList()))
						  .build();
	}
	
	public static Usuario convertToEntity(UsuarioDTO input) {
		return Usuario.builder()
						  .id(input.getId())
						  .username(input.getUsername())
						  .email(input.getEmail())
						  .estado(input.isEstado())
						  .pfp(input.getPfp() == null ? "user.png" : input.getPfp())
						  .password(input.getPassword()) // Sin embargo, si se la paso a la entidad para poder operar con ella
						  .auth(input.getAuth())
						  .currency(input.getCurrency())
						  .nextPayment(input.getNextPayment())
						  .roles(input.getRolesDTO() == null ? null : input.getRolesDTO().stream().map(r -> RolDTO.convertToEntity(r, input)).collect(Collectors.toList()))
						  .build();
	}
	
}
