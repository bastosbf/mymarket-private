package com.mymarket.server.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.model.MarketList;

public class MarketListDAO extends GenericDAO<MarketList> {

	public MarketListDAO(SessionFactory factory) {
		super(factory);
	}

	public List<MarketList> list(String uid) {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(MarketList.class)
				.createAlias("user", "u")
				.add(Restrictions.eq("u.uid", uid))
				.addOrder(Order.asc("name"));
		List<MarketList> list = criteria.list();
		return list;
	}
}
