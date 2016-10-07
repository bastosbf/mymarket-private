package com.mymarket.server.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.mymarket.server.dao.GenericDAO;
import com.mymarket.server.dto.model.ProductBarcode;

public class ProductBarcodeDAO extends GenericDAO<ProductBarcode> {

	public ProductBarcodeDAO(SessionFactory factory) {
		super(factory);
	}
	
	public ProductBarcode get(String barcode) {
		Session session = factory.openSession();
		try {
			session.beginTransaction();
			Criteria criteria = session.createCriteria(ProductBarcode.class).add(Restrictions.eq("barcode", barcode));
			List<ProductBarcode> list = criteria.list();
			if (!list.isEmpty()) {
				return list.get(0);
			}
			return null;
		} finally {
			session.close();
		}
	}
}
