package com.mymarket.server.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.dto.model.Place;

public class PlaceDAO extends GenericDAO<Place> {

	public PlaceDAO(SessionFactory factory) {
		super(factory);
	}

	public List<Place> list(int city) {
		Session session = factory.openSession();
		session.beginTransaction();
		try {
			Criteria criteria = session.createCriteria(Place.class)
					.createAlias("city", "c")
					.add(Restrictions.eq("c.id", city))
					.addOrder(Order.asc("name"));
			List<Place> list = criteria.list();
			return list;
		} finally {
			session.close();
		}
	}

	public Place get(int id) {
		Session session = factory.openSession();
		session.beginTransaction();
		try {
			Criteria criteria = session.createCriteria(Place.class).add(Restrictions.eq("id", id));
			List<Place> list = criteria.list();
			if (!list.isEmpty()) {
				return list.get(0);
			}
			return null;
		} finally {
			session.close();
		}
	}

}
