package com.javi.deckup.model.dto;

import com.javi.deckup.repository.entity.Game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {

	private Long id;
	private String status;

	private PlayerStatusDTO player1;
	private PlayerStatusDTO player2;

	private Integer turno;
	private Boolean p1_c;
	private Boolean p2_c;

	private LineaDTO l1_1;
	private LineaDTO l1_2;
	private LineaDTO l1_3;
	private LineaDTO l1_4;
	private LineaDTO l1_5;

	private LineaDTO l2_1;
	private LineaDTO l2_2;
	private LineaDTO l2_3;
	private LineaDTO l2_4;
	private LineaDTO l2_5;
	
	public static GameDTO convertToDTO(Game input) {
		return GameDTO.builder()
					  .id(input.getId())
					  .status(input.getStatus())
					  .player1(PlayerStatusDTO.convertToDTO(input.getPlayer1()))
					  .player2(PlayerStatusDTO.convertToDTO(input.getPlayer2()))
					  .turno(input.getTurno())
					  .l1_1(LineaDTO.convertToDTO(input.getL1_1()))
					  .l1_2(LineaDTO.convertToDTO(input.getL1_2()))
					  .l1_3(LineaDTO.convertToDTO(input.getL1_3()))
					  .l1_4(LineaDTO.convertToDTO(input.getL1_4()))
					  .l1_5(LineaDTO.convertToDTO(input.getL1_5()))
					  .l2_1(LineaDTO.convertToDTO(input.getL2_1()))
					  .l2_2(LineaDTO.convertToDTO(input.getL2_2()))
					  .l2_3(LineaDTO.convertToDTO(input.getL2_3()))
					  .l2_4(LineaDTO.convertToDTO(input.getL2_4()))
					  .l2_5(LineaDTO.convertToDTO(input.getL2_5()))
					  .p1_c(input.getP1_c())
					  .p2_c(input.getP2_c())
					  .build();
	}
	
	public static Game convertToEntity(GameDTO input) {
		return Game.builder()
					  .id(input.getId())
					  .status(input.getStatus())
					  .player1(PlayerStatusDTO.convertToEntity(input.getPlayer1()))
					  .player2(PlayerStatusDTO.convertToEntity(input.getPlayer2()))
					  .turno(input.getTurno())
					  .L1_1(LineaDTO.convertToEntity(input.getL1_1()))
					  .L1_2(LineaDTO.convertToEntity(input.getL1_2()))
					  .L1_3(LineaDTO.convertToEntity(input.getL1_3()))
					  .L1_4(LineaDTO.convertToEntity(input.getL1_4()))
					  .L1_5(LineaDTO.convertToEntity(input.getL1_5()))
					  .L2_1(LineaDTO.convertToEntity(input.getL2_1()))
					  .L2_2(LineaDTO.convertToEntity(input.getL2_2()))
					  .L2_3(LineaDTO.convertToEntity(input.getL2_3()))
					  .L2_4(LineaDTO.convertToEntity(input.getL2_4()))
					  .L2_5(LineaDTO.convertToEntity(input.getL2_5()))
					  .p1_c(input.getP1_c())
					  .p2_c(input.getP2_c())
					  .build();
	}

}
