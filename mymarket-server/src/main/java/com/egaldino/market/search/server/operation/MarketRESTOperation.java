package com.egaldino.market.search.server.operation;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.egaldino.market.search.server.HibernateConfig;
import com.egaldino.market.search.server.dao.MarketDAO;
import com.egaldino.market.search.server.model.Market;

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
