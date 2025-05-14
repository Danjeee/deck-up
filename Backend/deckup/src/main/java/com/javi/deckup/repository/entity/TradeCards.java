package com.javi.deckup.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name="trades_cartas")
public class TradeCards {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Integer cant;
	
	@ManyToOne
	@JoinColumn(name = "id_jugador")
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name = "id_carta")
	private Carta carta;
	
	@ManyToOne
	@JoinColumn(name = "id_trade")
	private Trade trade;
	
}