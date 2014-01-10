package com.hongqiang.shop.modules.account.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.FlushModeType;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Coupon;
import com.hongqiang.shop.modules.entity.CouponCode;
import com.hongqiang.shop.modules.entity.Member;

@Repository
public class CouponCodeDaoImpl extends BaseDaoImpl<CouponCode> implements
		CouponCodeDaoCustom {
	@Override
	public boolean codeExists(String code) {
		if (code == null)
			return false;
		String str = "select count(*) from CouponCode couponCode where lower(couponCode.code) = lower(:code)";
		Long localLong = (Long) this.getEntityManager()
				.createQuery(str, Long.class)
				.setFlushMode(FlushModeType.COMMIT).setParameter("code", code)
				.getSingleResult();
		return localLong.longValue() > 0L;
	}

	@Override
	public CouponCode build(Coupon coupon, Member member) {
		CouponCode localCouponCode = new CouponCode();
		String str = UUID.randomUUID().toString().toUpperCase();
		localCouponCode.setCode(coupon.getPrefix() + str.substring(0, 8)
				+ str.substring(9, 13) + str.substring(14, 18)
				+ str.substring(19, 23) + str.substring(24));
		localCouponCode.setIsUsed(Boolean.valueOf(false));
		localCouponCode.setCoupon(coupon);
		localCouponCode.setMember(member);
		super.persist(localCouponCode);
		return localCouponCode;
	}

	@Override
	public List<CouponCode> build(Coupon coupon, Member member, Integer count) {
		List<CouponCode> localArrayList = new ArrayList<CouponCode>();
		for (int i = 0; i < count.intValue(); i++) {
			CouponCode localCouponCode = build(coupon, member);
			localArrayList.add(localCouponCode);
			if (i % 20 != 0)
				continue;
			super.flush();
			super.clear();
		}
		return localArrayList;
	}

	@Override
	public Page<CouponCode> findPage(Member member, Pageable pageable) {
		String sqlString = " select couponCode from CouponCode couponCode where 1=1 ";
		List<Object> params = new ArrayList<Object>();
		if (member != null) {
			sqlString += " and couponCode.member = ? ";
			params.add(member);
		}
		Page<CouponCode> couponCodePage = new Page<CouponCode>(
				pageable.getPageNumber(), pageable.getPageSize());
		return super.findPage(couponCodePage, sqlString, params, pageable);
	}

	@Override
	public Page<CouponCode> findPage(Pageable pageable) {
		Page<CouponCode> couponCodePage = new Page<CouponCode>(
				pageable.getPageNumber(), pageable.getPageSize());
		String qlString = "select couponCode from CouponCode couponCode where 1=1 ";
		List<Object> parameter = new ArrayList<Object>();
		return super.findPage(couponCodePage, qlString, parameter, pageable);
	}

	@Override
	public Long count(Coupon coupon, Member member, Boolean hasBegun,
			Boolean hasExpired, Boolean isUsed) {
		String sqlString = " select couponCode from CouponCode couponCode where 1=1 ";
		List<Object> params = new ArrayList<Object>();
		if (coupon != null) {
			sqlString += " and couponCode.coupon = ? ";
			params.add(coupon);
		}
		if (member != null) {
			sqlString += " and couponCode.member = ? ";
			params.add(member);
		}
		Date nowaday = new Date();
		if (hasBegun != null) {
			if (hasBegun.booleanValue()) {
				sqlString += " and (couponCode.coupon.beginDate != null or couponCode.coupon.beginDate <= ?) ";
				params.add(nowaday);
			} else {
				sqlString += " and (couponCode.coupon.beginDate != null and couponCode.coupon.beginDate > ?) ";
				params.add(nowaday);
			}
		}
		if (hasExpired != null) {
			if (hasExpired.booleanValue()) {
				sqlString += " and (couponCode.coupon.endDate != null and couponCode.coupon.beginDate < ?) ";
				params.add(nowaday);
			} else {
				sqlString += " and (couponCode.coupon.endDate != null or couponCode.coupon.beginDate >= ?) ";
				params.add(nowaday);
			}
		}
		if (isUsed != null) {
			sqlString += " and couponCode.isUsed = ? ";
			params.add(isUsed);
		}
		StringBuilder stringBuilder = new StringBuilder(sqlString);
		return super.count(stringBuilder, null, params);
	}
}