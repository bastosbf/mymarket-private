package com.egaldino.market.search.server.operation;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.egaldino.market.search.server.HibernateConfig;
import com.egaldino.market.search.server.dao.MarketDAO;
import com.egaldino.market.search.server.dao.MarketProductDAO;
import com.egaldino.market.search.server.dao.MarketSuggestionDAO;
import com.egaldino.market.search.server.dao.ProductDAO;
import com.egaldino.market.search.server.dao.ProductNameSuggestionDAO;
import com.egaldino.market.search.server.model.Market;
import com.egaldino.market.search.server.model.MarketProduct;
import com.egaldino.market.search.server.model.MarketSuggestion;
import com.egaldino.market.search.server.model.Product;
import com.egaldino.market.search.server.model.ProductNameSuggestion;

@Path("/collaboration")
public class CollaborationRESTOperation {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/suggest-market/{name}/{place}/{city}")
	public void suggestMarket(@PathParam("name") String name, @PathParam("place") String place, @PathParam("city") String city) {
		MarketSuggestion suggestion = new MarketSuggestion();
		suggestion.setName(name);
		suggestion.setPlace(place);
		suggestion.setCity(city);
		MarketSuggestionDAO dao = new MarketSuggestionDAO(HibernateConfig.factory);
		dao.add(suggestion);

		try {
			Properties props = System.getProperties();
			Session session = Session.getDefaultInstance(props);
			Message msn = new MimeMessage(session);
			msn.setFrom(new InternetAddress("eMercado@mail.com"));
			msn.setRecipients(Message.RecipientType.TO, InternetAddress.parse("edson1galdino@gmail.com", false));
			msn.setSubject("Market collaboration");
			msn.setText("Mercado: " + name + "\nLocal: " + place + "\nCidade: " + city);
			msn.setHeader("e-Marcado", "MARKET-SUGGESTION");
			msn.setSentDate(new Date());
			// Transport.send(msn);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/suggest-product/{market}/{barcode}/{name}/{price}")
	public void suggestProduct(@PathParam("market") int market, @PathParam("barcode") String barcode, @PathParam("name") String name, @PathParam("price") double price) {
		Product product = null;
		{
			ProductDAO dao = new ProductDAO(HibernateConfig.factory);
			product = dao.get(barcode);
		}
		if (product == null) {
			{
				product = new Product();
				product.setBarcode(barcode);
				product.setName(name.toUpperCase());

				ProductDAO dao = new ProductDAO(HibernateConfig.factory);
				dao.add(product);
			}
		}

		Market m = null;
		{
			MarketDAO dao = new MarketDAO(HibernateConfig.factory);
			m = dao.get(market);
		}
		if (m != null) {
			MarketProduct mp = new MarketProduct();
			mp.setMarket(m);
			mp.setProduct(product);
			mp.setLastUpdate(new Date());
			mp.setPrice(price);

			MarketProductDAO dao = new MarketProductDAO(HibernateConfig.factory);
			dao.add(mp);
		}
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/suggest-price/{market}/{product}/{price}")
	public void suggestPrice(@PathParam("market") int market, @PathParam("product") String product, @PathParam("price") double price) {
		Market m = null;
		{
			MarketDAO dao = new MarketDAO(HibernateConfig.factory);
			m = dao.get(market);
		}
		if (m != null) {
			Product p = null;
			{
				ProductDAO dao = new ProductDAO(HibernateConfig.factory);
				p = dao.get(product);
			}
			if (p != null) {
				MarketProductDAO dao = new MarketProductDAO(HibernateConfig.factory);
				dao.updatePrice(market, product, price);
			}
		}

	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/suggest-name/{barcode}/{name}")
	public void suggestName(@PathParam("barcode") String barcode, @PathParam("name") String name) {
		ProductNameSuggestion suggestion = new ProductNameSuggestion();
		suggestion.setSuggestedName(name);
		suggestion.setBarcode(barcode);

		ProductDAO productDAO = new ProductDAO(HibernateConfig.factory);
		Product product = productDAO.get(barcode);
		suggestion.setCurrentName(product.getName());
		suggestion.setDate(new Date());

		ProductNameSuggestionDAO dao = new ProductNameSuggestionDAO(HibernateConfig.factory);
		dao.add(suggestion);

		try {
			Properties props = System.getProperties();
			Session session = Session.getDefaultInstance(props);
			Message msn = new MimeMessage(session);
			msn.setFrom(new InternetAddress("eMercado@mail.com"));
			msn.setRecipients(Message.RecipientType.TO, InternetAddress.parse("edson1galdino@gmail.com", false));
			msn.setSubject("Product Name collaboration");
			msn.setText("Barcode: " + barcode + "\nCurrent Name: " + product.getName() + "\nSuggested Name: " + name);
			msn.setHeader("e-Marcado", "PRODUCT-NAME-SUGGESTION");
			msn.setSentDate(new Date());
			// Transport.send(msn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/confirm-price/{market}/{barcode}")
	public void confirmPrice(@PathParam("market") int market, @PathParam("barcode") String product) {
		Market m = null;
		{
			MarketDAO dao = new MarketDAO(HibernateConfig.factory);
			m = dao.get(market);
		}
		if (m != null) {
			Product p = null;
			{
				ProductDAO dao = new ProductDAO(HibernateConfig.factory);
				p = dao.get(product);
			}
			if (p != null) {
				MarketProductDAO dao = new MarketProductDAO(HibernateConfig.factory);
				dao.confirmPrice(market, product, new Date());
			}
		}
	}

}