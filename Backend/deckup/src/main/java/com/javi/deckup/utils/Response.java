package com.javi.deckup.utils;

import com.javi.deckup.model.dto.UsuarioDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
 @NoArgsConstructor
 @AllArgsConstructor
public class Response {
	private int status;
	private String tit;
	private String msg;
	private UsuarioDTO user;
	
	public Response(int status, String tit, String msg) {
		super();
		this.status = status;
		this.tit = tit;
		this.msg = msg;
	}
}