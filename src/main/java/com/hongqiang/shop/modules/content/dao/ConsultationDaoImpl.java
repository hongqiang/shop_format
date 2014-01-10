package com.hongqiang.shop.modules.content.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Filter;
import com.hongqiang.shop.common.utils.Order;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Consultation;
import com.hongqiang.shop.modules.entity.Member;
import com.hongqiang.shop.modules.entity.Product;

@Repository
public class ConsultationDaoImpl extends BaseDaoImpl<Consultation> implements
ConsultationDaoCustom {
	@Override
	public List<Consultation> findList(Member member, Product product,
			Boolean isShow, Integer count, List<Filter> filters,
			List<Order> orders) {
		String sqlString = " select consultation from Consultation consultation where 1=1 ";
		List<Object> params = new ArrayList<Object>();
		sqlString += " and consultation.forConsultation != null ";
		if (member != null) {
			sqlString += " and consultation.member = ? ";
			params.add(member);
		}
		if (product != null) {
			sqlString += " and consultation.product = ? ";
			params.add(product);
		}
		if (isShow != null) {
			sqlString += " and consultation.isShow = ? ";
			params.add(isShow);
		}
		return super.findList(sqlString, params, null, count, filters, orders);
	}

	@Override
	public Page<Consultation> findPage(Member member, Product product,
			Boolean isShow, Pageable pageable) {
		String sqlString = "select  consultation from Consultation consultation where 1=1 ";
		List<Object> params = new ArrayList<Object>();
		sqlString += " and consultation.forConsultation != null ";
		if (member != null) {
			sqlString += " and consultation.member = ? ";
			params.add(member);
		}
		if (product != null) {
			sqlString += " and consultation.product = ? ";
			params.add(product);
		}
		if (isShow != null) {
			sqlString += " and consultation.isShow = ? ";
			params.add(isShow);
		}
		Page<Consultation> consultationPage = new Page<Consultation>(
				pageable.getPageNumber(), pageable.getPageSize());
		return super.findPage(consultationPage, sqlString, params, pageable);
	}

	@Override
	public Long count(Member member, Product product, Boolean isShow) {
		String sqlString = "select consultation from Consultation consultation where 1=1 ";
		List<Object> params = new ArrayList<Object>();
		sqlString += " and consultation.forConsultation != null ";
		if (member != null) {
			sqlString += " and consultation.member = ? ";
			params.add(member);
		}
		if (product != null) {
			sqlString += " and consultation.product = ? ";
			params.add(product);
		}
		if (isShow != null) {
			sqlString += " and consultation.isShow = ? ";
			params.add(isShow);
		}
		StringBuilder sBuilder = new StringBuilder(sqlString);
		return super.count(sBuilder, null, params);
	}

	@Override
	public List<Consultation> findList(Integer first, Integer count,
			List<Filter> filters, List<Order> orders) {
		String qlString = "select consultation from Consultation consultation where 1=1 ";
		List<Object> parameter = new ArrayList<Object>();
		return super.findList(qlString, parameter, first, count, filters,
				orders);
	}

	@Override
	public Page<Consultation> findPage(Pageable pageable) {
		Page<Consultation> brandPage = new Page<Consultation>(
				pageable.getPageNumber(), pageable.getPageSize());
		String qlString = "select consultation from Consultation consultation where 1=1 ";
		List<Object> parameter = new ArrayList<Object>();
		return super.findPage(brandPage, qlString, parameter, pageable);
	}
}