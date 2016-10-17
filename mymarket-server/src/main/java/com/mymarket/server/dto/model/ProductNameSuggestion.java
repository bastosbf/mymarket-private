package com.mymarket.server.dto.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ProductNameSuggestion implements Serializable {
	private Integer id;
	private Product product;
	private String suggestedName;	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getSuggestedName() {
		return suggestedName;
	}

	public void setSuggestedName(String suggestedName) {
		if(suggestedName != null) {
			suggestedName = suggestedName.toUpperCase();
		}
		this.suggestedName = suggestedName;
	}
}
