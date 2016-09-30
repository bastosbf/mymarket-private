package com.mymarket.server.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.dto.model.Market;

public class MarketDAO extends GenericDAO<Market> {

	public MarketDAO(SessionFactory factory) {
		super(factory);
	}

	public List<Market> listByCity(int city) {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(Market.class)
				.createAlias("place", "pl")
				.add(Restrictions.eq("pl.city.id", city))
				.addOrder(Order.asc("name"));
		List<Market> list = criteria.list();
		return list;
	}
	
	public List<Market> listByPlace(int place) {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(Market.class)
				.createAlias("place", "pl")
				.add(Restrictions.eq("pl.id", place))
				.addOrder(Order.asc("name"));
		List<Market> list = criteria.list();
		return list;
	}

	public Market get(int id) {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(Market.class)
				.add(Restrictions.eq("id", id));
		List<Market> list = criteria.list();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
}
