package com.hongqiang.shop.modules.shipping.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Filter;
import com.hongqiang.shop.common.utils.Order;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Member;
import com.hongqiang.shop.modules.entity.OrderItem;
import com.hongqiang.shop.modules.entity.Product;

@Repository
public class OrderDaoImpl extends
		BaseDaoImpl<com.hongqiang.shop.modules.entity.Order> implements
		OrderDaoCustom {

	@Override
	public List<com.hongqiang.shop.modules.entity.Order> findList(
			Member member, Integer count, List<Filter> filters,
			List<Order> orders) {
		if (member == null)
			return Collections.emptyList();
		String qlString = "select o from Order o where 1=1 and o.member = ? ";
		List<Object> parameter = new ArrayList<Object>();
		parameter.add(member);
		return super
				.findList(qlString, parameter, null, count, filters, orders);
	}

	@Override
	public Page<com.hongqiang.shop.modules.entity.Order> findPage(
			Member member, Pageable pageable) {
		if (member == null)
			return new Page<com.hongqiang.shop.modules.entity.Order>(0,0);
		String qlString = "select o from Order o where 1=1 and o.member = ? ";
		List<Object> parameter = new ArrayList<Object>();
		parameter.add(member);
		Page<com.hongqiang.shop.modules.entity.Order> orderPage = new Page<com.hongqiang.shop.modules.entity.Order>(
				pageable.getPageNumber(), pageable.getPageSize());
		return super.findPage(orderPage, qlString, parameter, pageable);
	}

	@Override
	public Page<com.hongqiang.shop.modules.entity.Order> findPage(
			com.hongqiang.shop.modules.entity.Order.OrderStatus orderStatus,
			com.hongqiang.shop.modules.entity.Order.PaymentStatus paymentStatus,
			com.hongqiang.shop.modules.entity.Order.ShippingStatus shippingStatus,
			Boolean hasExpired, Pageable pageable) {
		String qlString = "select o from Order o where 1=1 ";
		List<Object> parameter = new ArrayList<Object>();
		if (orderStatus != null) {
			qlString += " and o.orderStatus= ? ";
			parameter.add(orderStatus);
		}
		if (paymentStatus != null) {
			qlString += " and o.paymentStatus= ? ";
			parameter.add(paymentStatus);
		}
		if (shippingStatus != null) {
			qlString += " and o.shippingStatus= ? ";
			parameter.add(shippingStatus);
		}
		Date nowadays = new Date();
		if (hasExpired != null) {
			if (hasExpired.booleanValue()) {
				qlString += " and o.expire is not null and  o.expire < ? ";
				parameter.add(nowadays);
			} else {
				qlString += " and (o.expire is null or  o.expire >= ?) ";
				parameter.add(nowadays);
			}
		}
		Page<com.hongqiang.shop.modules.entity.Order> orderPage = new Page<com.hongqiang.shop.modules.entity.Order>(
				pageable.getPageNumber(), pageable.getPageSize());
		return super.findPage(orderPage, qlString, parameter, pageable);
	}

	@Override
	public Long count(
			com.hongqiang.shop.modules.entity.Order.OrderStatus orderStatus,
			com.hongqiang.shop.modules.entity.Order.PaymentStatus paymentStatus,
			com.hongqiang.shop.modules.entity.Order.ShippingStatus shippingStatus,
			Boolean hasExpired) {
		String qlString = "select o from Order o where 1=1 ";
		List<Object> parameter = new ArrayList<Object>();
		if (orderStatus != null) {
			qlString += " and o.orderStatus= ? ";
			parameter.add(orderStatus);
		}
		if (paymentStatus != null) {
			qlString += " and o.paymentStatus= ? ";
			parameter.add(paymentStatus);
		}
		if (shippingStatus != null) {
			qlString += " and o.shippingStatus= ? ";
			parameter.add(shippingStatus);
		}
		Date nowadays = new Date();
		if (hasExpired != null) {
			if (hasExpired.booleanValue()) {
				qlString += " and o.expire is not null and  o.expire < ? ";
				parameter.add(nowadays);
			} else {
				qlString += " and (o.expire is null or  o.expire >= ?) ";
				parameter.add(nowadays);
			}
		}
		StringBuilder stringBuilder = new StringBuilder(qlString);
		return super.count(stringBuilder, null, parameter);
	}

	@Override
	public Long waitingPaymentCount(Member member) {
		String qlString = "select o from Order o where 1=1 ";
		List<Object> parameter = new ArrayList<Object>();
		qlString += " and o.orderStatus <> ? and o.orderStatus <> ? ";
		parameter
				.add(com.hongqiang.shop.modules.entity.Order.OrderStatus.completed);
		parameter
				.add(com.hongqiang.shop.modules.entity.Order.OrderStatus.cancelled);

		qlString += " and (o.paymentStatus = ?  or o.paymentStatus = ?)";
		parameter
				.add(com.hongqiang.shop.modules.entity.Order.PaymentStatus.unpaid);
		parameter
				.add(com.hongqiang.shop.modules.entity.Order.PaymentStatus.partialPayment);

		qlString += " and (o.expire is null or  o.expire >= ? )";
		parameter.add(new Date());

		if (member != null) {
			qlString += " and o.member = ? ";
			parameter.add(member);
		}
		StringBuilder stringBuilder = new StringBuilder(qlString);
		return super.count(stringBuilder, null, parameter);
	}

	@Override
	public Long waitingShippingCount(Member member) {
		String qlString = "select o from Order o where 1=1 ";
		List<Object> parameter = new ArrayList<Object>();
		qlString += " and o.orderStatus <> ? and o.orderStatus <> ? ";
		parameter
				.add(com.hongqiang.shop.modules.entity.Order.OrderStatus.completed);
		parameter
				.add(com.hongqiang.shop.modules.entity.Order.OrderStatus.cancelled);

		qlString += " and (o.paymentStatus = ?  and o.shippingStatus = ?)";
		parameter
				.add(com.hongqiang.shop.modules.entity.Order.PaymentStatus.paid);
		parameter
				.add(com.hongqiang.shop.modules.entity.Order.ShippingStatus.unshipped);

		qlString += " and (o.expire is null or  o.expire >= ? )";
		parameter.add(new Date());

		if (member != null) {
			qlString += " and o.member = ? ";
			parameter.add(member);
		}
		StringBuilder stringBuilder = new StringBuilder(qlString);
		return super.count(stringBuilder, null, parameter);
	}

	@SuppressWarnings("unchecked")
	@Override
	public BigDecimal getSalesAmount(Date beginDate, Date endDate) {
		CriteriaBuilder localCriteriaBuilder = this.getEntityManager()
				.getCriteriaBuilder();
		CriteriaQuery<BigDecimal> localCriteriaQuery = localCriteriaBuilder
				.createQuery(BigDecimal.class);
		@SuppressWarnings("rawtypes")
		Root localRoot = localCriteriaQuery.from(com.hongqiang.shop.modules.entity.Order.class);
		localCriteriaQuery.select(localCriteriaBuilder.sum(localRoot
				.get("amountPaid")));
		Predicate localPredicate = localCriteriaBuilder.conjunction();
		localPredicate = localCriteriaBuilder.and(localPredicate,
				localCriteriaBuilder.equal(localRoot.get("orderStatus"),
						com.hongqiang.shop.modules.entity.Order.OrderStatus.completed));
		if (beginDate != null)
			localPredicate = localCriteriaBuilder.and(
					localPredicate,
					localCriteriaBuilder.greaterThanOrEqualTo(
							localRoot.get("createDate"), beginDate));
		if (endDate != null)
			localPredicate = localCriteriaBuilder.and(
					localPredicate,
					localCriteriaBuilder.lessThanOrEqualTo(
							localRoot.get("createDate"), endDate));
		localCriteriaQuery.where(localPredicate);
		return (BigDecimal) this.getEntityManager().createQuery(localCriteriaQuery)
				.setFlushMode(FlushModeType.COMMIT).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Integer getSalesVolume(Date beginDate, Date endDate) {
		CriteriaBuilder localCriteriaBuilder = this.getEntityManager()
				.getCriteriaBuilder();
		CriteriaQuery<Integer> localCriteriaQuery = localCriteriaBuilder
				.createQuery(Integer.class);
		@SuppressWarnings("rawtypes")
		Root localRoot = localCriteriaQuery.from(com.hongqiang.shop.modules.entity.Order.class);
		localCriteriaQuery.select(localCriteriaBuilder.sum(localRoot.join(
				"orderItems").get("shippedQuantity")));
		Predicate localPredicate = localCriteriaBuilder.conjunction();
		localPredicate = localCriteriaBuilder.and(localPredicate,
				localCriteriaBuilder.equal(localRoot.get("orderStatus"),
						com.hongqiang.shop.modules.entity.Order.OrderStatus.completed));
		if (beginDate != null)
			localPredicate = localCriteriaBuilder.and(
					localPredicate,
					localCriteriaBuilder.greaterThanOrEqualTo(
							localRoot.get("createDate"), beginDate));
		if (endDate != null)
			localPredicate = localCriteriaBuilder.and(
					localPredicate,
					localCriteriaBuilder.lessThanOrEqualTo(
							localRoot.get("createDate"), endDate));
		localCriteriaQuery.where(localPredicate);
		return (Integer) this.getEntityManager().createQuery(localCriteriaQuery)
				.setFlushMode(FlushModeType.COMMIT).getSingleResult();
	}

	@Override
	public void releaseStock() {
		String str = "select orders from Order orders where orders.isAllocatedStock = :isAllocatedStock and orders.expire is not null and orders.expire <= :now";
		List<com.hongqiang.shop.modules.entity.Order> localList = this.getEntityManager()
				.createQuery(str, com.hongqiang.shop.modules.entity.Order.class)
				.setParameter("isAllocatedStock", Boolean.valueOf(true))
				.setParameter("now", new Date()).getResultList();
		if (localList != null) {
			Iterator<com.hongqiang.shop.modules.entity.Order> localIterator1 = localList
					.iterator();
			while (localIterator1.hasNext()) {
				com.hongqiang.shop.modules.entity.Order localOrder = (com.hongqiang.shop.modules.entity.Order) localIterator1
						.next();
				if ((localOrder == null)
						|| (localOrder.getOrderItems() == null))
					continue;
				Iterator<OrderItem> localIterator2 = localOrder.getOrderItems()
						.iterator();
				while (localIterator2.hasNext()) {
					OrderItem localOrderItem = (OrderItem) localIterator2
							.next();
					if (localOrderItem == null)
						continue;
					Product localProduct = localOrderItem.getProduct();
					if (localProduct == null)
						continue;
					this.getEntityManager().lock(localProduct,
							LockModeType.PESSIMISTIC_WRITE);
					localProduct
							.setAllocatedStock(Integer
									.valueOf(localProduct.getAllocatedStock()
											.intValue()
											- (localOrderItem.getQuantity()
													.intValue() - localOrderItem
													.getShippedQuantity()
													.intValue())));
				}
				localOrder.setIsAllocatedStock(Boolean.valueOf(false));
			}
		}
	}
}