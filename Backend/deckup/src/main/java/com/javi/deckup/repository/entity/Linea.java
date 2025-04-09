package com.javi.deckup.repository.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lineas")
public class Linea {

	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	 	@ManyToOne
		@JoinColumn(name = "carta")
	    private Carta carta;
	    private Integer vida;
	    private Integer stun;
	    private String stun_name;
	    private Integer turn_dmg;
	    private Integer turn_dmg_left;
	    private String turn_dmg_name;

	    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	    @JoinColumn(name = "game")
	    @ToString.Exclude
	    private Game game;
	
}


