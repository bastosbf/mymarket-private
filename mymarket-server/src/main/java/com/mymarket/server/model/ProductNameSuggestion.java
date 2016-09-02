package com.mymarket.server.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ProductNameSuggestion implements Serializable {
	private Integer id;
	private String currentName;
	private String suggestedName;
	private String barcode;
	private Date date;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCurrentName() {
		return currentName;
	}

	public void setCurrentName(String currentName) {
		this.currentName = currentName;
	}

	public String getSuggestedName() {
		return suggestedName;
	}

	public void setSuggestedName(String suggestedName) {
		this.suggestedName = suggestedName;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
}
