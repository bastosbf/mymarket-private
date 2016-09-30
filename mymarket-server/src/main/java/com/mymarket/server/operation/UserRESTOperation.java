package com.mymarket.server.operation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mymarket.server.HibernateConfig;
import com.mymarket.server.dao.impl.UserDAO;
import com.mymarket.server.dto.model.User;

@Path("/user")
public class UserRESTOperation {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/add/{uid}/{name}/{email}")
	public void add(@PathParam("uid") String uid, @PathParam("name") String name, @PathParam("email") String email) {
		User user = new User();
		user.setUid(uid);
		user.setName(name);
		user.setEmail(email);
		
		UserDAO dao = new UserDAO(HibernateConfig.factory);
		if(!dao.exists(uid)) {
			dao.add(user);
		}
	}
	
}
