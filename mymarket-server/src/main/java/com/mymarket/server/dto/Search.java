package com.mymarket.server.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.mymarket.server.dto.model.Market;
import com.mymarket.server.dto.model.Product;

@XmlRootElement
public class Search {
	private Product product;
	private Market market;
	private Float price;
	private Boolean offer;
	private Date lastUpdate;

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Market getMarket() {
		return market;
	}

	public void setMarket(Market market) {
		this.market = market;
	}

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

	@XmlElement(name = "last_update")
	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

}
