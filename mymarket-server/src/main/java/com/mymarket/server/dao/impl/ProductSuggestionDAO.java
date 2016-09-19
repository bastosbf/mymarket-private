package com.mymarket.server.dao.impl;

import org.hibernate.SessionFactory;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.model.ProductSuggestion;

public class ProductSuggestionDAO extends GenericDAO<ProductSuggestion> {

	public ProductSuggestionDAO(SessionFactory factory) {
		super(factory);
	}
}
