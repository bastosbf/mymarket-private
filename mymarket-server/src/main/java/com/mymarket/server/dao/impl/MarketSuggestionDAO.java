package com.mymarket.server.dao.impl;

import org.hibernate.SessionFactory;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.dto.model.MarketSuggestion;

public class MarketSuggestionDAO extends GenericDAO<MarketSuggestion> {

	public MarketSuggestionDAO(SessionFactory factory) {
		super(factory);
	}
}
