package com.mymarket.server.dao.impl;

import org.hibernate.SessionFactory;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.model.MarketListProduct;

public class MarketListProductDAO extends GenericDAO<MarketListProduct> {

	public MarketListProductDAO(SessionFactory factory) {
		super(factory);
	}
}
