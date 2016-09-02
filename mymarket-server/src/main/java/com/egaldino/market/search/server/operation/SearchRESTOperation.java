package com.egaldino.market.search.server.operation;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.egaldino.market.search.server.HibernateConfig;
import com.egaldino.market.search.server.dao.MarketProductDAO;
import com.egaldino.market.search.server.dao.ProductDAO;
import com.egaldino.market.search.server.model.MarketProduct;
import com.egaldino.market.search.server.model.Product;
import com.egaldino.market.search.server.to.Search;

@Path("/search")
public class SearchRESTOperation {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/products/{market}")
	public List<Search> products(@PathParam("market") int market) {
		MarketProductDAO dao = new MarketProductDAO(HibernateConfig.factory);
		List<MarketProduct> results = dao.getByMarket(market);
		List<Search> prices = new ArrayList<Search>();
		for (MarketProduct result : results) {
			Search price = new Search();
			price.setProduct(result.getProduct());
			price.setMarket(result.getMarket());
			price.setPrice(result.getPrice());
			price.setLastUpdate(result.getLastUpdate());

			prices.add(price);
		}
		return prices;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/prices-by-place/{barcode}/{place}")
	public List<Search> pricesByPlace(@PathParam("barcode") String barcode, @PathParam("place") int place) {
		MarketProductDAO dao = new MarketProductDAO(HibernateConfig.factory);
		List<MarketProduct> results = dao.getByBarcodeAndPlace(barcode, place);
		List<Search> searchList = new ArrayList<Search>();
		for (MarketProduct result : results) {
			Search price = new Search();
			price.setProduct(result.getProduct());
			price.setMarket(result.getMarket());
			price.setPrice(result.getPrice());
			price.setLastUpdate(result.getLastUpdate());

			searchList.add(price);
		}
		if (results.isEmpty()) {
			// busca produto por barcode
			ProductDAO productDAO = new ProductDAO(HibernateConfig.factory);
			Product product = productDAO.get(barcode);
			if (product != null) {
				Search productSearch = new Search();
				productSearch.setProduct(product);

				searchList.add(productSearch);
			}
		}

		return searchList;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/prices-by-city/{barcode}/{city}")
	public List<Search> pricesByCity(@PathParam("barcode") String barcode, @PathParam("city") int city) {
		MarketProductDAO dao = new MarketProductDAO(HibernateConfig.factory);
		List<MarketProduct> results = dao.getByBarcodeAndCity(barcode, city);
		List<Search> searchList = new ArrayList<Search>();
		for (MarketProduct result : results) {
			Search price = new Search();
			price.setProduct(result.getProduct());
			price.setMarket(result.getMarket());
			price.setPrice(result.getPrice());
			price.setLastUpdate(result.getLastUpdate());

			searchList.add(price);
		}
		if (results.isEmpty()) {
			// busca produto por barcode
			ProductDAO productDAO = new ProductDAO(HibernateConfig.factory);
			Product product = productDAO.get(barcode);
			if (product != null) {
				Search productSearch = new Search();
				productSearch.setProduct(product);

				searchList.add(productSearch);
			}
		}

		/**
		 * TODO: Soluçao temporaria para armazenar consultas
		 */
		Session session = HibernateConfig.factory.openSession();
		try {
			SQLQuery query = session.createSQLQuery("INSERT INTO search_accounting (barcode, date) VALUES ('" + barcode + "', now())");
			query.executeUpdate();
		} finally {
			session.flush();
			session.close();
		}

		return searchList;
	}

}
