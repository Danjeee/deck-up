package com.javi.deckup.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAction {

	private String user_auth;
	
	private Integer artifact_id;
	
	private Long artifact_long;
	
	private Integer artifact_aux;
	
	private Long user_id;
	
	private String code;
	
}
