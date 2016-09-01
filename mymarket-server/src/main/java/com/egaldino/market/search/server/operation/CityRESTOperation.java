package com.egaldino.market.search.server.operation;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.egaldino.market.search.server.HibernateConfig;
import com.egaldino.market.search.server.dao.CityDAO;
import com.egaldino.market.search.server.model.City;

@Path("/city")
public class CityRESTOperation {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/list")
	public List<City> list() {
		CityDAO dao = new CityDAO(HibernateConfig.factory);
		return dao.list();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/get/{city}")
	public City get(@PathParam("city") int city) {
		CityDAO dao = new CityDAO(HibernateConfig.factory);
		return dao.get(city);
	}

}
