package com.javi.deckup.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="tienda")
public class Tienda {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carta1")
	private Carta carta1;
	@OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carta2")
	private Carta carta2;
	@OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carta3")
	private Carta carta3;
	@OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carta4")
	private Carta carta4;
	@OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carta5")
	private Carta carta5;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paq1")
	private Paquete paq1;
	@OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paq2")
	private Paquete paq2;
	@OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paq3")
	private Paquete paq3;
}
