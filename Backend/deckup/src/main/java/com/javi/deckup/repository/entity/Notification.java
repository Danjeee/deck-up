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

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notifications")
public class Notification {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@ManyToOne
	@JoinColumn(name = "user")
    private Usuario user;
    private String title;
    private String msg;
    private Integer currency;

	@ManyToOne
	@JoinColumn(name = "card")
    private Carta card;
	
	@Column(name = "card_cant")
    private Integer cardCant;
    private Boolean claimed;
}
