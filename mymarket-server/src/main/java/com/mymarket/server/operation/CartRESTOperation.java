package com.mymarket.server.operation;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mymarket.server.HibernateConfig;
import com.mymarket.server.dao.impl.MarketListDAO;
import com.mymarket.server.dao.impl.MarketListProductDAO;
import com.mymarket.server.model.MarketList;
import com.mymarket.server.model.MarketListProduct;
import com.mymarket.server.model.Product;
import com.mymarket.server.model.User;

@Path("/cart")
public class CartRESTOperation {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/save-shopping-list/{uid}/{name}/{list: .*}")
	public void saveShoppingList(@PathParam("uid") String uid, @PathParam("name") String name, @PathParam("list") String list) {
		MarketList ml = new MarketList();
		{
			User user = new User();
			user.setUid(uid);
			ml.setUser(user);
			ml.setName(name);
			MarketListDAO dao = new MarketListDAO(HibernateConfig.factory);
			dao.add(ml);
		}
		MarketListProductDAO dao = new MarketListProductDAO(HibernateConfig.factory);
		String[] tokens = list.split("/");
		for (String token : tokens) {
			String[] data = token.split(":");
			int product = Integer.parseInt(data[0]);
			int quantity = Integer.parseInt(data[1]);

			MarketListProduct mlp = new MarketListProduct();
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
	@Path("/list-shopping-lists/{uid}")
	public List<MarketList> listShoppingLists(@PathParam("uid") String uid) {
		MarketListDAO dao = new MarketListDAO(HibernateConfig.factory);
		return dao.list(uid);
	}
}
