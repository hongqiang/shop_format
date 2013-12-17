package com.hongqiang.shop.modules.shipping.dao;

import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Shipping;

public interface ShippingDao extends ShippingDaoCustom, CrudRepository<Shipping, Long> {
	public Shipping findBySn(String snString);
}

/**
 * DAO自定义接口
 * 
 * @author Jack
 * 
 */
interface ShippingDaoCustom extends BaseDao<Shipping> {
  public Page<Shipping>  findPage(Pageable pageable);
}
