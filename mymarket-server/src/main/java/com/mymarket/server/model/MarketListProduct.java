package com.mymarket.server.model;

import java.io.Serializable;

public class MarketListProduct implements Serializable {
	private MarketList list;
	private Product product;
	private Integer quantity;

	public MarketList getList() {
		return list;
	}

	public void setList(MarketList list) {
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
