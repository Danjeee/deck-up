package com.javi.deckup.repository.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="cartas")
public class Carta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String nombre;
	
	private String descripcion;
	
	private String imagen;
	
	private Integer precio;
	
	private boolean exclusive;
	
	@ManyToOne
	@JoinColumn(name = "rareza")
	private Rareza rareza;
	
	@ManyToOne
	@JoinColumn(name = "paquete")
	private Paquete paquete;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "carta")
	@ToString.Exclude
	private List<PlayerCards> usuarios;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "carta")
	@ToString.Exclude
	private List<Codigo> codigos;
	
	@ManyToOne
	@JoinColumn(name = "habilidad")
	private Habilidad habilidad;
}
