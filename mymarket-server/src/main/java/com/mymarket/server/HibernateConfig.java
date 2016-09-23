package com.mymarket.server;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.mymarket.server.model.City;
import com.mymarket.server.model.Market;
import com.mymarket.server.model.MarketList;
import com.mymarket.server.model.MarketListProduct;
import com.mymarket.server.model.MarketProduct;
import com.mymarket.server.model.MarketSuggestion;
import com.mymarket.server.model.Notification;
import com.mymarket.server.model.Place;
import com.mymarket.server.model.Product;
import com.mymarket.server.model.ProductBarcode;
import com.mymarket.server.model.ProductNameSuggestion;
import com.mymarket.server.model.Score;
import com.mymarket.server.model.State;
import com.mymarket.server.model.User;

public class HibernateConfig {

	public static final SessionFactory factory = new Configuration()
			.configure()
			.addClass(City.class)
			.addClass(Market.class)
			.addClass(MarketList.class)
			.addClass(MarketListProduct.class)
			.addClass(MarketProduct.class)
			.addClass(MarketSuggestion.class)
			.addClass(Notification.class)
			.addClass(Place.class)
			.addClass(Product.class)
			.addClass(ProductBarcode.class)
			.addClass(ProductNameSuggestion.class)
			.addClass(Score.class)
			.addClass(State.class)
			.addClass(User.class)			
			.buildSessionFactory();

	public static void main(String[] args) throws Exception {

	}
}
