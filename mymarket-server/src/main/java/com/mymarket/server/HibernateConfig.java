package com.mymarket.server;

import java.util.TimeZone;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.mymarket.server.model.City;
import com.mymarket.server.model.Market;
import com.mymarket.server.model.MarketProduct;
import com.mymarket.server.model.MarketSuggestion;
import com.mymarket.server.model.Place;
import com.mymarket.server.model.Product;
import com.mymarket.server.model.ProductNameSuggestion;

public class HibernateConfig {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		System.setProperty("user.timezone", "UTC");

	}

	public static final SessionFactory factory = new Configuration().configure().addClass(Market.class).addClass(City.class).addClass(Place.class).addClass(Product.class).addClass(MarketProduct.class).addClass(MarketSuggestion.class).addClass(ProductNameSuggestion.class).buildSessionFactory();

	public static void main(String[] args) throws Exception {

	}
}
