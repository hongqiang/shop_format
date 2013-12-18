package com.hongqiang.shop.modules.account.dao;

import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Coupon;

public interface CouponDao extends CouponDaoCustom,
		CrudRepository<Coupon, Long> {

}

/**
 * DAO自定义接口
 * 
 * @author Jack
 * 
 */
interface CouponDaoCustom extends BaseDao<Coupon> {

	public Page<Coupon> findPage(Boolean isEnabled, Boolean isExchange,
			Boolean hasExpired, Pageable pageable);

	public Page<Coupon> findPage(Pageable pageable);
}