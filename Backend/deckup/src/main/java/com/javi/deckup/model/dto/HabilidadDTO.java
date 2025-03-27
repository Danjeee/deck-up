package com.javi.deckup.model.dto;

import java.io.Serializable;
import java.util.List;

import com.javi.deckup.repository.entity.Carta;
import com.javi.deckup.repository.entity.Habilidad;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HabilidadDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private String nombre;
	
	private String descripcion;
	
	private char tipo;
	
	private Integer dmg;
	
	// No incluyo la lista de cartas porque es innecesaria
	
	public static HabilidadDTO convertToDTO(Habilidad input) {
		return HabilidadDTO.builder()
							.id(input.getId())
							.nombre(input.getNombre())
							.descripcion(input.getDescripcion())
							.tipo(input.getTipo())
							.dmg(input.getDmg())
							.build();
	}
	
	public static Habilidad convertToEntity(HabilidadDTO input, List<Carta> cartas) {
		return Habilidad.builder()
							.id(input.getId())
							.nombre(input.getNombre())
							.descripcion(input.getDescripcion())
							.tipo(input.getTipo())
							.dmg(input.getDmg())
							.cartas(cartas)
							.build();
	}
	
}
