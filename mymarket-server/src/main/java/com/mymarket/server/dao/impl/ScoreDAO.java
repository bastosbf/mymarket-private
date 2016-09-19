package com.mymarket.server.dao.impl;

import org.hibernate.SessionFactory;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.model.Score;

public class ScoreDAO extends GenericDAO<Score> {

	public ScoreDAO(SessionFactory factory) {
		super(factory);
	}
}
