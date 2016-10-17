package com.mymarket.server.operation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

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
		
		/**
		 * TODO: Soluçao temporaria para armazenar logins
		 */
		Session session = HibernateConfig.factory.openSession();
		try {
			SQLQuery query = session.createSQLQuery("INSERT INTO login_accounting (uid, name, email) VALUES ('" + uid + "','" + name + "','" + email + "')");
			query.executeUpdate();
		} finally {
			session.flush();
			session.close();
		}

	}
	
}
