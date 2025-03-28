package com.javi.deckup.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "entornos")
public class Entorno {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String nombre;
	
	 private String descripcion;
	 
	 private Integer dmg;
	 
	 private Integer heal;
	 
	 private Double prnctUp;
	 
	 private Double prcntDwn;
	 
	 private Double crit;
	 
	 private Double critDmg;
	 
	 private String especial;
	
}
