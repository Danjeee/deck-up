package com.javi.deckup.repository.entity;

import jakarta.persistence.CascadeType;
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
@Table(name = "player_status")
public class PlayerStatus {

	 	@Id 
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	 	@ManyToOne
		@JoinColumn(name = "usuario")
	    private Usuario usuario;
	 	
	 	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	    @JoinColumn(name = "game")
	    @ToString.Exclude
	    private Game game;
	    private Integer vida;
	    private Integer mana;
	    
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
	
}
