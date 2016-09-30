package com.mymarket.server.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.dto.model.Notification;

public class NotificationDAO extends GenericDAO<Notification> {

	public NotificationDAO(SessionFactory factory) {
		super(factory);
	}
	
	public List<Notification> list() {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(Notification.class).add(Restrictions.eq("status", "H")).addOrder(Order.asc("kind"));
		List<Notification> list = criteria.list();
		return list;
	}
}
