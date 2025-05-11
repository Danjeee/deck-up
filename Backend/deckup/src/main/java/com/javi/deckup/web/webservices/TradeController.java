package com.javi.deckup.web.webservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javi.deckup.service.TradeService;

@RestController
@RequestMapping("/trades")
public class TradeController {

	@Autowired
	TradeService ts;
}
