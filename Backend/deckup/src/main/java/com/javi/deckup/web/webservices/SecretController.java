package com.javi.deckup.web.webservices;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.utils.Response;
import com.javi.deckup.utils.UserAction;

@RestController
@RequestMapping("/code")
public class SecretController {
	
	

	@PostMapping("/")
	public Response codecheck(@ModelAttribute UserAction data) {
		//TODO: process POST request
		
		return null;
	}
	
	
}
