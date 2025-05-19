package com.javi.deckup.repository.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "trade")
	@ToString.Exclude
	private List<TradeCards> cartas;
	
	@Column(name = "player1_currency")
	private Integer p1curr;
	@Column(name = "player2_currency")
	private Integer p2curr;
	
	private Boolean p1c;
	private Boolean p2c;
}
