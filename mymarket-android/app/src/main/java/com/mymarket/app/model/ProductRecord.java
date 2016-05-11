package com.mymarket.app.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by bastosbf on 12/8/15.
 */
public class ProductRecord implements Serializable {

    private String name;
    private String barcode;
    private Market market;
    private String price;
    private Date lastUpdate;

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return name;
    }
}
