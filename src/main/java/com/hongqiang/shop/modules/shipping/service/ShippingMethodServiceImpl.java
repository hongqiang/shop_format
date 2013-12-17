package com.hongqiang.shop.modules.shipping.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.service.BaseService;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.ShippingMethod;
import com.hongqiang.shop.modules.shipping.dao.ShippingMethodDao;

@Service
public class ShippingMethodServiceImpl extends BaseService
  implements ShippingMethodService
{
  @Autowired
  private ShippingMethodDao shippingMethodDao;

   @Transactional
	public ShippingMethod find(Long id) {
		return this.shippingMethodDao.find(id);
	}
   
 @Transactional
   public Page<ShippingMethod> findPage(Pageable pageable){
	   return this.shippingMethodDao.findPage(pageable);
   }
   
    @Transactional
   public Long count(){
	return this.shippingMethodDao.count();
   }
   
   @Transactional
  public void save(ShippingMethod shippingMethod)
  {
	this.shippingMethodDao.persist(shippingMethod);
  }

  @Transactional
  public ShippingMethod update(ShippingMethod shippingMethod)
  {
    return (ShippingMethod)this.shippingMethodDao.merge(shippingMethod);
  }

  @Transactional
  public ShippingMethod update(ShippingMethod shippingMethod, String[] ignoreProperties)
  {
    return (ShippingMethod)this.shippingMethodDao.update(shippingMethod, ignoreProperties);
  }

  @Transactional
  public void delete(Long id)
  {

    this.shippingMethodDao.delete(id);
  }

  @Transactional
  public void delete(Long[] ids)
  {
	  if (ids != null)
			for (Long localSerializable : ids)
				this.shippingMethodDao.delete(localSerializable);
  }

  @Transactional
  public void delete(ShippingMethod shippingMethod)
  {
    this.shippingMethodDao.delete(shippingMethod);
  }
}