package com.hongqiang.shop.modules.product.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Attribute;

@Repository
public class AttributeDaoImpl extends BaseDaoImpl<Attribute> implements
		AttributeDaoCustom {

	@Override
	public Page<Attribute> findPage(Pageable pageable) {
		Page<Attribute> attributePage = new Page<Attribute>(
				pageable.getPageNumber(), pageable.getPageSize());
		String qlString = "select attribute from Attribute attribute";
		List<Object> parameter = new ArrayList<Object>();
		return super.findPage(attributePage,  qlString,  parameter, pageable) ;
	}
}