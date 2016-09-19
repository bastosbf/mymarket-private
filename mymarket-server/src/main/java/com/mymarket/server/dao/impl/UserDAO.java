package com.mymarket.server.dao.impl;

import org.hibernate.SessionFactory;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.model.User;

public class UserDAO extends GenericDAO<User> {

	public UserDAO(SessionFactory factory) {
		super(factory);
	}
}
