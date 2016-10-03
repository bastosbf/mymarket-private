package com.mymarket.server.dto;

import javax.xml.bind.annotation.XmlRootElement;

import com.mymarket.server.dto.model.Product;

@XmlRootElement
public class ProductWithOfferAndPrice extends Product {

	public ProductWithOfferAndPrice() {}

	public ProductWithOfferAndPrice(Product product) {
		setId(product.getId());
		setName(product.getName());
	}

	private Float price;
	private Boolean offer;

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Boolean getOffer() {
		return offer;
	}

	public void setOffer(Boolean offer) {
		this.offer = offer;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProductWithOfferAndPrice) {
			return ((ProductWithOfferAndPrice) obj).getId().equals(getId());
		}
		return false;
	}

}
