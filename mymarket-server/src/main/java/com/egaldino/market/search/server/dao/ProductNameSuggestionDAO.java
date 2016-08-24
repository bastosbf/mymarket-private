package com.egaldino.market.search.server.dao;

import org.hibernate.SessionFactory;

import com.egaldino.market.search.server.model.ProductNameSuggestion;

public class ProductNameSuggestionDAO extends GenericDAO<ProductNameSuggestion> {

	public ProductNameSuggestionDAO(SessionFactory factory) {
		super(factory);
	}
}
