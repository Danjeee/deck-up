package com.javi.deckup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javi.deckup.repository.dao.TradeRepository;

@Service
public class TradeServiceImpl implements TradeService{

	@Autowired
	TradeRepository tr;
}
