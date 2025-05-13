package com.javi.deckup.service;

import com.javi.deckup.model.dto.TradeDTO;

public interface TradeService {

	TradeDTO save(TradeDTO trade);
	
	TradeDTO save(TradeDTO trade, String notify);
	
	TradeDTO findById(Long id);

	TradeDTO findByCode(String code);

}
