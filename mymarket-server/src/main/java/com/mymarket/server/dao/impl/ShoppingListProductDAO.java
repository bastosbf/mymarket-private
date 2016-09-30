package com.mymarket.server.dao.impl;

import org.hibernate.SessionFactory;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.dto.model.ShoppingListProduct;

public class ShoppingListProductDAO extends GenericDAO<ShoppingListProduct> {

	public ShoppingListProductDAO(SessionFactory factory) {
		super(factory);
	}
}
