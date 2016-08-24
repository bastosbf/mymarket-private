package com.egaldino.market.search.server;

import java.util.TimeZone;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.egaldino.market.search.server.model.City;
import com.egaldino.market.search.server.model.Market;
import com.egaldino.market.search.server.model.MarketProduct;
import com.egaldino.market.search.server.model.MarketSuggestion;
import com.egaldino.market.search.server.model.Place;
import com.egaldino.market.search.server.model.Product;
import com.egaldino.market.search.server.model.ProductNameSuggestion;

public class HibernateConfig {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		System.setProperty("user.timezone", "UTC");

	}

	public static final SessionFactory factory = new Configuration().configure().addClass(Market.class).addClass(City.class).addClass(Place.class).addClass(Product.class).addClass(MarketProduct.class).addClass(MarketSuggestion.class).addClass(ProductNameSuggestion.class).buildSessionFactory();

	public static void main(String[] args) throws Exception {

	}
}
