package com.mymarket.server.dto.model;

import java.io.Serializable;

public class ShoppingListProduct implements Serializable {
	private ShoppingtList list;
	private Product product;
	private Integer quantity;

	public ShoppingtList getList() {
		return list;
	}

	public void setList(ShoppingtList list) {
		this.list = list;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
