package com.javi.deckup.model.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.javi.deckup.repository.entity.Carta;
import com.javi.deckup.repository.entity.Rareza;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RarezaDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private String nombre;
	
	private double porcentaje;
	
	// No añado la lista de cartas porque es innecesaria
	
	public static RarezaDTO convertToDTO(Rareza input) {
		if (input == null) {
			return null;
		}
		return RarezaDTO.builder()
						 .id(input.getId())
						 .nombre(input.getNombre())
						 .porcentaje(input.getPorcentaje())
						 .build();
	}
	
	public static Rareza convertToEntity(RarezaDTO input, List<Carta> cartas) {
		return Rareza.builder()
						 .id(input.getId())
						 .nombre(input.getNombre())
						 .porcentaje(input.getPorcentaje())
						 .cartas(cartas)
						 .build();
	}
	
    public RarezaDTO(Integer id) {
        this.id = id;
    }
	
}
