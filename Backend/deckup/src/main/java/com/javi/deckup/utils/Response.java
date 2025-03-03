package com.javi.deckup.utils;

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
	private String msg;
}