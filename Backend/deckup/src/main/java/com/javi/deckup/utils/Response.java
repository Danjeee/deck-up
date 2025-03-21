package com.javi.deckup.utils;

import java.util.List;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.PaqueteDTO;
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
	private List<CartaDTO> cartas;
	private PaqueteDTO pack;

	public Response(int status, String tit, String msg) {
		super();
		this.status = status;
		this.tit = tit;
		this.msg = msg;
	}
	
	public Response(int status, String tit, String msg, UsuarioDTO user) {
		super();
		this.status = status;
		this.tit = tit;
		this.msg = msg;
		this.user = user;
	}
	
	public Response(List<CartaDTO> cards, PaqueteDTO pack) {
		super();
		this.status = 200;
		this.cartas = cards;
		this.pack = pack;
	}

	public static Response error(String msg) {
		return Response.builder().status(400).tit("Error").msg(msg).build();
	}

	public static Response error(String msg, int status) {
		return Response.builder().status(status).tit("Error").msg(msg).build();
	}
	
	public static Response success(String msg, String tit) {
		return Response.builder().status(200).tit(tit).msg(msg).build();
	}
	
	public static Response success(String msg) {
		return Response.builder().status(200).tit("¡Exito!").msg(msg).build();
	}
}