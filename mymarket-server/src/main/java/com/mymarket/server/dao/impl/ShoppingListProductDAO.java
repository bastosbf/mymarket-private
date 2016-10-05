package com.mymarket.server.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.dto.model.ShoppingListProduct;

public class ShoppingListProductDAO extends GenericDAO<ShoppingListProduct> {

	public ShoppingListProductDAO(SessionFactory factory) {
		super(factory);
	}
	
	public List<ShoppingListProduct> list(String list) {
		Session session = factory.openSession();
		session.beginTransaction();
		try { 
			Criteria criteria = session.createCriteria(ShoppingListProduct.class)
					.createAlias("list", "l")
					.createAlias("product", "p")
					.add(Restrictions.eq("l.id", list))
					.addOrder(Order.asc("p.name"));		
			return criteria.list();
		} finally {
			session.close();
		}
	}
}
