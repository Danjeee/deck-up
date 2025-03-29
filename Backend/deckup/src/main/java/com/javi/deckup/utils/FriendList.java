package com.javi.deckup.utils;

import java.util.List;

import com.javi.deckup.model.dto.SolicitudAmistadDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendList {

	private List<SolicitudAmistadDTO> friends;
	
	private List<SolicitudAmistadDTO> pending;
	
}
