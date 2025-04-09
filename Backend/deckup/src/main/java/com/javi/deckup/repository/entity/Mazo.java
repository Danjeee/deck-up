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
import lombok.ToString;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "mazos")
public class Mazo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nombre;
	
	@ManyToOne
	@JoinColumn(name = "carta1")
	private Carta carta1;
	
	@ManyToOne
	@JoinColumn(name = "carta2")
	private Carta carta2;
	
	@ManyToOne
	@JoinColumn(name = "carta3")
	private Carta carta3;
	
	@ManyToOne
	@JoinColumn(name = "carta4")
	private Carta carta4;
	
	@ManyToOne
	@JoinColumn(name = "carta5")
	private Carta carta5;
	
	@ManyToOne
	@JoinColumn(name = "carta6")
	private Carta carta6;
	
	@ManyToOne
	@JoinColumn(name = "carta7")
	private Carta carta7;
	
	@ManyToOne
	@JoinColumn(name = "carta8")
	private Carta carta8;
	
	@ManyToOne
	@JoinColumn(name = "id_usuario")
	@ToString.Exclude
	private Usuario usuario;
}
