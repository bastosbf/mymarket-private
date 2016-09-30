package com.mymarket.server.operation;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mymarket.server.HibernateConfig;
import com.mymarket.server.dao.impl.NotificationDAO;
import com.mymarket.server.dto.model.Notification;

@Path("/notification")
public class NotificationRESTOperation {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/list")
	public List<Notification> list() {
		NotificationDAO dao = new NotificationDAO(HibernateConfig.factory);
		return dao.list();
	}
}
