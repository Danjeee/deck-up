package com.javi.deckup.model.dto;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import com.javi.deckup.repository.entity.Carta;
import com.javi.deckup.repository.entity.Codigo;
import com.javi.deckup.repository.entity.Habilidad;
import com.javi.deckup.repository.entity.Paquete;
import com.javi.deckup.repository.entity.PlayerCards;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartaDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private String nombre;
	
	private String descripcion;
	
	private String imagen;
	
	private Integer precio;
	
	private Long copias;
	
	private RarezaDTO rarezaDTO;
	
	private List<HabilidadDTO> habilidadesDTO;
	
	private PaqueteDTO paqueteDTO;
	
	// No añado la lista de codigos y usuarios porque es una carga de datos innecesaria
	
	public static CartaDTO convertToDTO(Carta input) { // Convert a DTO sin hacer el proceso de conseguir las copias
		return CartaDTO.builder()
						.id(input.getId())
						.nombre(input.getNombre())
						.descripcion(input.getDescripcion())
						.imagen(input.getImagen())
						.precio(input.getPrecio())
						.copias(null)
						.rarezaDTO(RarezaDTO.convertToDTO(input.getRareza()))
						.habilidadesDTO(input.getHabilidades().stream().map(h -> HabilidadDTO.convertToDTO(h)).collect(Collectors.toList()))
						.paqueteDTO(PaqueteDTO.convertToDTO(input.getPaquete()))
						.build();
						
	}
	
	public static CartaDTO convertToDTO(Carta input, boolean getCopies) { // Convert a DTO con las copias incluidas (o no)
		return CartaDTO.builder()
						.id(input.getId())
						.nombre(input.getNombre())
						.descripcion(input.getDescripcion())
						.imagen(input.getImagen())
						.precio(input.getPrecio())
						.copias(getCopies ? calculateCopias(input.getUsuarios()) : null)
						.rarezaDTO(RarezaDTO.convertToDTO(input.getRareza()))
						.habilidadesDTO(input.getHabilidades().stream().map(h -> HabilidadDTO.convertToDTO(h)).collect(Collectors.toList()))
						.paqueteDTO(PaqueteDTO.convertToDTO(input.getPaquete()))
						.build();
						
	}
	
	// A la hora de convertir a entidad (para guardar) tendremos que seleccionar si la guardamos cargando las respectivas listas en el servicio
	// Habrá que buscar todas estas listas si queremos EDITAR una carta (cosa que seguramente no se pueda)
	// A la hora de guardar una nueva simplemente seteamos codigos y usuarios a null
	public static Carta convertToEntity(CartaDTO input, List<Carta> cartasrareza, List<Habilidad> cartashabilidad, List<PlayerCards> usuarios, List<Codigo> cartascodigo) {
		return Carta.builder()
						.id(input.getId())
						.nombre(input.getNombre())
						.descripcion(input.getDescripcion())
						.imagen(input.getImagen())
						.precio(input.getPrecio())
						.rareza(RarezaDTO.convertToEntity(input.getRarezaDTO(), cartasrareza))
						.habilidades(input.getHabilidadesDTO().stream().map(h -> HabilidadDTO.convertToEntity(h, cartasrareza)).collect(Collectors.toList()))
						.paquete(Paquete.builder().id(input.getPaqueteDTO().getId()).build())
						.usuarios(usuarios)
						.codigos(cartascodigo)
						.build();
						
	}
	
	private static Long calculateCopias(List<PlayerCards> input) {
		Long aux = 0L;
		for (PlayerCards i : input) {
			aux += (i.getCant());
		}
		return aux;
	}
	

	
}
