package com.mymarket.server.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.model.MarketProduct;

public class MarketProductDAO extends GenericDAO<MarketProduct> {

	public MarketProductDAO(SessionFactory factory) {
		super(factory);
	}

	public List<MarketProduct> getByMarket(int market) {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(MarketProduct.class)
				.createAlias("product", "p")
				.createAlias("market", "m")
				.add(Restrictions.eq("m.id", market))
				.addOrder(Order.asc("p.name"));
		List<MarketProduct> list = criteria.list();		
		return list;
	}
	
	public void updatePrice(int market, int product, float price) {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(MarketProduct.class)
				.createAlias("product", "p")
				.createAlias("market", "m")
				.add(Restrictions.eq("p.id", product))
				.add(Restrictions.eq("m.id", market));
		List<MarketProduct> list = criteria.list();
		if(!list.isEmpty()) {
			MarketProduct mp = list.get(0);
			mp.setPrice(price);
			update(mp);
		}		
	}
	
	public void confirmPrice(int market, int product) {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(MarketProduct.class)
				.createAlias("product", "p")
				.createAlias("market", "m")
				.add(Restrictions.eq("p.id", product))
				.add(Restrictions.eq("m.id", market));
		List<MarketProduct> list = criteria.list();
		if(!list.isEmpty()) {
			MarketProduct mp = list.get(0);
			update(mp);
		}		
	}
	
	public List<MarketProduct> getByBarcodeAndPlace(String barcode, int place, int maxResults) {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(MarketProduct.class)				
				.createAlias("product_barcode", "pb")
				.createAlias("product", "p")
				.createAlias("market", "m")
				.add(Restrictions.eq("pb.barcode", barcode))
				.add(Restrictions.eq("p.id", "pb.product"))
				.add(Restrictions.eq("m.place.id", place))
				.addOrder(Order.asc("price"));
		criteria.setMaxResults(maxResults);
		List<MarketProduct> list = criteria.list();		
		return list;
	}
	
	public List<MarketProduct> getByBarcodeAndCity(String barcode, int city, int maxResults) {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(MarketProduct.class)
				.createAlias("product_barcode", "pb")
				.createAlias("product", "p")
				.createAlias("market", "m")
				.createAlias("m.place", "mpl")
				.add(Restrictions.eq("pb.barcode", barcode))
				.add(Restrictions.eq("p.id", "pb.product"))
				.add(Restrictions.eq("mpl.city.id", city))
				.addOrder(Order.asc("price"));
		criteria.setMaxResults(maxResults);
		List<MarketProduct> list = criteria.list();		
		return list;
	}
	
	public List<MarketProduct> getByBarcodeAndPlace(String barcode, int place) {
		return getByBarcodeAndPlace(barcode, place, -1);
	}
	
	public List<MarketProduct> getByBarcodeAndCity(String barcode, int city) {
		return getByBarcodeAndCity(barcode, city, -1);
	}
}
