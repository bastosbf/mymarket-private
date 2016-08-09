package com.egaldino.market.search.server.operation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.egaldino.market.search.server.HibernateConfig;
import com.egaldino.market.search.server.dao.ProductDAO;
import com.egaldino.market.search.server.model.Product;

@Path("/product")
public class ProductRESTOperation {
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/get")
	public Product get(@QueryParam("barcode") String barcode) {
		ProductDAO dao = new ProductDAO(HibernateConfig.factory);
		return dao.get(barcode);
	}
}
