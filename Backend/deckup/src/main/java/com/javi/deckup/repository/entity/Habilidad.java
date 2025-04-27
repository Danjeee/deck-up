package com.javi.deckup.repository.entity;

import java.util.List;

import jakarta.annotation.Nullable;
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

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="habilidades")
public class Habilidad {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String nombre;
	
	private String descripcion;
	
	private Integer dmg;
	
	private String especial;
	
    private Integer heal;
    
    private Integer freeze;
    
    private String freezeName;
    
    private Integer burn;
    
    private Integer poisn;
    
    private Integer bleed;
    
    private Integer prcnt;
    
    @ManyToOne
	@JoinColumn(name = "entorno")
    private Entorno entorno;
    
    private Integer loadAtq;
    
    private Integer crit;
    
    private Integer critMult;
    
    private Integer leth;
    
    private Integer esq;
    
    private Integer prcntUp;
    
    private Integer prcntDwn;
    
    private String color;
	
    //Innecesario
	//@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "habilidad")
	//@ToString.Exclude
	//private List<Carta> cartas;
}
