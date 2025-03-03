package com.javi.deckup.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="jugadores-cartas")
public class PlayerCards {

	private Long id;
	
	private Integer cant;
	
	@ManyToOne
	@JoinColumn(name = "id_jugador")
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name = "id_carta")
	private Carta carta;
	
}
