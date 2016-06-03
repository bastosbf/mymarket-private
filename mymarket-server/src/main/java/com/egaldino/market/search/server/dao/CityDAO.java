package com.egaldino.market.search.server.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.egaldino.market.search.server.model.City;
import com.egaldino.market.search.server.model.Place;

public class CityDAO extends GenericDAO<City> {

	public CityDAO(SessionFactory factory) {
		super(factory);
	}

	public List<City> list() {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(City.class)
				.addOrder(Order.asc("name"));
		List<City> list = criteria.list();
		return list;
	}

	public City get(int id) {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(City.class).add(Restrictions.eq("id", id));
		List<City> list = criteria.list();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

}
