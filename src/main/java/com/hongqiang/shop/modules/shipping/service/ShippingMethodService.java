package com.hongqiang.shop.modules.shipping.service;

import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.ShippingMethod;

public interface ShippingMethodService {

	public ShippingMethod find(Long id);

	public Page<ShippingMethod> findPage(Pageable pageable);

	public Long count();

	public void save(ShippingMethod shippingMethod);

	public ShippingMethod update(ShippingMethod shippingMethod);

	public ShippingMethod update(ShippingMethod shippingMethod,
			String[] ignoreProperties);

	public void delete(Long id);

	public void delete(Long[] ids);

	public void delete(ShippingMethod shippingMethod);
}