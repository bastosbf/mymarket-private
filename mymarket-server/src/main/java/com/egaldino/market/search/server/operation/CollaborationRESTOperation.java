package com.egaldino.market.search.server.operation;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.egaldino.market.search.server.HibernateConfig;
import com.egaldino.market.search.server.dao.MarketDAO;
import com.egaldino.market.search.server.dao.MarketProductDAO;
import com.egaldino.market.search.server.dao.MarketSuggestionDAO;
import com.egaldino.market.search.server.dao.ProductDAO;
import com.egaldino.market.search.server.model.Market;
import com.egaldino.market.search.server.model.MarketProduct;
import com.egaldino.market.search.server.model.MarketSuggestion;
import com.egaldino.market.search.server.model.Product;

@Path("/collaboration")
public class CollaborationRESTOperation {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/suggest-market")
	public void suggestMarket(@QueryParam("name") String name, @QueryParam("place") String place, @QueryParam("city") String city) {
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
			Transport.send(msn);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/suggest-product")
	public void suggestProduct(@QueryParam("market") int market, @QueryParam("barcode") String barcode, @QueryParam("name") String name, @QueryParam("price") double price) {
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
	@Path("/suggest-price")
	public void suggestPrice(@QueryParam("market") int market, @QueryParam("product") String product, @QueryParam("price") double price) {
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
	@Path("/suggest-name")
	public void suggestName(@QueryParam("product") String product, @QueryParam("name") String name) {
		Product p = null;
		{
			ProductDAO dao = new ProductDAO(HibernateConfig.factory);
			p = dao.get(product);
			if (p != null) {
				dao.updateName(product, name.toUpperCase());
			}
		}
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/confirm-price")
	public void confirmPrice(@QueryParam("market") int market, @QueryParam("barcode") String product) {
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