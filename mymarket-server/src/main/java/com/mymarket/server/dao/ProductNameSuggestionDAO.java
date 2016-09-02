package com.mymarket.server.dao;

import org.hibernate.SessionFactory;

import com.mymarket.server.model.ProductNameSuggestion;

public class ProductNameSuggestionDAO extends GenericDAO<ProductNameSuggestion> {

	public ProductNameSuggestionDAO(SessionFactory factory) {
		super(factory);
	}
}
