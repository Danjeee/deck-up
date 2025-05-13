package com.javi.deckup.repository.entity;

import jakarta.persistence.Column;
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

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "trades")
public class Trade {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String code;
	private String status;
	
	@ManyToOne
	@JoinColumn(name = "player1")
	private Usuario player1;
	
	@ManyToOne
	@JoinColumn(name = "player2")
	private Usuario player2;
	
	@Column(name = "player1_cards")
	private String p1cards;
	@Column(name = "player2_cards")
	private String p2cards;
	
	@Column(name = "player1_currency")
	private String p1curr;
	@Column(name = "player2_currency")
	private String p2curr;
	
	private Boolean p1c;
	private Boolean p2c;
}
