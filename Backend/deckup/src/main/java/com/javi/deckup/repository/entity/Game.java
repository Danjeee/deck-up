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

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "games")
public class Game {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "player1")
    private PlayerStatus player1;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "player2")
    private PlayerStatus player2;
    
    private Integer turno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "L1_1")
    private Linea L1_1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "L1_2")
    private Linea L1_2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "L1_3")
    private Linea L1_3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "L1_4")
    private Linea L1_4;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "L1_5")
    private Linea L1_5;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "L2_1")
    private Linea L2_1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "L2_2")
    private Linea L2_2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "L2_3")
    private Linea L2_3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "L2_4")
    private Linea L2_4;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "L2_5")
    private Linea L2_5;

}
