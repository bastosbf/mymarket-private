package com.mymarket.server.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.dto.model.ShoppingList;

public class ShoppingListDAO extends GenericDAO<ShoppingList> {

	public ShoppingListDAO(SessionFactory factory) {
		super(factory);
	}

	public List<ShoppingList> list(String uid) {
		Session session = factory.openSession();
		session.beginTransaction();
		try {
			Criteria criteria = session.createCriteria(ShoppingList.class)
					.createAlias("user", "u")
					.add(Restrictions.eq("u.uid", uid))
					.addOrder(Order.asc("name"));
			List<ShoppingList> list = criteria.list();
			return list;
		} finally {
			session.close();
		}
	}
	
	
}
