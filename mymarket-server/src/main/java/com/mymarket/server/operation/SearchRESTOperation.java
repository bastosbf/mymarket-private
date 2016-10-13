package com.mymarket.server.operation;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.mymarket.server.HibernateConfig;
import com.mymarket.server.dao.impl.MarketProductDAO;
import com.mymarket.server.dao.impl.ProductBarcodeDAO;
import com.mymarket.server.dto.Search;
import com.mymarket.server.dto.model.MarketProduct;
import com.mymarket.server.dto.model.ProductBarcode;

@Path("/search")
public class SearchRESTOperation {

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
			ProductBarcodeDAO productBarcodeDAO = new ProductBarcodeDAO(HibernateConfig.factory);
			ProductBarcode productBarcode = productBarcodeDAO.get(barcode);
			if (productBarcode != null) {
				Search productSearch = new Search();
				productSearch.setProduct(productBarcode.getProduct());

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
			price.setOffer(result.getOffer());
			price.setLastUpdate(result.getLastUpdate());

			searchList.add(price);
		}
		if (results.isEmpty()) {
			// busca produto por barcode
			ProductBarcodeDAO productBarcodeDAO = new ProductBarcodeDAO(HibernateConfig.factory);
			ProductBarcode productBarcode = productBarcodeDAO.get(barcode);
			if (productBarcode != null) {
				Search productSearch = new Search();
				productSearch.setProduct(productBarcode.getProduct());

				searchList.add(productSearch);
			}
		}

		/**
		 * TODO: Soluçao temporaria para armazenar consultas
		 */
		Session session = HibernateConfig.factory.openSession();
		try {
			SQLQuery query = session.createSQLQuery("INSERT INTO search_accounting (product_barcode) VALUES ('" + barcode + "')");
			query.executeUpdate();
		} finally {
			session.flush();
			session.close();
		}

		return searchList;
	}

}
