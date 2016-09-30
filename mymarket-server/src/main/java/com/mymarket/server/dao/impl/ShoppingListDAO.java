package com.mymarket.server.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.dto.model.ShoppingtList;

public class ShoppingListDAO extends GenericDAO<ShoppingtList> {

	public ShoppingListDAO(SessionFactory factory) {
		super(factory);
	}

	public List<ShoppingtList> list(String uid) {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(ShoppingtList.class)
				.createAlias("user", "u")
				.add(Restrictions.eq("u.uid", uid))
				.addOrder(Order.asc("name"));
		List<ShoppingtList> list = criteria.list();
		return list;
	}
}
