package com.egaldino.market.search.server.dao;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.egaldino.market.search.server.model.EnhancedProduct;
import com.egaldino.market.search.server.model.MarketProduct;
import com.egaldino.market.search.server.model.Product;

public class ProductDAO extends GenericDAO<Product> {

	public ProductDAO(SessionFactory factory) {
		super(factory);
	}

	public List<Product> listByMarket(int market) {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(MarketProduct.class).add(Restrictions.eq("market.id", market));
		List<MarketProduct> list = criteria.list();
		List<Product> products = new ArrayList<Product>();
		for (MarketProduct mp : list) {
			Product product = mp.getProduct();
			products.add(product);
		}
		return products;
	}

	public List<Product> listByPlace(int place) {
		Session session = factory.openSession();
		try {
			session.beginTransaction();
			Criteria criteria = session.createCriteria(MarketProduct.class).createAlias("market", "m").add(Restrictions.eq("m.place.id", place));
			List<MarketProduct> list = criteria.list();
			List<Product> products = new ArrayList<Product>();
			for (MarketProduct mp : list) {
				Product product = mp.getProduct();
				products.add(product);
			}
			return products;
		} finally {
			session.close();
		}
	}

	public List<Product> listByName(String[] tokens) {
		Session session = factory.openSession();
		try {
			session.beginTransaction();
			Criteria criteria = session.createCriteria(Product.class);
			for (String token : tokens) {
				if (token.length() < 3) {
					return new ArrayList<Product>();
				}
				criteria = criteria.add(Restrictions.like("name", token.toUpperCase(), MatchMode.ANYWHERE));
			}
			List<Product> products = criteria.list();
			return products;
		} finally {
			session.close();
		}
	}

	public List<EnhancedProduct> listWithPriceByName(String[] tokens, int city, int place) {
		Session session = factory.openSession();
		try {
			session.beginTransaction();
			Criteria criteria = session.createCriteria(MarketProduct.class)
					.createAlias("product", "p")
					.createAlias("market", "m")
					.createAlias("m.place", "pl");
			if (place > 0) {
				criteria = criteria.add(Restrictions.eq("m.place.id", place));
			} else if (city > 0) {
				criteria = criteria.add(Restrictions.eq("pl.city.id", city));
			}
			for (String token : tokens) {
				if (token.length() < 3) {
					return new ArrayList<EnhancedProduct>();
				}
				criteria = criteria.add(Restrictions.like("p.name", token.toUpperCase(), MatchMode.ANYWHERE));
			}			
			criteria.addOrder(Order.asc("price"));
			List<MarketProduct> list = criteria.list();
			Set<EnhancedProduct> products = new LinkedHashSet<EnhancedProduct>();
			for (MarketProduct mp : list) {
				Product product = mp.getProduct();
				EnhancedProduct enhancedProduct = new EnhancedProduct(product);
				enhancedProduct.setLowestPrice(mp.getPrice());
				products.add(enhancedProduct);
			}
			return new ArrayList<EnhancedProduct>(products);
		} finally {
			session.close();
		}
	}

	public Product get(String barcode) {
		Session session = factory.openSession();
		try {
			session.beginTransaction();
			Criteria criteria = session.createCriteria(Product.class).add(Restrictions.eq("barcode", barcode));
			List<Product> list = criteria.list();
			if (!list.isEmpty()) {
				return list.get(0);
			}
			return null;
		} finally {
			session.flush();
			session.close();
		}
	}

	public void updateName(String barcode, String name) {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(Product.class).add(Restrictions.eq("barcode", barcode));
		List<Product> list = criteria.list();
		if (!list.isEmpty()) {
			Product p = list.get(0);
			p.setName(name);
			update(p);
		}
	}
}
