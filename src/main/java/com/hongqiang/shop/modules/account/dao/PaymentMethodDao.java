package com.hongqiang.shop.modules.account.dao;

import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.PaymentMethod;

public interface PaymentMethodDao extends PaymentMethodDaoCustom,
		CrudRepository<PaymentMethod, Long> {

}

/**
 * DAO自定义接口
 * 
 * @author Jack
 * 
 */
interface PaymentMethodDaoCustom extends BaseDao<PaymentMethod> {
	public Page<PaymentMethod> findPage(Pageable pageable);
}