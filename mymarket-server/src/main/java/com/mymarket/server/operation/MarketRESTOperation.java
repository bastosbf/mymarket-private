package com.mymarket.server.operation;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mymarket.server.HibernateConfig;
import com.mymarket.server.dao.impl.MarketDAO;
import com.mymarket.server.model.Market;

@Path("/market")
public class MarketRESTOperation {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/list/{place}")
	public List<Market> list(@PathParam("place") int place) {
		MarketDAO dao = new MarketDAO(HibernateConfig.factory);
		return dao.list(place);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/get/{market}")
	public Market get(@PathParam("market") int market) {
		MarketDAO dao = new MarketDAO(HibernateConfig.factory);
		return dao.get(market);
	}

}
