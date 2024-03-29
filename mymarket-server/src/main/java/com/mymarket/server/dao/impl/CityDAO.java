package com.mymarket.server.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.dto.model.City;

public class CityDAO extends GenericDAO<City> {

	public CityDAO(SessionFactory factory) {
		super(factory);
	}

	public List<City> list() {
		Session session = factory.openSession();
		session.beginTransaction();
		try {
			Criteria criteria = session.createCriteria(City.class)
					.addOrder(Order.asc("name"));
			List<City> list = criteria.list();		
			return list;
		} finally {
			session.close();
		}
	}
	
	public List<City> list(int state) {
		Session session = factory.openSession();
		session.beginTransaction();
		try {
			Criteria criteria = session.createCriteria(City.class)
					.addOrder(Order.asc("name"))
					.add(Restrictions.eq("state.id", state));
			List<City> list = criteria.list();
			return list;
		} finally {
			session.close();
		}
	}

	public City get(int id) {
		Session session = factory.openSession();
		session.beginTransaction();
		try {
			Criteria criteria = session.createCriteria(City.class).add(Restrictions.eq("id", id));
			List<City> list = criteria.list();
			if (!list.isEmpty()) {
				return list.get(0);
			}
			return null;
		} finally {
			session.close();
		}
	}

}
