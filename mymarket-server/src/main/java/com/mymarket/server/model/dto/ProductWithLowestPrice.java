package com.mymarket.server.model.dto;

import javax.xml.bind.annotation.XmlRootElement;

import com.mymarket.server.model.Product;

@XmlRootElement
public class ProductWithLowestPrice extends Product {

	public ProductWithLowestPrice() {}

	public ProductWithLowestPrice(Product product) {
		setId(product.getId());
		setName(product.getName());
	}

	private Float lowestPrice;

	public Float getLowestPrice() {
		return lowestPrice;
	}

	public void setLowestPrice(Float lowestPrice) {
		this.lowestPrice = lowestPrice;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProductWithLowestPrice) {
			return ((ProductWithLowestPrice) obj).getId().equals(getId());
		}
		return false;
	}

}
