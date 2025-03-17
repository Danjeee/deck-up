package com.javi.deckup.utils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class Session {
	public static void logIn(String username, String password) {
		UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(username, password);
		SecurityContext sc = SecurityContextHolder.getContext();
		sc.setAuthentication(authReq);
	}
	public static void logOut() {
		SecurityContextHolder.clearContext();
	}
	public static UserDetails getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                return (UserDetails) principal;
            }
        }
        return null;
    }

    public static String getUsername() {
        UserDetails userDetails = getUser();
        if(userDetails != null){
            return userDetails.getUsername();
        }
        return null;
    }
}
