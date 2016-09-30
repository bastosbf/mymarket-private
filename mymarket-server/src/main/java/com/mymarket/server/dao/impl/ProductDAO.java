package com.mymarket.server.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.dto.ProductWithLowestPrice;
import com.mymarket.server.dto.ProductWithPrice;
import com.mymarket.server.dto.model.MarketProduct;
import com.mymarket.server.dto.model.Product;

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
			criteria.addOrder(Order.asc("name"));
			List<Product> products = criteria.list();
			return products;
		} finally {
			session.close();
		}
	}
	
	public List<ProductWithLowestPrice> listWithLowestPriceByName(String[] tokens, int city, int place) {
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
					return new ArrayList<ProductWithLowestPrice>();
				}
				criteria = criteria.add(Restrictions.like("p.name", token.toUpperCase(), MatchMode.ANYWHERE));
			}			
			criteria.addOrder(Order.asc("price"));
			List<MarketProduct> list = criteria.list();
			//will only allow a single product
			Set<ProductWithLowestPrice> products = new LinkedHashSet<ProductWithLowestPrice>();
			for (MarketProduct mp : list) {
				Product product = mp.getProduct();
				ProductWithLowestPrice enhancedProduct = new ProductWithLowestPrice(product);
				enhancedProduct.setLowestPrice(mp.getPrice());
				products.add(enhancedProduct);
			}
			return new ArrayList<ProductWithLowestPrice>(products);
		} finally {
			session.close();
		}
	}
	
	public List<ProductWithPrice> getWithPrice(int market, Integer... products) {
		Session session = factory.openSession();
		try {
			session.beginTransaction();
			Criteria criteria = session.createCriteria(MarketProduct.class)
					.createAlias("product", "p")
					.createAlias("market", "m")			
				  .add(Restrictions.eq("m.id", market))
				  .add(Restrictions.in("p.id", Arrays.asList(products)));
			
			List<MarketProduct> list = criteria.list();
			List<ProductWithPrice> productList = new ArrayList<ProductWithPrice>();
			for (MarketProduct mp : list) {
				Product product = mp.getProduct();
				ProductWithPrice enhancedProduct = new ProductWithPrice(product);
				enhancedProduct.setPrice(mp.getPrice());
				productList.add(enhancedProduct);
			}
			return productList;
		} finally {
			session.close();
		}
	}

	public Product get(int id) {
		Session session = factory.openSession();
		try {
			session.beginTransaction();
			Criteria criteria = session.createCriteria(Product.class).add(Restrictions.eq("id", id));
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

	public void updateName(int id, String name) {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(Product.class).add(Restrictions.eq("id", id));
		List<Product> list = criteria.list();
		if (!list.isEmpty()) {
			Product p = list.get(0);
			p.setName(name);
			update(p);
		}
	}
}
