package com.mymarket.server.model.dto;

import javax.xml.bind.annotation.XmlRootElement;

import com.mymarket.server.model.Product;

@XmlRootElement
public class ProductWithPrice extends Product {

	public ProductWithPrice() {}

	public ProductWithPrice(Product product) {
		setId(product.getId());
		setName(product.getName());
	}

	private Float price;

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProductWithPrice) {
			return ((ProductWithPrice) obj).getId().equals(getId());
		}
		return false;
	}

}
