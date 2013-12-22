package com.hongqiang.shop.modules.shipping.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Filter;
import com.hongqiang.shop.common.utils.Order;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Member;

public interface OrderDao extends OrderDaoCustom, CrudRepository<com.hongqiang.shop.modules.entity.Order, Long> {
	public com.hongqiang.shop.modules.entity.Order findBySn(String snString);
}

/**
 * DAO自定义接口
 * 
 * @author Jack
 * 
 */
interface OrderDaoCustom extends BaseDao<com.hongqiang.shop.modules.entity.Order> {

	public List<com.hongqiang.shop.modules.entity.Order> findList(Member paramMember, Integer paramInteger,
			List<Filter> paramList, List<Order> paramList1);

	public Page<com.hongqiang.shop.modules.entity.Order> findPage(Member paramMember, Pageable paramPageable);

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
}