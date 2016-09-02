package com.mymarket.server.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EnhancedProduct extends Product {

	public EnhancedProduct() {}

	public EnhancedProduct(Product product) {
		setBarcode(product.getBarcode());
		setName(product.getName());
	}

	private Double lowestPrice;

	public Double getLowestPrice() {
		return lowestPrice;
	}

	public void setLowestPrice(Double lowestPrice) {
		this.lowestPrice = lowestPrice;
	}

	@Override
	public int hashCode() {
		return getBarcode().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EnhancedProduct) {
			return ((EnhancedProduct) obj).getBarcode().equals(getBarcode());
		}
		return false;
	}

}
