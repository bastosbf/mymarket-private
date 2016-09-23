package com.mymarket.server.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.model.State;

public class StateDAO extends GenericDAO<State> {

	public StateDAO(SessionFactory factory) {
		super(factory);
	}

	public List<State> list() {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(State.class)
				.addOrder(Order.asc("acronym"));
		List<State> list = criteria.list();
		return list;
	}

	public State get(int id) {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(State.class).add(Restrictions.eq("id", id));
		List<State> list = criteria.list();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

}
