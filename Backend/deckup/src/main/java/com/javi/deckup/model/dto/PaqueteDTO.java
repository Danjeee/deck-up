package com.javi.deckup.model.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.javi.deckup.repository.entity.Carta;
import com.javi.deckup.repository.entity.Paquete;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaqueteDTO {
private Integer id;
	
	private String nombre;
	
	private String descripcion;
	
	private String imagen;
	
	private Integer precio;
	
	private List<CartaDTO> cartas;
	
	public static PaqueteDTO convertToDTO(Paquete input) { // No incluir las cartas
		if (input == null) {
			return null;
		}
		return PaqueteDTO.builder()
						  .id(input.getId())
						  .nombre(input.getNombre())
						  .descripcion(input.getDescripcion())
						  .imagen(input.getImagen())
						  .precio(input.getPrecio())
						  .cartas(null)
						  .build();
	}
	
	public static PaqueteDTO convertToDTO(Paquete input, boolean mostrarCartas) {
		if (input == null) {
			return null;
		}
		return PaqueteDTO.builder()
						  .id(input.getId())
						  .nombre(input.getNombre())
						  .descripcion(input.getDescripcion())
						  .imagen(input.getImagen())
						  .precio(input.getPrecio())
						  .cartas(mostrarCartas ? input.getCartas().stream().map(c -> CartaDTO.convertToDTO(c)).collect(Collectors.toList()) : null)
						  .build();
	}
	
	public static Paquete convertToEntity(PaqueteDTO input, List<Carta> cartas) {
		return Paquete.builder()
						  .id(input.getId())
						  .nombre(input.getNombre())
						  .descripcion(input.getDescripcion())
						  .imagen(input.getImagen())
						  .precio(input.getPrecio())
						  .cartas(cartas)
						  .build();
	}
}
