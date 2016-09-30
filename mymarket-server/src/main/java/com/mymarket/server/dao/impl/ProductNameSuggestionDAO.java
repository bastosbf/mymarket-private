package com.mymarket.server.dao.impl;

import org.hibernate.SessionFactory;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.dto.model.ProductNameSuggestion;

public class ProductNameSuggestionDAO extends GenericDAO<ProductNameSuggestion> {

	public ProductNameSuggestionDAO(SessionFactory factory) {
		super(factory);
	}
}
