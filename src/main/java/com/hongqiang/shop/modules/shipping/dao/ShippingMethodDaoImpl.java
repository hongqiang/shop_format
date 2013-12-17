package com.hongqiang.shop.modules.shipping.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.ShippingMethod;

@Repository
public class ShippingMethodDaoImpl extends BaseDaoImpl<ShippingMethod>
		implements ShippingMethodDaoCustom {

	@Override
	public Page<ShippingMethod> findPage(Pageable pageable) {
		Page<ShippingMethod> shippingMethodPage = new Page<ShippingMethod>(
				pageable.getPageNumber(), pageable.getPageSize());
		String qlString = "select shippingMethod from ShippingMethod shippingMethod where 1=1 ";
		List<Object> parameter = new ArrayList<Object>();
		return super
				.findPage(shippingMethodPage, qlString, parameter, pageable);
	}

	// @Override
	// public Long count(){
	// String qlString =
	// "select shippingMethod from ShippingMethod shippingMethod where 1=1 ";
	// List<Object> parameter = new ArrayList<Object>();
	// StringBuilder sBuilder = new StringBuilder(sqlString);
	// return super.count(sBuilder, null, parameter);
	// }
}