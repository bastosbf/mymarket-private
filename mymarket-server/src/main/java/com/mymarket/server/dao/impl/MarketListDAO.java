package com.mymarket.server.dao.impl;

import org.hibernate.SessionFactory;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.model.MarketList;

public class MarketListDAO extends GenericDAO<MarketList> {

	public MarketListDAO(SessionFactory factory) {
		super(factory);
	}
}
