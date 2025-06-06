package com.javi.deckup.service;

import java.util.List;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.TradeCardsDTO;
import com.javi.deckup.model.dto.TradeDTO;
import com.javi.deckup.model.dto.UsuarioDTO;

public interface TradeService {

	TradeDTO save(TradeDTO trade);
	
	TradeDTO save(TradeDTO trade, String notify);
	
	TradeDTO findById(Long id);

	TradeDTO findByCode(String code);

	TradeCardsDTO save(TradeCardsDTO tc);

	void sendWsTo(TradeDTO trade);

	List<TradeCardsDTO> getAllCards(TradeDTO trade);

	TradeCardsDTO findCardByTradeAndPlayerAndCard(TradeDTO trade, UsuarioDTO user, CartaDTO carta);

	TradeCardsDTO findTCByCard(Long user_id);

	void removeTC(TradeCardsDTO pc);

	void save(TradeDTO trade, boolean b);

	void sendWsTo(TradeDTO trade, String string);

	List<TradeDTO> getPast(Long id);

}
