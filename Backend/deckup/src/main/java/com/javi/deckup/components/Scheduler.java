package com.javi.deckup.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.javi.deckup.service.TiendaService;

@Component
public class Scheduler {

	@Autowired
	TiendaService ts;
	
	@Scheduled(cron = "0 0 0 * * *", zone = "Europe/Madrid")
//	@Scheduled(cron = "0 * * * * *", zone = "Europe/Madrid")
	public void changeStore() {
		ts.change();
	}
}
