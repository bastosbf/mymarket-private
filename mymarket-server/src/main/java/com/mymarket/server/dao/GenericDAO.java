package com.mymarket.server.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class GenericDAO<E> {
	protected SessionFactory factory;

	public GenericDAO(SessionFactory factory) {
		this.factory = factory;
	}

	public void add(E e) {
		Session session = factory.openSession();
		session.beginTransaction();
		try {
			session.save(e);
			session.getTransaction().commit();
		} finally {
			session.close();
		}
	}
	
	public void update(E e) {
		Session session = factory.openSession();
		session.beginTransaction();
		try {
			session.update(e);
			session.getTransaction().commit();
		} finally {
			session.close();
		}
	}


}
