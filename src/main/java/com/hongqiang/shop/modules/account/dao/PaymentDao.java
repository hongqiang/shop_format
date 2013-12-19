package com.hongqiang.shop.modules.account.dao;

import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Payment;

public interface PaymentDao extends PaymentDaoCustom, CrudRepository<Payment, Long> {
	public Payment findBySn(String snString);
}

/**
 * DAO自定义接口
 * 
 * @author Jack
 * 
 */
interface PaymentDaoCustom extends BaseDao<Payment> {
  public Page<Payment>  findPage(Pageable pageable);
}