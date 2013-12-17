package com.hongqiang.shop.modules.shipping.dao;

import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.DeliveryCenter;

public interface DeliveryCenterDao extends DeliveryCenterDaoCustom, CrudRepository<DeliveryCenter, Long> {
	
}

/**
 * DAO自定义接口
 * 
 * @author Jack
 * 
 */
interface DeliveryCenterDaoCustom extends BaseDao<DeliveryCenter> {

  public DeliveryCenter findDefault();
  
  public Page<DeliveryCenter> findPage(Pageable pageable);
  
  public void persist(DeliveryCenter deliveryCenter);
  
  public DeliveryCenter merge(DeliveryCenter deliveryCenter);
  
  
}