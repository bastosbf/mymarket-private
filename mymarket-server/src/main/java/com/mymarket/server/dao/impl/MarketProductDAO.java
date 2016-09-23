package com.mymarket.server.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.model.MarketProduct;
import com.mymarket.server.model.ProductBarcode;

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
		int productId = -1;
		{
			Criteria criteria = session.createCriteria(ProductBarcode.class)
				.add(Restrictions.eq("barcode", barcode));
			List<ProductBarcode> list = criteria.list();
			if(!list.isEmpty()) {
				ProductBarcode productBarcode = list.get(0);
				productId = productBarcode.getProduct().getId();
			}
		}
		
		if(productId != -1) {		
			Criteria criteria = session.createCriteria(MarketProduct.class)				
					.createAlias("product", "p")
					.createAlias("market", "m")
					.add(Restrictions.eq("p.id", productId))
					.add(Restrictions.eq("m.place.id", place))
					.addOrder(Order.asc("price"));
			
			criteria.setMaxResults(maxResults);
			List<MarketProduct> list = criteria.list();
			return list;
		}
		return new ArrayList<MarketProduct>();
	}
	
	public List<MarketProduct> getByBarcodeAndCity(String barcode, int city, int maxResults) {
		Session session = factory.openSession();
		session.beginTransaction();
		int productId = -1;
		{
			Criteria criteria = session.createCriteria(ProductBarcode.class)
				.add(Restrictions.eq("barcode", barcode));
			List<ProductBarcode> list = criteria.list();
			if(!list.isEmpty()) {
				ProductBarcode productBarcode = list.get(0);
				productId = productBarcode.getProduct().getId();
			}
		}
		
		if(productId != -1) {
			Criteria criteria = session.createCriteria(MarketProduct.class)
					.createAlias("product", "p")
					.createAlias("market", "m")
					.createAlias("m.place", "mpl")
					.add(Restrictions.eq("p.id", productId))
					.add(Restrictions.eq("mpl.city.id", city))
					.addOrder(Order.asc("price"));
			criteria.setMaxResults(maxResults);
			List<MarketProduct> list = criteria.list();
			return list;
		}
		return new ArrayList<MarketProduct>();
	}
	
	public List<MarketProduct> getByBarcodeAndPlace(String barcode, int place) {
		return getByBarcodeAndPlace(barcode, place, -1);
	}
	
	public List<MarketProduct> getByBarcodeAndCity(String barcode, int city) {
		return getByBarcodeAndCity(barcode, city, -1);
	}
}
