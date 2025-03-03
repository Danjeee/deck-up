package com.javi.deckup.utils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class Session {
	public static void logIn(String username, String password) {
		UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(username, password);
		SecurityContext sc = SecurityContextHolder.getContext();
		sc.setAuthentication(authReq);
	}
	public static void logOut() {
		SecurityContextHolder.clearContext();
	}
}
