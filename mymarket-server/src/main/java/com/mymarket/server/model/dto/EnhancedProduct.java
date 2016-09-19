package com.mymarket.server.model.dto;

import javax.xml.bind.annotation.XmlRootElement;

import com.mymarket.server.model.Product;

@XmlRootElement
public class EnhancedProduct extends Product {

	public EnhancedProduct() {}

	public EnhancedProduct(Product product) {
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
		if (obj instanceof EnhancedProduct) {
			return ((EnhancedProduct) obj).getId().equals(getId());
		}
		return false;
	}

}
