package com.hongqiang.shop.modules.account.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.PaymentMethod;

@Repository
public class PaymentMethodDaoImpl extends BaseDaoImpl<PaymentMethod> implements
		PaymentMethodDaoCustom {
	@Override
	public Page<PaymentMethod> findPage(Pageable pageable) {
		Page<PaymentMethod> paymentMethodPage = new Page<PaymentMethod>(
				pageable.getPageNumber(), pageable.getPageSize());
		String qlString = "select paymentMethod from PaymentMethod paymentMethod where 1=1 ";
		List<Object> parameter = new ArrayList<Object>();
		return super.findPage(paymentMethodPage, qlString, parameter, pageable);
	}
}