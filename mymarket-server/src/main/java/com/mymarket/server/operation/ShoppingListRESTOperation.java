package com.mymarket.server.operation;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mymarket.server.HibernateConfig;
import com.mymarket.server.dao.impl.ShoppingListDAO;
import com.mymarket.server.dao.impl.ShoppingListProductDAO;
import com.mymarket.server.dto.model.Product;
import com.mymarket.server.dto.model.ShoppingListProduct;
import com.mymarket.server.dto.model.ShoppingList;
import com.mymarket.server.dto.model.User;

@Path("/shopping-list")
public class ShoppingListRESTOperation {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/save/{uid}/{name}/{list: .*}")
	public void save(@PathParam("uid") String uid, @PathParam("name") String name, @PathParam("list") String list) {
		ShoppingList ml = new ShoppingList();
		{
			User user = new User();
			user.setUid(uid);
			ml.setUser(user);
			ml.setName(name);
			ShoppingListDAO dao = new ShoppingListDAO(HibernateConfig.factory);
			dao.add(ml);
		}
		ShoppingListProductDAO dao = new ShoppingListProductDAO(HibernateConfig.factory);
		String[] tokens = list.split("/");
		for (String token : tokens) {
			String[] data = token.split(":");
			int product = Integer.parseInt(data[0]);
			int quantity = Integer.parseInt(data[1]);

			ShoppingListProduct mlp = new ShoppingListProduct();
			mlp.setList(ml);
			Product p = new Product();
			p.setId(product);
			mlp.setProduct(p);
			mlp.setQuantity(quantity);

			dao.add(mlp);
		}
	}
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/list/{uid}")
	public List<ShoppingList> list(@PathParam("uid") String uid) {
		ShoppingListDAO dao = new ShoppingListDAO(HibernateConfig.factory);
		return dao.list(uid);
	}
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/list-products/{list}")
	public List<ShoppingListProduct> listProducts(@PathParam("list") String list) {
		ShoppingListProductDAO dao = new ShoppingListProductDAO(HibernateConfig.factory);
		return dao.list(list);
	}
}
