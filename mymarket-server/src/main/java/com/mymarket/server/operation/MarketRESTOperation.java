package com.mymarket.server.operation;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mymarket.server.HibernateConfig;
import com.mymarket.server.dao.impl.MarketDAO;
import com.mymarket.server.dto.model.Market;

@Path("/market")
public class MarketRESTOperation {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/list-by-city/{city}")
	public List<Market> listByCity(@PathParam("city") int city) {
		MarketDAO dao = new MarketDAO(HibernateConfig.factory);
		return dao.listByCity(city);
	}
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/list-by-place/{place}")
	public List<Market> listbyPlace(@PathParam("place") int place) {
		MarketDAO dao = new MarketDAO(HibernateConfig.factory);
		return dao.listByPlace(place);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/get/{market}")
	public Market get(@PathParam("market") int market) {
		MarketDAO dao = new MarketDAO(HibernateConfig.factory);
		return dao.get(market);
	}

}
