package com.hongqiang.shop.modules.shipping.dao;

import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.website.entity.DeliveryTemplate;

public interface DeliveryTemplateDao extends DeliveryTemplateDaoCustom, CrudRepository<DeliveryTemplate, Long> {

}

/**
 * DAO自定义接口
 * 
 * @author Jack
 * 
 */
interface DeliveryTemplateDaoCustom extends BaseDao<DeliveryTemplate> {
  public DeliveryTemplate findDefault();
  
  public Page<DeliveryTemplate> findPage(Pageable pageable);
  
  public void persist(DeliveryTemplate deliveryTemplate);
  
  public DeliveryTemplate merge(DeliveryTemplate deliveryTemplate);
}

