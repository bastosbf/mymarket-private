package com.mymarket.server.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.dto.model.User;

public class UserDAO extends GenericDAO<User> {

	public UserDAO(SessionFactory factory) {
		super(factory);
	}
	
	public boolean exists(String uid) {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(User.class)
				.add(Restrictions.eq("uid", uid));
		List<User> list = criteria.list();
		return !list.isEmpty();
	}
}
