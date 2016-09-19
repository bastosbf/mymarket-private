package com.mymarket.server.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mymarket.server.HibernateConfig;
import com.mymarket.server.dao.impl.ProductDAO;
import com.mymarket.server.model.Product;
import com.mymarket.server.model.dto.EnhancedProduct;

@Path("/product")
public class ProductRESTOperation {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/get/{product}")
	public Product get(@PathParam("product") int product) {
		ProductDAO dao = new ProductDAO(HibernateConfig.factory);
		return dao.get(product);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/get-products-by-name/{searchString}")
	public List<Product> getProductsByName(@PathParam("searchString") String searchString) {
		List<Product> list = new ArrayList<Product>();
		if (searchString != null && searchString.trim().length() >= 3) {
			String[] tokens = searchString.trim().split(" ");
			ProductDAO dao = new ProductDAO(HibernateConfig.factory);
			return dao.listByName(Arrays.stream(tokens).distinct().toArray(size -> new String[size]));
		}
		return list;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/get-products-with-price-by-name/{searchString}/{city}/{place}")
	public List<EnhancedProduct> getProductsWithPriceByName(@PathParam("searchString") String searchString, @PathParam("city") int city, @PathParam("place") int place) {
		if (searchString != null && searchString.trim().length() >= 3) {
			String[] tokens = searchString.trim().split(" ");
			ProductDAO dao = new ProductDAO(HibernateConfig.factory);
			return dao.listWithPriceByName(Arrays.stream(tokens).distinct().toArray(size -> new String[size]), city, place);
		}
		return new ArrayList<EnhancedProduct>();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/get-products-with-price-by-name/{searchString}/{city}")
	public List<EnhancedProduct> getProductsWithPriceByName(@PathParam("searchString") String searchString, @PathParam("city") int city) {
		return getProductsWithPriceByName(searchString, city, 0);
	}

}
