package com.hongqiang.shop.modules.shipping.dao;

import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.ShippingMethod;

public interface ShippingMethodDao extends ShippingMethodDaoCustom, CrudRepository<ShippingMethod, Long> {
	
}

/**
 * DAO自定义接口
 * 
 * @author Jack
 * 
 */
interface ShippingMethodDaoCustom extends BaseDao<ShippingMethod> {

	public Page<ShippingMethod>  findPage(Pageable pageable);
	
	//调用CrudRepository自带的返回数目的函数。
//	public Long count();
}
