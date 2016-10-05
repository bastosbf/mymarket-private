package com.mymarket.server.dto.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ShoppingListProduct implements Serializable {
	private ShoppingList list;
	private Product product;
	private Integer quantity;

	public ShoppingList getList() {
		return list;
	}

	public void setList(ShoppingList list) {
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
