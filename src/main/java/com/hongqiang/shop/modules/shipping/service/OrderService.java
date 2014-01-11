package com.hongqiang.shop.modules.shipping.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.hongqiang.shop.common.base.persistence.Page;
import com.hongqiang.shop.common.utils.Filter;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Admin;
import com.hongqiang.shop.modules.entity.Cart;
import com.hongqiang.shop.modules.entity.CouponCode;
import com.hongqiang.shop.modules.entity.Member;
import com.hongqiang.shop.modules.entity.Payment;
import com.hongqiang.shop.modules.entity.PaymentMethod;
import com.hongqiang.shop.modules.entity.Receiver;
import com.hongqiang.shop.modules.entity.Refunds;
import com.hongqiang.shop.modules.entity.Returns;
import com.hongqiang.shop.modules.entity.Shipping;
import com.hongqiang.shop.modules.entity.ShippingMethod;

public interface OrderService {
	public com.hongqiang.shop.modules.entity.Order findBySn(String sntring);

	public List<com.hongqiang.shop.modules.entity.Order> findList(
			Member paramMember, Integer paramInteger, List<Filter> paramList,
			List<com.hongqiang.shop.common.utils.Order> paramList1);

	public Page<com.hongqiang.shop.modules.entity.Order> findPage(
			Member paramMember, Pageable paramPageable);

	public Page<com.hongqiang.shop.modules.entity.Order> findPage(
			com.hongqiang.shop.modules.entity.Order.OrderStatus paramOrderStatus,
			com.hongqiang.shop.modules.entity.Order.PaymentStatus paramPaymentStatus,
			com.hongqiang.shop.modules.entity.Order.ShippingStatus paramShippingStatus,
			Boolean paramBoolean, Pageable paramPageable);

	public Long count(
			com.hongqiang.shop.modules.entity.Order.OrderStatus paramOrderStatus,
			com.hongqiang.shop.modules.entity.Order.PaymentStatus paramPaymentStatus,
			com.hongqiang.shop.modules.entity.Order.ShippingStatus paramShippingStatus,
			Boolean paramBoolean);

	public Long waitingPaymentCount(Member paramMember);

	public Long waitingShippingCount(Member paramMember);

	public BigDecimal getSalesAmount(Date paramDate1, Date paramDate2);

	public Integer getSalesVolume(Date paramDate1, Date paramDate2);

	public void releaseStock();

	public com.hongqiang.shop.modules.entity.Order build(Cart paramCart,
			Receiver paramReceiver, PaymentMethod paramPaymentMethod,
			ShippingMethod paramShippingMethod, CouponCode paramCouponCode,
			boolean paramBoolean1, String paramString1, boolean paramBoolean2,
			String paramString2);

	public com.hongqiang.shop.modules.entity.Order create(Cart paramCart,
			Receiver paramReceiver, PaymentMethod paramPaymentMethod,
			ShippingMethod paramShippingMethod, CouponCode paramCouponCode,
			boolean paramBoolean1, String paramString1, boolean paramBoolean2,
			String paramString2, Admin paramAdmin);

	public void update(com.hongqiang.shop.modules.entity.Order paramOrder,
			Admin paramAdmin);

	public void confirm(com.hongqiang.shop.modules.entity.Order paramOrder,
			Admin paramAdmin);

	public void complete(com.hongqiang.shop.modules.entity.Order paramOrder,
			Admin paramAdmin);

	public void cancel(com.hongqiang.shop.modules.entity.Order paramOrder,
			Admin paramAdmin);

	public void payment(com.hongqiang.shop.modules.entity.Order paramOrder,
			Payment paramPayment, Admin paramAdmin);

	public void refunds(com.hongqiang.shop.modules.entity.Order paramOrder,
			Refunds paramRefunds, Admin paramAdmin);

	public void shipping(com.hongqiang.shop.modules.entity.Order paramOrder,
			Shipping paramShipping, Admin paramAdmin);

	public void returns(com.hongqiang.shop.modules.entity.Order paramOrder,
			Returns paramReturns, Admin paramAdmin);

	public com.hongqiang.shop.modules.entity.Order find(Long id);

	public void save(com.hongqiang.shop.modules.entity.Order order);

	public com.hongqiang.shop.modules.entity.Order update(
			com.hongqiang.shop.modules.entity.Order order);

	public com.hongqiang.shop.modules.entity.Order update(
			com.hongqiang.shop.modules.entity.Order order,
			String[] ignoreProperties);

	public void delete(com.hongqiang.shop.modules.entity.Order order);

	public void delete(Long id);

	public void delete(Long[] ids);
}