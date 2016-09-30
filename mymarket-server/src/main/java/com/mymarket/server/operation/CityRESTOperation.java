package com.mymarket.server.operation;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mymarket.server.HibernateConfig;
import com.mymarket.server.dao.impl.CityDAO;
import com.mymarket.server.dto.model.City;

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
	@Path("/list/{state}")
	public List<City> list(@PathParam("state") int state) {
		CityDAO dao = new CityDAO(HibernateConfig.factory);
		return dao.list(state);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/get/{city}")
	public City get(@PathParam("city") int city) {
		CityDAO dao = new CityDAO(HibernateConfig.factory);
		return dao.get(city);
	}

}
