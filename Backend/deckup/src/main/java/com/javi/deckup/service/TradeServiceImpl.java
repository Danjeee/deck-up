package com.javi.deckup.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.CartaDTO;
import com.javi.deckup.model.dto.TradeCardsDTO;
import com.javi.deckup.model.dto.TradeDTO;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.repository.dao.TradeCardsRepository;
import com.javi.deckup.repository.dao.TradeRepository;
import com.javi.deckup.repository.entity.Trade;
import com.javi.deckup.repository.entity.TradeCards;
import com.javi.deckup.web.websockets.NotificacionWSC;

@Service
public class TradeServiceImpl implements TradeService{

	@Autowired
	TradeRepository tr;
	
	@Autowired
	NotificacionWSC ws;
	
	@Autowired
	TradeCardsRepository tcr;
	
	@Override
	public TradeDTO save(TradeDTO trade) {
		return TradeDTO.convertToDTO(tr.save(TradeDTO.convertToEntity(trade)));
	}

	@Override
	public TradeDTO findById(Long id) {
		Trade trade = tr.findById(id).orElse(null);
		return trade == null ? null : TradeDTO.convertToDTO(trade);
	}

	@Override
	public TradeDTO save(TradeDTO trade, String notify) {
		ws.tradeStatusChange(notify, trade.getId());
		return TradeDTO.convertToDTO(tr.save(TradeDTO.convertToEntity(trade)));
	}

	@Override
	public TradeDTO findByCode(String code) {
		Trade trade = tr.findByCode(code).orElse(null);
		return trade == null ? null : TradeDTO.convertToDTO(trade);
	}

	@Override
	public TradeCardsDTO save(TradeCardsDTO tc) {
		return TradeCardsDTO.convertToDTO(tcr.save(TradeCardsDTO.convertToEntity(tc)));
	}

	@Override
	public void sendWsTo(TradeDTO trade) {
		ws.tradeStatusChange(trade);
	}

	@Override
	public List<TradeCardsDTO> getAllCards(TradeDTO trade) {
		return tcr.getAllCards(trade.getId()).stream().map(c -> TradeCardsDTO.convertToDTO(c)).collect(Collectors.toList());
	}

	@Override
	public TradeCardsDTO findCardByTradeAndPlayerAndCard(TradeDTO trade, UsuarioDTO user, CartaDTO carta) {
		TradeCards tc = tcr.findCardByTradeAndPlayerAndCard(trade.getId(), user.getId(), carta.getId()).orElse(null);
		return tc == null ? null : TradeCardsDTO.convertToDTO(tc);
	}

	@Override
	public TradeCardsDTO findTCByCard(Long user_id) {
		TradeCards tc = tcr.findById(user_id).orElse(null);
		return tc == null ? null : TradeCardsDTO.convertToDTO(tc);
	}

	@Override
	public void removeTC(TradeCardsDTO pc) {
		tcr.deleteTC(pc.getId());
	}
}
