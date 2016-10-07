package com.mymarket.server.operation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mymarket.server.HibernateConfig;
import com.mymarket.server.dao.impl.MarketDAO;
import com.mymarket.server.dao.impl.MarketProductDAO;
import com.mymarket.server.dao.impl.MarketSuggestionDAO;
import com.mymarket.server.dao.impl.ProductDAO;
import com.mymarket.server.dao.impl.ProductNameSuggestionDAO;
import com.mymarket.server.dao.impl.ProductSuggestionDAO;
import com.mymarket.server.dto.model.Market;
import com.mymarket.server.dto.model.MarketSuggestion;
import com.mymarket.server.dto.model.Product;
import com.mymarket.server.dto.model.ProductNameSuggestion;
import com.mymarket.server.dto.model.ProductSuggestion;

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
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/suggest-product/{market}/{barcode}/{name}/{price}/{offer}")
	public void suggestProduct(@PathParam("market") int market, @PathParam("barcode") String barcode, @PathParam("name") String name, @PathParam("price") float price, @PathParam("offer") boolean offer) {
		Market m = null;
		{
			MarketDAO dao = new MarketDAO(HibernateConfig.factory);
			m = dao.get(market);
		}
		ProductSuggestion suggestion = new ProductSuggestion();
		suggestion.setMarket(m);
		suggestion.setBarcode(barcode);
		suggestion.setName(name);
		if (price != 0) {
			suggestion.setPrice(price);			
		}
		suggestion.setOffer(offer);
		ProductSuggestionDAO dao = new ProductSuggestionDAO(HibernateConfig.factory);
		dao.add(suggestion);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/suggest-name/{product}/{name}")
	public void suggestName(@PathParam("product") int product, @PathParam("name") String name) {
		Product p = null;
		{
			ProductDAO dao = new ProductDAO(HibernateConfig.factory);
			p = dao.get(product);
		}
		if (p != null) {
			ProductNameSuggestion suggestion = new ProductNameSuggestion();
			suggestion.setSuggestedName(name);
			suggestion.setProduct(p);

			ProductNameSuggestionDAO dao = new ProductNameSuggestionDAO(HibernateConfig.factory);
			dao.add(suggestion);
		}
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/suggest-price/{market}/{product}/{price}/{offer}")
	public void suggestPrice(@PathParam("market") int market, @PathParam("product") int product, @PathParam("price") float price, @PathParam("offer") boolean offer) {
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
				dao.updatePrice(market, product, price, offer);
			}
		}
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/confirm-price/{market}/{product}")
	public void confirmPrice(@PathParam("market") int market, @PathParam("product") int product) {
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
				dao.confirmPrice(market, product);
			}
		}
	}

}