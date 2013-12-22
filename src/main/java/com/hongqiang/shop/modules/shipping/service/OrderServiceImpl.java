package com.hongqiang.shop.modules.shipping.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.LockModeType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.service.BaseService;
import com.hongqiang.shop.common.utils.Filter;
import com.hongqiang.shop.common.utils.Order;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.common.utils.Setting;
import com.hongqiang.shop.common.utils.SettingUtils;
import com.hongqiang.shop.modules.account.dao.CartDao;
import com.hongqiang.shop.modules.account.dao.CouponCodeDao;
import com.hongqiang.shop.modules.account.dao.PaymentDao;
import com.hongqiang.shop.modules.entity.Admin;
import com.hongqiang.shop.modules.entity.Cart;
import com.hongqiang.shop.modules.entity.CartItem;
import com.hongqiang.shop.modules.entity.Coupon;
import com.hongqiang.shop.modules.entity.CouponCode;
import com.hongqiang.shop.modules.entity.Deposit;
import com.hongqiang.shop.modules.entity.GiftItem;
import com.hongqiang.shop.modules.entity.Member;
import com.hongqiang.shop.modules.entity.MemberRank;
import com.hongqiang.shop.modules.entity.OrderItem;
import com.hongqiang.shop.modules.entity.OrderLog;
import com.hongqiang.shop.modules.entity.Payment;
import com.hongqiang.shop.modules.entity.PaymentMethod;
import com.hongqiang.shop.modules.entity.Product;
import com.hongqiang.shop.modules.entity.Promotion;
import com.hongqiang.shop.modules.entity.Receiver;
import com.hongqiang.shop.modules.entity.Refunds;
import com.hongqiang.shop.modules.entity.Returns;
import com.hongqiang.shop.modules.entity.ReturnsItem;
import com.hongqiang.shop.modules.entity.Shipping;
import com.hongqiang.shop.modules.entity.ShippingItem;
import com.hongqiang.shop.modules.entity.ShippingMethod;
import com.hongqiang.shop.modules.entity.Sn;
import com.hongqiang.shop.modules.product.dao.ProductDao;
import com.hongqiang.shop.modules.product.dao.SnDao;
import com.hongqiang.shop.modules.shipping.dao.OrderDao;
import com.hongqiang.shop.modules.shipping.dao.OrderItemDao;
import com.hongqiang.shop.modules.shipping.dao.OrderLogDao;
import com.hongqiang.shop.modules.shipping.dao.RefundsDao;
import com.hongqiang.shop.modules.shipping.dao.ReturnsDao;
import com.hongqiang.shop.modules.shipping.dao.ShippingDao;
import com.hongqiang.shop.modules.user.dao.DepositDao;
import com.hongqiang.shop.modules.user.dao.MemberDao;
import com.hongqiang.shop.modules.user.dao.MemberRankDao;
import com.hongqiang.shop.modules.util.service.StaticService;

@Service
public class OrderServiceImpl extends BaseService implements OrderService {

	@Autowired
	private OrderDao orderDao;

	@Autowired
	private OrderItemDao orderItemDao;

	@Autowired
	private OrderLogDao orderLogDao;

	@Autowired
	private CartDao cartDao;

	@Autowired
	private CouponCodeDao couponCodeDao;

	@Autowired
	private SnDao snDao;

	@Autowired
	private MemberDao memberDao;

	@Autowired
	private MemberRankDao memberRankDao;

	@Autowired
	private ProductDao productDao;

	@Autowired
	private DepositDao depositDao;

	@Autowired
	private PaymentDao paymentDao;

	@Autowired
	private RefundsDao refundsDao;

	@Autowired
	private ShippingDao shippingDao;

	@Autowired
	private ReturnsDao returnsDao;

	@Autowired
	private StaticService staticService;

	@Transactional(readOnly = true)
	public com.hongqiang.shop.modules.entity.Order findBySn(String sn) {
		return this.orderDao.findBySn(sn);
	}

	@Transactional(readOnly = true)
	public List<com.hongqiang.shop.modules.entity.Order> findList(
			Member member, Integer count, List<Filter> filters,
			List<Order> orders) {
		return this.orderDao.findList(member, count, filters, orders);
	}

	@Transactional(readOnly = true)
	public Page<com.hongqiang.shop.modules.entity.Order> findPage(
			Member member, Pageable pageable) {
		return this.orderDao.findPage(member, pageable);
	}

	@Transactional(readOnly = true)
	public Page<com.hongqiang.shop.modules.entity.Order> findPage(
			com.hongqiang.shop.modules.entity.Order.OrderStatus orderStatus,
			com.hongqiang.shop.modules.entity.Order.PaymentStatus paymentStatus,
			com.hongqiang.shop.modules.entity.Order.ShippingStatus shippingStatus,
			Boolean hasExpired, Pageable pageable) {
		return this.orderDao.findPage(orderStatus, paymentStatus,
				shippingStatus, hasExpired, pageable);
	}

	@Transactional(readOnly = true)
	public Long count(
			com.hongqiang.shop.modules.entity.Order.OrderStatus orderStatus,
			com.hongqiang.shop.modules.entity.Order.PaymentStatus paymentStatus,
			com.hongqiang.shop.modules.entity.Order.ShippingStatus shippingStatus,
			Boolean hasExpired) {
		return this.orderDao.count(orderStatus, paymentStatus, shippingStatus,
				hasExpired);
	}

	@Transactional(readOnly = true)
	public Long waitingPaymentCount(Member member) {
		return this.orderDao.waitingPaymentCount(member);
	}

	@Transactional(readOnly = true)
	public Long waitingShippingCount(Member member) {
		return this.orderDao.waitingShippingCount(member);
	}

	@Transactional(readOnly = true)
	public BigDecimal getSalesAmount(Date beginDate, Date endDate) {
		return this.orderDao.getSalesAmount(beginDate, endDate);
	}

	@Transactional(readOnly = true)
	public Integer getSalesVolume(Date beginDate, Date endDate) {
		return this.orderDao.getSalesVolume(beginDate, endDate);
	}

	public void releaseStock() {
		this.orderDao.releaseStock();
	}

	@Transactional(readOnly = true)
	public com.hongqiang.shop.modules.entity.Order build(Cart cart,
			Receiver receiver, PaymentMethod paymentMethod,
			ShippingMethod shippingMethod, CouponCode couponCode,
			boolean isInvoice, String invoiceTitle, boolean useBalance,
			String memo) {
		com.hongqiang.shop.modules.entity.Order localOrder = new com.hongqiang.shop.modules.entity.Order();
		if (cart == null || cart.getMember() == null
				|| cart.getCartItems().isEmpty()) {
			return localOrder;
		}
		localOrder
				.setShippingStatus(com.hongqiang.shop.modules.entity.Order.ShippingStatus.unshipped);
		localOrder.setFee(new BigDecimal(0));
		localOrder.setDiscount(cart.getDiscount());
		localOrder.setPoint(Integer.valueOf(cart.getPoint()));
		localOrder.setMemo(memo);
		localOrder.setMember(cart.getMember());
		if (receiver != null) {
			localOrder.setConsignee(receiver.getConsignee());
			localOrder.setAreaName(receiver.getAreaName());
			localOrder.setAddress(receiver.getAddress());
			localOrder.setZipCode(receiver.getZipCode());
			localOrder.setPhone(receiver.getPhone());
			localOrder.setArea(receiver.getArea());
		}
		if (!cart.getPromotions().isEmpty()) {
			StringBuffer stringBuffer = new StringBuffer();
			Iterator<Promotion> localObject3 = cart.getPromotions().iterator();
			while (localObject3.hasNext()) {
				Promotion localPromotion = (Promotion) localObject3.next();
				if ((localPromotion == null)
						|| (((Promotion) localPromotion).getName() == null))
					continue;
				stringBuffer.append(" " + localPromotion.getName());
			}
			if (stringBuffer.length() > 0)
				((StringBuffer) stringBuffer).deleteCharAt(0);
			localOrder.setPromotion(((StringBuffer) stringBuffer).toString());
		}
		localOrder.setPaymentMethod(paymentMethod);
		if ((shippingMethod != null)
				&& (paymentMethod != null)
				&& (paymentMethod.getShippingMethods().contains(shippingMethod))) {
			BigDecimal bigDecimal = shippingMethod.calculateFreight(Integer
					.valueOf(cart.getWeight()));
			Iterator<Promotion> localObject3 = cart.getPromotions().iterator();
			while (localObject3.hasNext()) {
				Promotion localPromotion = (Promotion) localObject3.next();
				if (!((Promotion) localPromotion).getIsFreeShipping()
						.booleanValue())
					continue;
				bigDecimal = new BigDecimal(0);
				break;
			}
			localOrder.setFreight((BigDecimal) bigDecimal);
			localOrder.setShippingMethod(shippingMethod);
		} else {
			localOrder.setFreight(new BigDecimal(0));
		}
		if ((couponCode != null) && (cart.isCouponAllowed())) {
			this.couponCodeDao.lock(couponCode, LockModeType.PESSIMISTIC_READ);
			if ((!couponCode.getIsUsed().booleanValue())
					&& (couponCode.getCoupon() != null)
					&& (cart.isValid(couponCode.getCoupon()))) {
				BigDecimal bigDecimal = couponCode.getCoupon().calculatePrice(
						cart.getAmount());
				BigDecimal bigDecimal2 = cart.getAmount().subtract(
						(BigDecimal) bigDecimal);
				if (((BigDecimal) bigDecimal2).compareTo(new BigDecimal(0)) > 0)
					localOrder.setDiscount(cart.getDiscount().add(
							(BigDecimal) bigDecimal2));
				localOrder.setCouponCode(couponCode);
			}
		}
		List<OrderItem> localOrderItems = localOrder.getOrderItems();
		Iterator<CartItem> localCartItems = cart.getCartItems().iterator();
		Product localProduct;
		OrderItem localOrderItem;
		while (localCartItems.hasNext()) {
			CartItem localCartItem = (CartItem) localCartItems.next();
			if ((localCartItem == null)
					|| (((CartItem) localCartItem).getProduct() == null))
				continue;
			localProduct = ((CartItem) localCartItem).getProduct();
			localOrderItem = new OrderItem();
			localOrderItem.setSn(localProduct.getSn());
			localOrderItem.setName(localProduct.getName());
			localOrderItem.setFullName(localProduct.getFullName());
			localOrderItem.setPrice(((CartItem) localCartItem).getUnitPrice());
			localOrderItem.setWeight(localProduct.getWeight());
			localOrderItem.setThumbnail(localProduct.getThumbnail());
			localOrderItem.setIsGift(Boolean.valueOf(false));
			localOrderItem
					.setQuantity(((CartItem) localCartItem).getQuantity());
			localOrderItem.setShippedQuantity(Integer.valueOf(0));
			localOrderItem.setReturnQuantity(Integer.valueOf(0));
			localOrderItem.setProduct(localProduct);
			localOrderItem.setOrder(localOrder);
			localOrderItems.add(localOrderItem);
		}
		Iterator<GiftItem> localGiftItems = cart.getGiftItems().iterator();
		while (localGiftItems.hasNext()) {
			GiftItem localGiftItem = (GiftItem) localGiftItems.next();
			if ((localGiftItem == null)
					|| (((GiftItem) localGiftItem).getGift() == null))
				continue;
			localProduct = ((GiftItem) localGiftItem).getGift();
			localOrderItem = new OrderItem();
			localOrderItem.setSn(localProduct.getSn());
			localOrderItem.setName(localProduct.getName());
			localOrderItem.setFullName(localProduct.getFullName());
			localOrderItem.setPrice(new BigDecimal(0));
			localOrderItem.setWeight(localProduct.getWeight());
			localOrderItem.setThumbnail(localProduct.getThumbnail());
			localOrderItem.setIsGift(Boolean.valueOf(true));
			localOrderItem
					.setQuantity(((GiftItem) localGiftItem).getQuantity());
			localOrderItem.setShippedQuantity(Integer.valueOf(0));
			localOrderItem.setReturnQuantity(Integer.valueOf(0));
			localOrderItem.setProduct(localProduct);
			localOrderItem.setOrder(localOrder);
			localOrderItems.add(localOrderItem);
		}
		Setting localSetting = SettingUtils.get();
		if ((((Setting) localSetting).getIsInvoiceEnabled().booleanValue())
				&& (isInvoice) && (StringUtils.isNotEmpty(invoiceTitle))) {
			localOrder.setIsInvoice(Boolean.valueOf(true));
			localOrder.setInvoiceTitle(invoiceTitle);
			localOrder.setTax(localOrder.calculateTax());
		} else {
			localOrder.setIsInvoice(Boolean.valueOf(false));
			localOrder.setTax(new BigDecimal(0));
		}
		if (useBalance) {
			Member localMember = cart.getMember();
			if (localMember.getBalance().compareTo(localOrder.getAmount()) >= 0)
				localOrder.setAmountPaid(localOrder.getAmount());
			else
				localOrder.setAmountPaid(localMember.getBalance());
		} else {
			localOrder.setAmountPaid(new BigDecimal(0));
		}
		if (localOrder.getAmountPayable().compareTo(new BigDecimal(0)) == 0) {
			localOrder
					.setOrderStatus(com.hongqiang.shop.modules.entity.Order.OrderStatus.confirmed);
			localOrder
					.setPaymentStatus(com.hongqiang.shop.modules.entity.Order.PaymentStatus.paid);
		} else if ((localOrder.getAmountPayable().compareTo(new BigDecimal(0)) > 0)
				&& (localOrder.getAmountPaid().compareTo(new BigDecimal(0)) > 0)) {
			localOrder
					.setOrderStatus(com.hongqiang.shop.modules.entity.Order.OrderStatus.confirmed);
			localOrder
					.setPaymentStatus(com.hongqiang.shop.modules.entity.Order.PaymentStatus.partialPayment);
		} else {
			localOrder
					.setOrderStatus(com.hongqiang.shop.modules.entity.Order.OrderStatus.unconfirmed);
			localOrder
					.setPaymentStatus(com.hongqiang.shop.modules.entity.Order.PaymentStatus.unpaid);
		}
		if ((paymentMethod != null)
				&& (paymentMethod.getTimeout() != null)
				&& (localOrder.getPaymentStatus() == com.hongqiang.shop.modules.entity.Order.PaymentStatus.unpaid))
			localOrder.setExpire(DateUtils.addMinutes(new Date(), paymentMethod
					.getTimeout().intValue()));
		return localOrder;
	}

	public com.hongqiang.shop.modules.entity.Order create(Cart cart,
			Receiver receiver, PaymentMethod paymentMethod,
			ShippingMethod shippingMethod, CouponCode couponCode,
			boolean isInvoice, String invoiceTitle, boolean useBalance,
			String memo, Admin operator) {
		com.hongqiang.shop.modules.entity.Order localOrder = new com.hongqiang.shop.modules.entity.Order();
		if (cart == null || cart.getMember() == null
				|| cart.getCartItems().isEmpty() || receiver == null
				|| paymentMethod == null || shippingMethod == null) {
			return localOrder;
		}
		localOrder = build(cart, receiver, paymentMethod, shippingMethod,
				couponCode, isInvoice, invoiceTitle, useBalance, memo);
		localOrder.setSn(this.snDao.generate(Sn.Type.order));
		if (paymentMethod.getType() == PaymentMethod.Type.online) {
			localOrder.setLockExpire(DateUtils.addSeconds(new Date(), 10));
			localOrder.setOperator(operator);
		}
		if (localOrder.getCouponCode() != null) {
			couponCode.setIsUsed(Boolean.valueOf(true));
			couponCode.setUsedDate(new Date());
			this.couponCodeDao.merge(couponCode);
		}
		Iterator<Promotion> promotionIterator = cart.getPromotions().iterator();
		while (promotionIterator.hasNext()) {
			Promotion localPromotion = (Promotion) promotionIterator.next();
			Iterator<Coupon> couponIterator = ((Promotion) localPromotion)
					.getCoupons().iterator();
			while (couponIterator.hasNext()) {
				Coupon localCoupon = (Coupon) couponIterator.next();
				localOrder.getCoupons().add(localCoupon);
			}
		}
		Setting localSetting = SettingUtils.get();
		if ((localSetting.getStockAllocationTime() == Setting.StockAllocationTime.order)
				|| ((localSetting.getStockAllocationTime() == Setting.StockAllocationTime.payment) && ((localOrder
						.getPaymentStatus() == com.hongqiang.shop.modules.entity.Order.PaymentStatus.partialPayment) || (localOrder
						.getPaymentStatus() == com.hongqiang.shop.modules.entity.Order.PaymentStatus.paid))))
			localOrder.setIsAllocatedStock(Boolean.valueOf(true));
		else
			localOrder.setIsAllocatedStock(Boolean.valueOf(false));
		this.orderDao.persist(localOrder);
		OrderLog localOrderLog = new OrderLog();
		localOrderLog.setType(OrderLog.Type.create);
		localOrderLog.setOperator(operator != null ? operator.getUsername()
				: null);
		localOrderLog.setOrder(localOrder);
		this.orderLogDao.persist(localOrderLog);
		Member localMember = cart.getMember();
		if (localOrder.getAmountPaid().compareTo(new BigDecimal(0)) > 0) {
			this.memberDao.lock(localMember, LockModeType.PESSIMISTIC_WRITE);
			localMember.setBalance(localMember.getBalance().subtract(
					localOrder.getAmountPaid()));
			this.memberDao.merge(localMember);
			Deposit localDeposit = new Deposit();
			localDeposit.setType(operator != null ? Deposit.Type.adminPayment
					: Deposit.Type.memberPayment);
			localDeposit.setCredit(new BigDecimal(0));
			localDeposit.setDebit(localOrder.getAmountPaid());
			localDeposit.setBalance(localMember.getBalance());
			localDeposit.setOperator(operator != null ? operator.getUsername()
					: null);
			localDeposit.setMember(localMember);
			localDeposit.setOrder(localOrder);
			this.depositDao.persist(localDeposit);
		}
		if ((localSetting.getStockAllocationTime() == Setting.StockAllocationTime.order)
				|| ((localSetting.getStockAllocationTime() == Setting.StockAllocationTime.payment) && ((localOrder
						.getPaymentStatus() == com.hongqiang.shop.modules.entity.Order.PaymentStatus.partialPayment) || (localOrder
						.getPaymentStatus() == com.hongqiang.shop.modules.entity.Order.PaymentStatus.paid)))) {
			Iterator<OrderItem> localIterator = localOrder.getOrderItems()
					.iterator();
			while (localIterator.hasNext()) {
				OrderItem localOrderItem = (OrderItem) localIterator.next();
				if (localOrderItem == null)
					continue;
				Product localProduct = localOrderItem.getProduct();
				this.productDao.lock(localProduct,
						LockModeType.PESSIMISTIC_WRITE);
				if ((localProduct == null) || (localProduct.getStock() == null))
					continue;
				localProduct
						.setAllocatedStock(Integer
								.valueOf(localProduct.getAllocatedStock()
										.intValue()
										+ (localOrderItem.getQuantity()
												.intValue() - localOrderItem
												.getShippedQuantity()
												.intValue())));
				this.productDao.merge(localProduct);
				this.orderDao.flush();
				this.staticService.build(localProduct);
			}
		}
		this.cartDao.remove(cart);
		return localOrder;
	}

	public void update(com.hongqiang.shop.modules.entity.Order order,
			Admin operator) {
		if (order == null) {
			return;
		}
		com.hongqiang.shop.modules.entity.Order localOrder = (com.hongqiang.shop.modules.entity.Order) this.orderDao
				.find(order.getId());
		if (localOrder.getIsAllocatedStock().booleanValue()) {
			Iterator<OrderItem> localIterator = localOrder.getOrderItems()
					.iterator();
			Product localProduct;
			while (localIterator.hasNext()) {
				OrderItem localOrderItem = (OrderItem) localIterator.next();
				if (localOrderItem == null)
					continue;
				localProduct = localOrderItem.getProduct();
				this.productDao.lock(localProduct,
						LockModeType.PESSIMISTIC_WRITE);
				if ((localProduct == null) || (localProduct.getStock() == null))
					continue;
				localProduct
						.setAllocatedStock(Integer
								.valueOf(localProduct.getAllocatedStock()
										.intValue()
										- (localOrderItem.getQuantity()
												.intValue() - localOrderItem
												.getShippedQuantity()
												.intValue())));
				this.productDao.merge(localProduct);
				this.orderDao.flush();
				this.staticService.build(localProduct);
			}
			localIterator = order.getOrderItems().iterator();
			while (localIterator.hasNext()) {
				OrderItem localOrderItem = (OrderItem) localIterator.next();
				if (localOrderItem == null)
					continue;
				localProduct = localOrderItem.getProduct();
				this.productDao.lock(localProduct,
						LockModeType.PESSIMISTIC_WRITE);
				if ((localProduct == null) || (localProduct.getStock() == null))
					continue;
				localProduct
						.setAllocatedStock(Integer
								.valueOf(localProduct.getAllocatedStock()
										.intValue()
										+ (localOrderItem.getQuantity()
												.intValue() - localOrderItem
												.getShippedQuantity()
												.intValue())));
				this.productDao.merge(localProduct);
				this.productDao.flush();
				this.staticService.build(localProduct);
			}
		}
		this.orderDao.merge(order);
		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.modify);
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrder(order);
		this.orderLogDao.persist(orderLog);
	}

	public void confirm(com.hongqiang.shop.modules.entity.Order order,
			Admin operator) {
		if (order == null) {
			return;
		}
		order.setOrderStatus(com.hongqiang.shop.modules.entity.Order.OrderStatus.confirmed);
		this.orderDao.merge(order);
		OrderLog localOrderLog = new OrderLog();
		localOrderLog.setType(OrderLog.Type.confirm);
		localOrderLog.setOperator(operator != null ? operator.getUsername()
				: null);
		localOrderLog.setOrder(order);
		this.orderLogDao.persist(localOrderLog);
	}

	public void complete(com.hongqiang.shop.modules.entity.Order order,
			Admin operator) {
		if (order == null) {
			return;
		}
		Member localMember = order.getMember();
		this.memberDao.lock(localMember, LockModeType.PESSIMISTIC_WRITE);
		if ((order.getShippingStatus() == com.hongqiang.shop.modules.entity.Order.ShippingStatus.partialShipment)
				|| (order.getShippingStatus() == com.hongqiang.shop.modules.entity.Order.ShippingStatus.shipped)) {
			localMember.setPoint(Long.valueOf(localMember.getPoint()
					.longValue() + order.getPoint().intValue()));
			Iterator<Coupon> localIterator = order.getCoupons().iterator();
			while (localIterator.hasNext()) {
				Coupon coupon = (Coupon) localIterator.next();
				this.couponCodeDao.build(coupon, localMember);
			}
		}
		if ((order.getShippingStatus() == com.hongqiang.shop.modules.entity.Order.ShippingStatus.unshipped)
				|| (order.getShippingStatus() == com.hongqiang.shop.modules.entity.Order.ShippingStatus.returned)) {
			CouponCode localCouponCode = order.getCouponCode();
			if (localCouponCode != null) {
				localCouponCode.setIsUsed(Boolean.valueOf(false));
				localCouponCode.setUsedDate(null);
				this.couponCodeDao.merge(localCouponCode);
				order.setCouponCode(null);
				this.orderDao.merge(order);
			}
		}
		localMember.setAmount(localMember.getAmount()
				.add(order.getAmountPaid()));
		if (!localMember.getMemberRank().getIsSpecial().booleanValue()) {
			MemberRank localMemberRank = this.memberRankDao
					.findByAmount(localMember.getAmount());
			if ((localMemberRank != null)
					&& (localMemberRank.getAmount().compareTo(
							localMember.getMemberRank().getAmount()) > 0))
				localMember.setMemberRank(localMemberRank);
		}
		this.memberDao.merge(localMember);
		Product localProduct;
		if (order.getIsAllocatedStock().booleanValue()) {
			Iterator<OrderItem> localIterator = order.getOrderItems()
					.iterator();
			while (localIterator.hasNext()) {
				OrderItem localOrderItem = (OrderItem) localIterator.next();
				if (localOrderItem == null)
					continue;
				localProduct = localOrderItem.getProduct();
				this.productDao.lock(localProduct,
						LockModeType.PESSIMISTIC_WRITE);
				if ((localProduct == null) || (localProduct.getStock() == null))
					continue;
				localProduct
						.setAllocatedStock(Integer
								.valueOf(localProduct.getAllocatedStock()
										.intValue()
										- (localOrderItem.getQuantity()
												.intValue() - localOrderItem
												.getShippedQuantity()
												.intValue())));
				this.productDao.merge(localProduct);
				this.orderDao.flush();
				this.staticService.build(localProduct);
			}
			order.setIsAllocatedStock(Boolean.valueOf(false));
		}
		Iterator<OrderItem> localIterator = order.getOrderItems().iterator();
		while (localIterator.hasNext()) {
			OrderItem localOrderItem = (OrderItem) localIterator.next();
			if (localOrderItem == null)
				continue;
			localProduct = localOrderItem.getProduct();
			this.productDao.lock(localProduct, LockModeType.PESSIMISTIC_WRITE);
			if (localProduct == null)
				continue;
			Integer localInteger = localOrderItem.getQuantity();
			Calendar localCalendar1 = Calendar.getInstance();
			// Calendar localCalendar2 = DateUtils.toCalendar(localProduct
			// .getWeekSalesDate());
			// Calendar localCalendar3 = DateUtils.toCalendar(localProduct
			// .getMonthSalesDate());
			Calendar localCalendar2 = Calendar.getInstance();
			localCalendar2.setTime(localProduct.getWeekSalesDate());
			Calendar localCalendar3 = Calendar.getInstance();
			localCalendar3.setTime(localProduct.getMonthSalesDate());
			if ((localCalendar1.get(1) != localCalendar2.get(1))
					|| (localCalendar1.get(3) > localCalendar2.get(3)))
				localProduct
						.setWeekSales(Long.valueOf(localInteger.intValue()));
			else
				localProduct.setWeekSales(Long.valueOf(localProduct
						.getWeekSales().longValue() + localInteger.intValue()));
			if ((localCalendar1.get(1) != localCalendar3.get(1))
					|| (localCalendar1.get(2) > localCalendar3.get(2)))
				localProduct
						.setMonthSales(Long.valueOf(localInteger.intValue()));
			else
				localProduct
						.setMonthSales(Long.valueOf(localProduct
								.getMonthSales().longValue()
								+ localInteger.intValue()));
			localProduct.setSales(Long.valueOf(localProduct.getSales()
					.longValue() + localInteger.intValue()));
			localProduct.setWeekSalesDate(new Date());
			localProduct.setMonthSalesDate(new Date());
			this.productDao.merge(localProduct);
			this.orderDao.flush();
			this.staticService.build(localProduct);
		}
		order.setOrderStatus(com.hongqiang.shop.modules.entity.Order.OrderStatus.completed);
		order.setExpire(null);
		this.orderDao.merge(order);
		OrderLog localOrderLog = new OrderLog();
		localOrderLog.setType(OrderLog.Type.complete);
		localOrderLog.setOperator(operator != null ? operator.getUsername()
				: null);
		localOrderLog.setOrder(order);
		this.orderLogDao.persist(localOrderLog);
	}

	public void cancel(com.hongqiang.shop.modules.entity.Order order,
			Admin operator) {
		if (order == null) {
			return;
		}
		CouponCode localCouponCode = order.getCouponCode();
		if (localCouponCode != null) {
			localCouponCode.setIsUsed(Boolean.valueOf(false));
			localCouponCode.setUsedDate(null);
			this.couponCodeDao.merge(localCouponCode);
			order.setCouponCode(null);
			this.orderDao.merge(order);
		}
		if (order.getIsAllocatedStock().booleanValue()) {
			Iterator<OrderItem> localIterator = order.getOrderItems()
					.iterator();
			while (localIterator.hasNext()) {
				OrderItem localOrderItem = (OrderItem) localIterator.next();
				if (localOrderItem == null)
					continue;
				Product localProduct = localOrderItem.getProduct();
				this.productDao.lock(localProduct,
						LockModeType.PESSIMISTIC_WRITE);
				if ((localProduct == null) || (localProduct.getStock() == null))
					continue;
				localProduct
						.setAllocatedStock(Integer
								.valueOf(localProduct.getAllocatedStock()
										.intValue()
										- (localOrderItem.getQuantity()
												.intValue() - localOrderItem
												.getShippedQuantity()
												.intValue())));
				this.productDao.merge(localProduct);
				this.orderDao.flush();
				this.staticService.build(localProduct);
			}
			order.setIsAllocatedStock(Boolean.valueOf(false));
		}
		order.setOrderStatus(com.hongqiang.shop.modules.entity.Order.OrderStatus.cancelled);
		order.setExpire(null);
		this.orderDao.merge(order);
		OrderLog localOrderLog = new OrderLog();
		localOrderLog.setType(OrderLog.Type.cancel);
		localOrderLog.setOperator(operator != null ? operator.getUsername()
				: null);
		localOrderLog.setOrder(order);
		this.orderLogDao.persist(localOrderLog);
	}

	public void payment(com.hongqiang.shop.modules.entity.Order order,
			Payment payment, Admin operator) {
		if (order == null || payment == null) {
			return;
		}
		this.orderDao.lock(order, LockModeType.PESSIMISTIC_WRITE);
		payment.setOrder(order);
		this.paymentDao.merge(payment);
		if (payment.getType() == Payment.Type.deposit) {
			Member localMember = order.getMember();
			this.memberDao.lock(localMember, LockModeType.PESSIMISTIC_WRITE);
			localMember.setBalance(localMember.getBalance().subtract(
					payment.getAmount()));
			this.memberDao.merge(localMember);
			Deposit localDeposit = new Deposit();
			localDeposit.setType(operator != null ? Deposit.Type.adminPayment
					: Deposit.Type.memberPayment);
			localDeposit.setCredit(new BigDecimal(0));
			localDeposit.setDebit(payment.getAmount());
			localDeposit.setBalance(localMember.getBalance());
			localDeposit.setOperator(operator != null ? operator.getUsername()
					: null);
			localDeposit.setMember(localMember);
			localDeposit.setOrder(order);
			this.depositDao.persist(localDeposit);
		}
		Setting localSetting = SettingUtils.get();
		if ((!order.getIsAllocatedStock().booleanValue())
				&& (((Setting) localSetting).getStockAllocationTime() == Setting.StockAllocationTime.payment)) {
			Iterator<OrderItem> localIterator = order.getOrderItems()
					.iterator();
			while (localIterator.hasNext()) {
				OrderItem localOrderItem = (OrderItem) localIterator.next();
				if (localOrderItem == null)
					continue;
				Product localProduct = ((OrderItem) localOrderItem)
						.getProduct();
				this.productDao.lock(localProduct,
						LockModeType.PESSIMISTIC_WRITE);
				if ((localProduct == null) || (localProduct.getStock() == null))
					continue;
				localProduct.setAllocatedStock(Integer.valueOf(localProduct
						.getAllocatedStock().intValue()
						+ (((OrderItem) localOrderItem).getQuantity()
								.intValue() - ((OrderItem) localOrderItem)
								.getShippedQuantity().intValue())));
				this.productDao.merge(localProduct);
				this.orderDao.flush();
				this.staticService.build(localProduct);
			}
			order.setIsAllocatedStock(Boolean.valueOf(true));
		}
		order.setAmountPaid(order.getAmountPaid().add(payment.getAmount()));
		order.setFee(payment.getFee());
		order.setExpire(null);
		if (order.getAmountPaid().compareTo(order.getAmount()) >= 0) {
			order.setOrderStatus(com.hongqiang.shop.modules.entity.Order.OrderStatus.confirmed);
			order.setPaymentStatus(com.hongqiang.shop.modules.entity.Order.PaymentStatus.paid);
		} else if (order.getAmountPaid().compareTo(new BigDecimal(0)) > 0) {
			order.setOrderStatus(com.hongqiang.shop.modules.entity.Order.OrderStatus.confirmed);
			order.setPaymentStatus(com.hongqiang.shop.modules.entity.Order.PaymentStatus.partialPayment);
		}
		this.orderDao.merge(order);
		OrderLog localOrderLog = new OrderLog();
		((OrderLog) localOrderLog).setType(OrderLog.Type.payment);
		((OrderLog) localOrderLog).setOperator(operator != null ? operator
				.getUsername() : null);
		((OrderLog) localOrderLog).setOrder(order);
		this.orderLogDao.persist(localOrderLog);
	}

	public void refunds(com.hongqiang.shop.modules.entity.Order order,
			Refunds refunds, Admin operator) {
		if (order == null || refunds == null) {
			return;
		}
		this.orderDao.lock(order, LockModeType.PESSIMISTIC_WRITE);
		refunds.setOrder(order);
		this.refundsDao.persist(refunds);
		if (refunds.getType() == Refunds.Type.deposit) {
			Member localMember = order.getMember();
			this.memberDao.lock(localMember, LockModeType.PESSIMISTIC_WRITE);
			((Member) localMember).setBalance(((Member) localMember)
					.getBalance().add(refunds.getAmount()));
			this.memberDao.merge(localMember);
			Deposit localDeposit = new Deposit();
			localDeposit.setType(Deposit.Type.adminRefunds);
			localDeposit.setCredit(refunds.getAmount());
			localDeposit.setDebit(new BigDecimal(0));
			localDeposit.setBalance(((Member) localMember).getBalance());
			localDeposit.setOperator(operator != null ? operator.getUsername()
					: null);
			localDeposit.setMember((Member) localMember);
			localDeposit.setOrder(order);
			this.depositDao.persist(localDeposit);
		}
		order.setAmountPaid(order.getAmountPaid().subtract(refunds.getAmount()));
		order.setExpire(null);
		if (order.getAmountPaid().compareTo(new BigDecimal(0)) == 0)
			order.setPaymentStatus(com.hongqiang.shop.modules.entity.Order.PaymentStatus.refunded);
		else if (order.getAmountPaid().compareTo(new BigDecimal(0)) > 0)
			order.setPaymentStatus(com.hongqiang.shop.modules.entity.Order.PaymentStatus.partialRefunds);
		this.orderDao.merge(order);
		OrderLog localOrderLog = new OrderLog();
		((OrderLog) localOrderLog).setType(OrderLog.Type.refunds);
		((OrderLog) localOrderLog).setOperator(operator != null ? operator
				.getUsername() : null);
		((OrderLog) localOrderLog).setOrder(order);
		this.orderLogDao.persist(localOrderLog);
	}

	public void shipping(com.hongqiang.shop.modules.entity.Order order,
			Shipping shipping, Admin operator) {
		if (order == null || shipping == null
				|| shipping.getShippingItems().isEmpty()) {
			return;
		}
		this.orderDao.lock(order, LockModeType.PESSIMISTIC_WRITE);
		Setting localSetting = SettingUtils.get();
		if ((!order.getIsAllocatedStock().booleanValue())
				&& (localSetting.getStockAllocationTime() == Setting.StockAllocationTime.ship)) {
			Iterator<OrderItem> localIterator = order.getOrderItems()
					.iterator();
			while (localIterator.hasNext()) {
				OrderItem localOrderItem = (OrderItem) localIterator.next();
				if (localOrderItem == null)
					continue;
				Product localProduct = ((OrderItem) localOrderItem)
						.getProduct();
				this.productDao.lock(localProduct,
						LockModeType.PESSIMISTIC_WRITE);
				if ((localProduct == null)
						|| (((Product) localProduct).getStock() == null))
					continue;
				((Product) localProduct)
						.setAllocatedStock(Integer
								.valueOf(((Product) localProduct)
										.getAllocatedStock().intValue()
										+ (((OrderItem) localOrderItem)
												.getQuantity().intValue() - ((OrderItem) localOrderItem)
												.getShippedQuantity()
												.intValue())));
				this.productDao.merge(localProduct);
				this.orderDao.flush();
				this.staticService.build((Product) localProduct);
			}
			order.setIsAllocatedStock(Boolean.valueOf(true));
		}
		shipping.setOrder(order);
		this.shippingDao.persist(shipping);
		Iterator<ShippingItem> localIterator = shipping.getShippingItems()
				.iterator();
		while (localIterator.hasNext()) {
			ShippingItem localShippingItem = (ShippingItem) localIterator
					.next();
			OrderItem localOrderItem = order
					.getOrderItem(((ShippingItem) localShippingItem).getSn());
			if (localOrderItem == null)
				continue;
			Product localProduct = ((OrderItem) localOrderItem).getProduct();
			this.productDao.lock(localProduct, LockModeType.PESSIMISTIC_WRITE);
			if (localProduct != null) {
				if (localProduct.getStock() != null) {
					localProduct.setStock(Integer.valueOf(localProduct
							.getStock().intValue()
							- ((ShippingItem) localShippingItem).getQuantity()
									.intValue()));
					if (order.getIsAllocatedStock().booleanValue())
						localProduct.setAllocatedStock(Integer
								.valueOf(localProduct.getAllocatedStock()
										.intValue()
										- ((ShippingItem) localShippingItem)
												.getQuantity().intValue()));
				}
				this.productDao.merge(localProduct);
				this.orderDao.flush();
				this.staticService.build(localProduct);
			}
			this.orderItemDao.lock(localOrderItem,
					LockModeType.PESSIMISTIC_WRITE);
			((OrderItem) localOrderItem).setShippedQuantity(Integer
					.valueOf(((OrderItem) localOrderItem).getShippedQuantity()
							.intValue()
							+ ((ShippingItem) localShippingItem).getQuantity()
									.intValue()));
		}
		if (order.getShippedQuantity() >= order.getQuantity()) {
			order.setShippingStatus(com.hongqiang.shop.modules.entity.Order.ShippingStatus.shipped);
			order.setIsAllocatedStock(Boolean.valueOf(false));
		} else if (order.getShippedQuantity() > 0) {
			order.setShippingStatus(com.hongqiang.shop.modules.entity.Order.ShippingStatus.partialShipment);
		}
		order.setExpire(null);
		this.orderDao.merge(order);
		OrderLog localOrderLog = new OrderLog();
		localOrderLog.setType(OrderLog.Type.shipping);
		localOrderLog.setOperator(operator != null ? operator.getUsername()
				: null);
		localOrderLog.setOrder(order);
		this.orderLogDao.persist(localOrderLog);
	}

	public void returns(com.hongqiang.shop.modules.entity.Order order,
			Returns returns, Admin operator) {
		if (order == null || returns == null
				|| returns.getReturnsItems().isEmpty()) {
			return;
		}
		this.orderDao.lock(order, LockModeType.PESSIMISTIC_WRITE);
		returns.setOrder(order);
		this.returnsDao.persist(returns);
		Iterator<ReturnsItem> localIterator = returns.getReturnsItems()
				.iterator();
		while (localIterator.hasNext()) {
			ReturnsItem localReturnsItem = (ReturnsItem) localIterator.next();
			OrderItem localOrderItem = order
					.getOrderItem(((ReturnsItem) localReturnsItem).getSn());
			if (localOrderItem == null)
				continue;
			this.orderItemDao.lock(localOrderItem,
					LockModeType.PESSIMISTIC_WRITE);
			localOrderItem.setReturnQuantity(Integer
					.valueOf(localOrderItem.getReturnQuantity().intValue()
							+ ((ReturnsItem) localReturnsItem).getQuantity()
									.intValue()));
		}
		if (order.getReturnQuantity() >= order.getShippedQuantity())
			order.setShippingStatus(com.hongqiang.shop.modules.entity.Order.ShippingStatus.returned);
		else if (order.getReturnQuantity() > 0)
			order.setShippingStatus(com.hongqiang.shop.modules.entity.Order.ShippingStatus.partialReturns);
		order.setExpire(null);
		this.orderDao.merge(order);
		OrderLog localOrderLog = new OrderLog();
		localOrderLog.setType(OrderLog.Type.returns);
		localOrderLog.setOperator(operator != null ? operator.getUsername()
				: null);
		localOrderLog.setOrder(order);
		this.orderLogDao.persist(localOrderLog);
	}

	@Transactional(readOnly = true)
	public com.hongqiang.shop.modules.entity.Order find(Long id) {
		return this.orderDao.find(id);
	}

	@Transactional
	public void save(com.hongqiang.shop.modules.entity.Order order) {
		this.orderDao.persist(order);
	}

	@Transactional
	public com.hongqiang.shop.modules.entity.Order update(
			com.hongqiang.shop.modules.entity.Order order) {
		return (com.hongqiang.shop.modules.entity.Order) this.orderDao
				.merge(order);
	}

	@Transactional
	public com.hongqiang.shop.modules.entity.Order update(
			com.hongqiang.shop.modules.entity.Order order,
			String[] ignoreProperties) {
		return (com.hongqiang.shop.modules.entity.Order) this.orderDao.update(
				order, ignoreProperties);
	}

	public void delete(com.hongqiang.shop.modules.entity.Order order) {
		if (order.getIsAllocatedStock().booleanValue()) {
			Iterator<OrderItem> localIterator = order.getOrderItems()
					.iterator();
			while (localIterator.hasNext()) {
				OrderItem localOrderItem = (OrderItem) localIterator.next();
				if (localOrderItem == null)
					continue;
				Product localProduct = localOrderItem.getProduct();
				this.productDao.lock(localProduct,
						LockModeType.PESSIMISTIC_WRITE);
				if ((localProduct == null) || (localProduct.getStock() == null))
					continue;
				localProduct
						.setAllocatedStock(Integer
								.valueOf(localProduct.getAllocatedStock()
										.intValue()
										- (localOrderItem.getQuantity()
												.intValue() - localOrderItem
												.getShippedQuantity()
												.intValue())));
				this.productDao.merge(localProduct);
				this.orderDao.flush();
				this.staticService.build(localProduct);
			}
		}
		this.orderDao.delete(order);
	}

	public void delete(Long id) {
		com.hongqiang.shop.modules.entity.Order order = this.find(id);
		this.delete(order);
	}

	public void delete(Long[] ids) {
		if (ids != null)
			for (Long id : ids)
				delete(id);
	}
}