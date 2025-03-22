package com.javi.deckup.model.dto;

import com.javi.deckup.repository.entity.OfertaGema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfertaGemaDTO {

	private Integer id;
	
	private String nombre;
	
	private String imagen;
	
	private double precio;
	
	private Integer cant;
	
	public static OfertaGemaDTO convertToDTO(OfertaGema input) {
		return OfertaGemaDTO.builder()
							.id(input.getId())
							.nombre(input.getNombre())
							.imagen(input.getImagen())
							.precio(input.getPrecio())
							.cant(input.getCant())
							.build();
	}
	
	public static OfertaGema convertToEntity(OfertaGemaDTO input) {
		return OfertaGema.builder()
							.id(input.getId())
							.nombre(input.getNombre())
							.imagen(input.getImagen())
							.precio(input.getPrecio())
							.cant(input.getCant())
							.build();
	}
	
}
