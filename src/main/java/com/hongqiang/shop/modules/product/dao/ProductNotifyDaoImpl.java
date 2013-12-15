package com.hongqiang.shop.modules.product.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.FlushModeType;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Member;
import com.hongqiang.shop.modules.entity.Product;
import com.hongqiang.shop.modules.entity.ProductNotify;

@Repository
public class ProductNotifyDaoImpl extends BaseDaoImpl<ProductNotify> implements
		ProductNotifyDaoCustom {
	public boolean exists(Product product, String email) {
		String str = "select count(*) from ProductNotify productNotify where productNotify.product = :product and lower(productNotify.email) = lower(:email) and productNotify.hasSent = false";
		Long localLong = (Long) this.getEntityManager()
				.createQuery(str, Long.class)
				.setFlushMode(FlushModeType.COMMIT)
				.setParameter("product", product).setParameter("email", email)
				.getSingleResult();
		return localLong.longValue() > 0L;
	}

	public Page<ProductNotify> findPage(Member member, Boolean isMarketable,
			Boolean isOutOfStock, Boolean hasSent, Pageable pageable) {
		String sqlString = "select DISTINCT productNotify from ProductNotify productNotify where 1=1 ";
		List<Object> params = new ArrayList<Object>();

		sqlString=inquerySql(sqlString, params, member, isMarketable, isOutOfStock,
				hasSent);
		Page<ProductNotify> productPage = new Page<ProductNotify>(
				pageable.getPageNumber(), pageable.getPageSize());
		return this.findPage(productPage, sqlString, params.toArray());// 测试这样是否可行，如果不行，需要重写该函数
	}

	public Long count(Member member, Boolean isMarketable,
			Boolean isOutOfStock, Boolean hasSent) {
		String sqlString = "select DISTINCT productNotify from ProductNotify productNotify where 1=1 ";
		List<Object> params = new ArrayList<Object>();

		sqlString=inquerySql(sqlString, params, member, isMarketable, isOutOfStock,
				hasSent);

		List<ProductNotify> listProductNotify = this.find(sqlString,
				params.toArray());// 测试下jeesite的函数能用不
		return (long) listProductNotify.size();
	}

	private String inquerySql(String sqlString, List<Object> params,
			Member member, Boolean isMarketable, Boolean isOutOfStock,
			Boolean hasSent) {

		if (member != null) {
			sqlString += " and productNotify.member= ? ";
			params.add(member);
		}
		if (isMarketable != null) {
			sqlString += " and productNotify.product.isMarketable = ? ";
			params.add(isMarketable);
		}
		if (isOutOfStock != null) {
			if (isOutOfStock.booleanValue()) {
				sqlString += " and (productNotify.product.stock !=null and productNotify.product.stock<= productNotify.product.allocatedStock) ";
			} else {
				sqlString += " and (productNotify.product.stock !=null or productNotify.product.stock> productNotify.product.allocatedStock)";
			}
		}
		if (hasSent != null) {
			sqlString += " and productNotify.hasSent = ?";
			params.add(hasSent);
		}
		return sqlString;
	}
}