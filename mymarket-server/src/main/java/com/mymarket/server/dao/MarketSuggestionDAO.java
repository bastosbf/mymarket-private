package com.mymarket.server.dao;

import org.hibernate.SessionFactory;

import com.mymarket.server.model.MarketSuggestion;

public class MarketSuggestionDAO extends GenericDAO<MarketSuggestion> {

	public MarketSuggestionDAO(SessionFactory factory) {
		super(factory);
	}
}
