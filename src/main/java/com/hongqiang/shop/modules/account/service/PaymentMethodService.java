package com.hongqiang.shop.modules.account.service;

import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.PaymentMethod;

public interface PaymentMethodService {
	public PaymentMethod find(Long id);

	public Page<PaymentMethod> findPage(Pageable pageable);

	public long count();

	public void save(PaymentMethod paymentMethod);

	public PaymentMethod update(PaymentMethod paymentMethod);

	public PaymentMethod update(PaymentMethod paymentMethod,
			String[] ignoreProperties);

	public void delete(Long id);

	public void delete(Long[] ids);

	public void delete(PaymentMethod paymentMethod);
}