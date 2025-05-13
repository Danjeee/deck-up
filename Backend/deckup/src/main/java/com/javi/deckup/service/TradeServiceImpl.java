package com.javi.deckup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.model.dto.TradeDTO;
import com.javi.deckup.repository.dao.TradeRepository;
import com.javi.deckup.repository.entity.Trade;
import com.javi.deckup.web.websockets.NotificacionWSC;

@Service
public class TradeServiceImpl implements TradeService{

	@Autowired
	TradeRepository tr;
	
	@Autowired
	NotificacionWSC ws;
	
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
}
