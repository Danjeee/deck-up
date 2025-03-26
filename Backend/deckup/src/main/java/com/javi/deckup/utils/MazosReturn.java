package com.javi.deckup.utils;

import java.util.List;

import com.javi.deckup.model.dto.MazoDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MazosReturn {

	private MazoDTO current;
	
	private List<MazoDTO> all;
	
}
