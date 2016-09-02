package com.mymarket.server.operation;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mymarket.server.HibernateConfig;
import com.mymarket.server.dao.PlaceDAO;
import com.mymarket.server.model.Place;

@Path("/place")
public class PlaceRESTOperation {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/list/{city}")
	public List<Place> list(@PathParam("city") int city) {
		PlaceDAO dao = new PlaceDAO(HibernateConfig.factory);
		return dao.list(city);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/get/{place}")
	public Place get(@PathParam("place") int place) {
		PlaceDAO dao = new PlaceDAO(HibernateConfig.factory);
		return dao.get(place);
	}

}
