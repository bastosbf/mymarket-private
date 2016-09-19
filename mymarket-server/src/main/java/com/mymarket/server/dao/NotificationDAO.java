package com.mymarket.server.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mymarket.server.model.Notification;

public class NotificationDAO extends GenericDAO<Notification> {

	public NotificationDAO(SessionFactory factory) {
		super(factory);
	}
	
	public List<Notification> list(String status) {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(Notification.class).add(Restrictions.eq("status", status)).addOrder(Order.asc("id"));
		List<Notification> list = criteria.list();
		return list;
	}
}
