package com.hongqiang.shop.modules.account.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.service.BaseService;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.account.dao.CouponDao;
import com.hongqiang.shop.modules.entity.Coupon;

@Service
public class CouponServiceImpl extends BaseService implements CouponService {

	@Autowired
	private CouponDao couponDao;

	@Transactional(readOnly = true)
	public Page<Coupon> findPage(Boolean isEnabled, Boolean isExchange,
			Boolean hasExpired, Pageable pageable) {
		return this.couponDao.findPage(isEnabled, isExchange, hasExpired,
				pageable);
	}

	@Transactional
	public Coupon find(Long id) {
		return this.couponDao.find(id);
	}

	@Transactional
	public Page<Coupon> findPage(Pageable pageable) {
		return this.couponDao.findPage(pageable);
	}

	@Transactional
	public void save(Coupon coupon) {
		this.couponDao.persist(coupon);
	}

	@Transactional
	public Coupon update(Coupon coupon) {
		return (Coupon) this.couponDao.merge(coupon);
	}

	@Transactional
	public Coupon update(Coupon coupon, String[] ignoreProperties) {
		return (Coupon) this.couponDao.update(coupon, ignoreProperties);
	}

	@Transactional
	public void delete(Long id) {

		this.couponDao.delete(id);
	}

	@Transactional
	public void delete(Long[] ids) {
		if (ids != null)
			for (Long localSerializable : ids)
				this.couponDao.delete(localSerializable);
	}

	@Transactional
	public void delete(Coupon coupon) {
		this.couponDao.delete(coupon);
	}
}