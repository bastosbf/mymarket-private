package com.mymarket.server;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.mymarket.server.dto.model.City;
import com.mymarket.server.dto.model.Market;
import com.mymarket.server.dto.model.MarketProduct;
import com.mymarket.server.dto.model.MarketSuggestion;
import com.mymarket.server.dto.model.Notification;
import com.mymarket.server.dto.model.Place;
import com.mymarket.server.dto.model.Product;
import com.mymarket.server.dto.model.ProductBarcode;
import com.mymarket.server.dto.model.ProductNameSuggestion;
import com.mymarket.server.dto.model.ProductSuggestion;
import com.mymarket.server.dto.model.Score;
import com.mymarket.server.dto.model.ShoppingList;
import com.mymarket.server.dto.model.ShoppingListProduct;
import com.mymarket.server.dto.model.State;
import com.mymarket.server.dto.model.User;

public class HibernateConfig {

	public static final SessionFactory factory = new Configuration()
			.configure()
			.addClass(City.class)
			.addClass(Market.class)
			.addClass(ShoppingList.class)
			.addClass(ShoppingListProduct.class)
			.addClass(MarketProduct.class)
			.addClass(MarketSuggestion.class)
			.addClass(Notification.class)
			.addClass(Place.class)
			.addClass(Product.class)
			.addClass(ProductBarcode.class)
			.addClass(ProductNameSuggestion.class)
			.addClass(ProductSuggestion.class)
			.addClass(Score.class)
			.addClass(State.class)
			.addClass(User.class)			
			.buildSessionFactory();

	public static void main(String[] args) throws Exception {

	}
}
