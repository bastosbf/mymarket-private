package com.mymarket.server.operation;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mymarket.server.HibernateConfig;
import com.mymarket.server.dao.impl.StateDAO;
import com.mymarket.server.dto.model.State;

@Path("/state")
public class StateRESTOperation {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/list")
	public List<State> list() {
		StateDAO dao = new StateDAO(HibernateConfig.factory);
		return dao.list();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/get/{state}")
	public State get(@PathParam("state") int state) {
		StateDAO dao = new StateDAO(HibernateConfig.factory);
		return dao.get(state);
	}

}
