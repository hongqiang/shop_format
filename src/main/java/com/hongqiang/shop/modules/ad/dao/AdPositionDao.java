package com.hongqiang.shop.modules.ad.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Filter;
import com.hongqiang.shop.common.utils.Order;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.website.entity.AdPosition;

public abstract interface AdPositionDao extends AdPositionDaoCustom, CrudRepository<AdPosition, Long> {
}

/**
 * DAO自定义接口
 * 
 * @author Jack
 * 
 */
interface AdPositionDaoCustom extends BaseDao<AdPosition> {

	public Page<AdPosition>  findPage(Pageable pageable);
	
	public  List<AdPosition> findList(Integer first, Integer count, List<Filter> filters, List<Order> orders);
	
	public List<AdPosition> findAll();
}