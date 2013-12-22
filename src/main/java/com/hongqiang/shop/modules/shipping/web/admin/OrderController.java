package com.hongqiang.shop.modules.shipping.web.admin;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hongqiang.shop.common.utils.Message;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.common.web.BaseController;
import com.hongqiang.shop.modules.account.service.PaymentMethodService;
import com.hongqiang.shop.modules.entity.Admin;
import com.hongqiang.shop.modules.entity.Area;
import com.hongqiang.shop.modules.entity.DeliveryCorp;
import com.hongqiang.shop.modules.entity.Member;
import com.hongqiang.shop.modules.entity.Order;
import com.hongqiang.shop.modules.entity.OrderItem;
import com.hongqiang.shop.modules.entity.Payment;
import com.hongqiang.shop.modules.entity.PaymentMethod;
import com.hongqiang.shop.modules.entity.Product;
import com.hongqiang.shop.modules.entity.Refunds;
import com.hongqiang.shop.modules.entity.Returns;
import com.hongqiang.shop.modules.entity.ReturnsItem;
import com.hongqiang.shop.modules.entity.Shipping;
import com.hongqiang.shop.modules.entity.ShippingItem;
import com.hongqiang.shop.modules.entity.ShippingMethod;
import com.hongqiang.shop.modules.entity.Sn;
import com.hongqiang.shop.modules.product.service.ProductService;
import com.hongqiang.shop.modules.product.service.SnService;
import com.hongqiang.shop.modules.shipping.service.DeliveryCorpService;
import com.hongqiang.shop.modules.shipping.service.OrderItemService;
import com.hongqiang.shop.modules.shipping.service.OrderService;
import com.hongqiang.shop.modules.shipping.service.ShippingMethodService;
import com.hongqiang.shop.modules.user.service.AdminService;
import com.hongqiang.shop.modules.user.service.AreaService;

@Controller("adminOrderController")
@RequestMapping({ "${adminPath}/order" })
public class OrderController extends BaseController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private AreaService areaService;

	@Autowired
	private ProductService productService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderItemService orderItemService;

	@Autowired
	private ShippingMethodService shippingMethodService;

	@Autowired
	private DeliveryCorpService deliveryCorpService;

	@Autowired
	private PaymentMethodService paymentMethodService;

	@Autowired
	private SnService snService;

	@RequestMapping(value = { "/check_lock" }, method = RequestMethod.POST)
	@ResponseBody
	public Message checkLock(Long id) {
		Order localOrder = (Order) this.orderService.find(id);
		if (localOrder == null)
			return Message.warn("admin.common.invalid", new Object[0]);
		Admin localAdmin = this.adminService.getCurrent();
		if (localOrder.isLocked(localAdmin)) {
			if (localOrder.getOperator() != null)
				return Message
						.warn("admin.order.adminLocked",
								new Object[] { localOrder.getOperator()
										.getUsername() });
			return Message.warn("admin.order.memberLocked", new Object[0]);
		}
		localOrder.setLockExpire(DateUtils.addSeconds(new Date(), 60));
		localOrder.setOperator(localAdmin);
		this.orderService.update(localOrder);
		return ADMIN_SUCCESS;
	}

	@RequestMapping(value = { "/view" }, method = RequestMethod.GET)
	public String view(Long id, ModelMap model) {
		model.addAttribute("types", Payment.Type.values());
		model.addAttribute("refundsTypes", Refunds.Type.values());
		model.addAttribute("paymentMethods",
				this.paymentMethodService.findAll());
		model.addAttribute("shippingMethods",
				this.shippingMethodService.findAll());
		model.addAttribute("deliveryCorps", this.deliveryCorpService.findAll());
		model.addAttribute("order", this.orderService.find(id));
		return "/admin/order/view";
	}

	@RequestMapping(value = { "/confirm" }, method = RequestMethod.POST)
	public String confirm(Long id, RedirectAttributes redirectAttributes) {
		Order localOrder = (Order) this.orderService.find(id);
		Admin localAdmin = this.adminService.getCurrent();
		if ((localOrder != null)
				&& (!localOrder.isExpired())
				&& (localOrder.getOrderStatus() == Order.OrderStatus.unconfirmed)
				&& (!localOrder.isLocked(localAdmin))) {
			this.orderService.confirm(localOrder, localAdmin);
			addMessage(redirectAttributes, ADMIN_SUCCESS);
		} else {
			addMessage(redirectAttributes,
					Message.warn("admin.common.invalid", new Object[0]));
		}
		return "redirect:view.jhtml?id=" + id;
	}

	@RequestMapping(value = { "/complete" }, method = RequestMethod.POST)
	public String complete(Long id, RedirectAttributes redirectAttributes) {
		Order localOrder = (Order) this.orderService.find(id);
		Admin localAdmin = this.adminService.getCurrent();
		if ((localOrder != null) && (!localOrder.isExpired())
				&& (localOrder.getOrderStatus() == Order.OrderStatus.confirmed)
				&& (!localOrder.isLocked(localAdmin))) {
			this.orderService.complete(localOrder, localAdmin);
			addMessage(redirectAttributes, ADMIN_SUCCESS);
		} else {
			addMessage(redirectAttributes,
					Message.warn("admin.common.invalid", new Object[0]));
		}
		return "redirect:view.jhtml?id=" + id;
	}

	@RequestMapping(value = { "/cancel" }, method = RequestMethod.POST)
	public String cancel(Long id, RedirectAttributes redirectAttributes) {
		Order localOrder = (Order) this.orderService.find(id);
		Admin localAdmin = this.adminService.getCurrent();
		if ((localOrder != null)
				&& (!localOrder.isExpired())
				&& (localOrder.getOrderStatus() == Order.OrderStatus.unconfirmed)
				&& (!localOrder.isLocked(localAdmin))) {
			this.orderService.cancel(localOrder, localAdmin);
			addMessage(redirectAttributes, ADMIN_SUCCESS);
		} else {
			addMessage(redirectAttributes,
					Message.warn("admin.common.invalid", new Object[0]));
		}
		return "redirect:view.jhtml?id=" + id;
	}

	@RequestMapping(value = { "/payment" }, method = RequestMethod.POST)
	public String payment(Long orderId, Long paymentMethodId, Payment payment,
			RedirectAttributes redirectAttributes) {
		Order localOrder = (Order) this.orderService.find(orderId);
		payment.setOrder(localOrder);
		PaymentMethod localPaymentMethod = (PaymentMethod) this.paymentMethodService
				.find(paymentMethodId);
		payment.setPaymentMethod(localPaymentMethod != null ? localPaymentMethod
				.getName() : null);
		if (!beanValidator(redirectAttributes, payment, new Class[0]))
			return ERROR_PAGE;
		if ((localOrder.isExpired())
				|| (localOrder.getOrderStatus() != Order.OrderStatus.confirmed))
			return ERROR_PAGE;
		if ((localOrder.getPaymentStatus() != Order.PaymentStatus.unpaid)
				&& (localOrder.getPaymentStatus() != Order.PaymentStatus.partialPayment))
			return ERROR_PAGE;
		if ((payment.getAmount().compareTo(new BigDecimal(0)) <= 0)
				|| (payment.getAmount()
						.compareTo(localOrder.getAmountPayable()) > 0))
			return ERROR_PAGE;
		Member localMember = localOrder.getMember();
		if ((payment.getType() == Payment.Type.deposit)
				&& (payment.getAmount().compareTo(localMember.getBalance()) > 0))
			return ERROR_PAGE;
		Admin localAdmin = this.adminService.getCurrent();
		if (localOrder.isLocked(localAdmin))
			return ERROR_PAGE;
		payment.setSn(this.snService.generate(Sn.Type.payment));
		payment.setStatus(Payment.Status.success);
		payment.setFee(new BigDecimal(0));
		payment.setOperator(localAdmin.getUsername());
		payment.setPaymentDate(new Date());
		payment.setPaymentPluginId(null);
		payment.setExpire(null);
		payment.setDeposit(null);
		payment.setMember(null);
		this.orderService.payment(localOrder, payment, localAdmin);
		addMessage(redirectAttributes, ADMIN_SUCCESS);
		return "redirect:view.jhtml?id=" + orderId;
	}

	@RequestMapping(value = { "/refunds" }, method = RequestMethod.POST)
	public String refunds(Long orderId, Long paymentMethodId, Refunds refunds,
			RedirectAttributes redirectAttributes) {
		Order localOrder = (Order) this.orderService.find(orderId);
		refunds.setOrder(localOrder);
		PaymentMethod localPaymentMethod = (PaymentMethod) this.paymentMethodService
				.find(paymentMethodId);
		refunds.setPaymentMethod(localPaymentMethod != null ? localPaymentMethod
				.getName() : null);
		if (!beanValidator(redirectAttributes, refunds, new Class[0]))
			return ERROR_PAGE;
		if ((localOrder.isExpired())
				|| (localOrder.getOrderStatus() != Order.OrderStatus.confirmed))
			return ERROR_PAGE;
		if ((localOrder.getPaymentStatus() != Order.PaymentStatus.paid)
				&& (localOrder.getPaymentStatus() != Order.PaymentStatus.partialPayment)
				&& (localOrder.getPaymentStatus() != Order.PaymentStatus.partialRefunds))
			return ERROR_PAGE;
		if ((refunds.getAmount().compareTo(new BigDecimal(0)) <= 0)
				|| (refunds.getAmount().compareTo(localOrder.getAmountPaid()) > 0))
			return ERROR_PAGE;
		Admin localAdmin = this.adminService.getCurrent();
		if (localOrder.isLocked(localAdmin))
			return ERROR_PAGE;
		refunds.setSn(this.snService.generate(Sn.Type.refunds));
		refunds.setOperator(localAdmin.getUsername());
		this.orderService.refunds(localOrder, refunds, localAdmin);
		addMessage(redirectAttributes, ADMIN_SUCCESS);
		return "redirect:view.jhtml?id=" + orderId;
	}

	@RequestMapping(value = { "/shipping" }, method = RequestMethod.POST)
	public String shipping(Long orderId, Long shippingMethodId,
			Long deliveryCorpId, Long areaId, Shipping shipping,
			RedirectAttributes redirectAttributes) {
		Order localOrder = (Order) this.orderService.find(orderId);
		if (localOrder == null)
			return ERROR_PAGE;
		Iterator<ShippingItem> localObject1 = shipping.getShippingItems()
				.iterator();
		while (localObject1.hasNext()) {
			ShippingItem localObject2 = (ShippingItem) (localObject1.next());
			if ((localObject2 == null)
					|| (StringUtils.isEmpty(((ShippingItem) localObject2)
							.getSn()))
					|| (((ShippingItem) localObject2).getQuantity() == null)
					|| (((ShippingItem) localObject2).getQuantity().intValue() <= 0)) {
				localObject1.remove();
			} else {
				OrderItem localObject3 = localOrder.getOrderItem(localObject2
						.getSn());
				if ((localObject3 == null)
						|| (((ShippingItem) localObject2).getQuantity()
								.intValue() > ((OrderItem) localObject3)
								.getQuantity().intValue()
								- ((OrderItem) localObject3)
										.getShippedQuantity().intValue()))
					return ERROR_PAGE;
				if ((((OrderItem) localObject3).getProduct() != null)
						&& (((OrderItem) localObject3).getProduct().getStock() != null)
						&& (((ShippingItem) localObject2).getQuantity()
								.intValue() > ((OrderItem) localObject3)
								.getProduct().getStock().intValue()))
					return ERROR_PAGE;
				((ShippingItem) localObject2)
						.setName(((OrderItem) localObject3).getFullName());
				((ShippingItem) localObject2).setShipping(shipping);
			}
		}
		shipping.setOrder(localOrder);
		ShippingMethod localshippingMethod = (ShippingMethod) this.shippingMethodService
				.find(shippingMethodId);
		shipping.setShippingMethod(localshippingMethod != null ? localshippingMethod
				.getName() : null);
		DeliveryCorp localObject2 = (DeliveryCorp) this.deliveryCorpService
				.find(deliveryCorpId);
		shipping.setDeliveryCorp(localObject2 != null ? ((DeliveryCorp) localObject2)
				.getName() : null);
		shipping.setDeliveryCorpUrl(localObject2 != null ? ((DeliveryCorp) localObject2)
				.getUrl() : null);
		shipping.setDeliveryCorpCode(localObject2 != null ? ((DeliveryCorp) localObject2)
				.getCode() : null);
		Area localObject3 = (Area) this.areaService.find(areaId);
		shipping.setArea(localObject3 != null ? ((Area) localObject3)
				.getFullName() : null);
		if (!beanValidator(redirectAttributes, shipping, new Class[0]))
			return ERROR_PAGE;
		if ((localOrder.isExpired())
				|| (localOrder.getOrderStatus() != Order.OrderStatus.confirmed))
			return ERROR_PAGE;
		if ((localOrder.getShippingStatus() != Order.ShippingStatus.unshipped)
				&& (localOrder.getShippingStatus() != Order.ShippingStatus.partialShipment))
			return ERROR_PAGE;
		Admin localAdmin = this.adminService.getCurrent();
		if (localOrder.isLocked(localAdmin))
			return ERROR_PAGE;
		shipping.setSn(this.snService.generate(Sn.Type.shipping));
		shipping.setOperator(localAdmin.getUsername());
		this.orderService.shipping(localOrder, shipping, localAdmin);
		addMessage(redirectAttributes, ADMIN_SUCCESS);
		return (String) ("redirect:view.jhtml?id=" + orderId);
	}

	@RequestMapping(value = { "/returns" }, method = RequestMethod.POST)
	public String returns(Long orderId, Long shippingMethodId,
			Long deliveryCorpId, Long areaId, Returns returns,
			RedirectAttributes redirectAttributes) {
		Order localOrder = (Order) this.orderService.find(orderId);
		if (localOrder == null)
			return ERROR_PAGE;
		Iterator<ReturnsItem> localObject1 = returns.getReturnsItems()
				.iterator();
		while (localObject1.hasNext()) {
			ReturnsItem localObject2 = (ReturnsItem) localObject1.next();
			if ((localObject2 == null)
					|| (StringUtils.isEmpty(((ReturnsItem) localObject2)
							.getSn()))
					|| (((ReturnsItem) localObject2).getQuantity() == null)
					|| (((ReturnsItem) localObject2).getQuantity().intValue() <= 0)) {
				localObject1.remove();
			} else {
				OrderItem localObject3 = localOrder
						.getOrderItem(((ReturnsItem) localObject2).getSn());
				if ((localObject3 == null)
						|| (((ReturnsItem) localObject2).getQuantity()
								.intValue() > ((OrderItem) localObject3)
								.getShippedQuantity().intValue()
								- ((OrderItem) localObject3)
										.getReturnQuantity().intValue()))
					return ERROR_PAGE;
				((ReturnsItem) localObject2).setName(((OrderItem) localObject3)
						.getFullName());
				((ReturnsItem) localObject2).setReturns(returns);
			}
		}
		returns.setOrder(localOrder);
		ShippingMethod localshippingMethod = (ShippingMethod) this.shippingMethodService
				.find(shippingMethodId);
		returns.setShippingMethod(localshippingMethod != null ? localshippingMethod
				.getName() : null);
		DeliveryCorp localObject2 = (DeliveryCorp) this.deliveryCorpService
				.find(deliveryCorpId);
		returns.setDeliveryCorp(localObject2 != null ? ((DeliveryCorp) localObject2)
				.getName() : null);
		Area localObject3 = (Area) this.areaService.find(areaId);
		returns.setArea(localObject3 != null ? ((Area) localObject3)
				.getFullName() : null);
		if (!beanValidator(redirectAttributes, returns, new Class[0]))
			return ERROR_PAGE;
		if ((localOrder.isExpired())
				|| (localOrder.getOrderStatus() != Order.OrderStatus.confirmed))
			return ERROR_PAGE;
		if ((localOrder.getShippingStatus() != Order.ShippingStatus.shipped)
				&& (localOrder.getShippingStatus() != Order.ShippingStatus.partialShipment)
				&& (localOrder.getShippingStatus() != Order.ShippingStatus.partialReturns))
			return ERROR_PAGE;
		Admin localAdmin = this.adminService.getCurrent();
		if (localOrder.isLocked(localAdmin))
			return ERROR_PAGE;
		returns.setSn(this.snService.generate(Sn.Type.returns));
		returns.setOperator(localAdmin.getUsername());
		this.orderService.returns(localOrder, returns, localAdmin);
		addMessage(redirectAttributes, ADMIN_SUCCESS);
		return (String) ("redirect:view.jhtml?id=" + orderId);
	}

	@RequestMapping(value = { "/edit" }, method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		model.addAttribute("paymentMethods",
				this.paymentMethodService.findAll());
		model.addAttribute("shippingMethods",
				this.shippingMethodService.findAll());
		model.addAttribute("order", this.orderService.find(id));
		return "/admin/order/edit";
	}

	@RequestMapping(value = { "/order_item_add" }, method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> orderItemAdd(String productSn) {
		HashMap<String, Object> localHashMap = new HashMap<String, Object>();
		Product localProduct = this.productService.findBySn(productSn);
		if (localProduct == null) {
			localHashMap.put("message",
					Message.warn("admin.order.productNotExist", new Object[0]));
			return localHashMap;
		}
		if (!localProduct.getIsMarketable().booleanValue()) {
			localHashMap.put("message", Message.warn(
					"admin.order.productNotMarketable", new Object[0]));
			return localHashMap;
		}
		if (localProduct.getIsOutOfStock().booleanValue()) {
			localHashMap.put("message", Message.warn(
					"admin.order.productOutOfStock", new Object[0]));
			return localHashMap;
		}
		localHashMap.put("sn", localProduct.getSn());
		localHashMap.put("fullName", localProduct.getFullName());
		localHashMap.put("price", localProduct.getPrice());
		localHashMap.put("weight", localProduct.getWeight());
		localHashMap.put("isGift", localProduct.getIsGift());
		localHashMap.put("message", ADMIN_SUCCESS);
		return localHashMap;
	}

	@RequestMapping(value = { "/calculate" }, method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> calculate(Order order, Long areaId,
			Long paymentMethodId, Long shippingMethodId) {
		HashMap<String, Object> localHashMap = new HashMap<String, Object>();
		Iterator<OrderItem> localObject1 = order.getOrderItems().iterator();
		while (localObject1.hasNext()) {
			OrderItem localObject2 = (OrderItem) localObject1.next();
			if ((localObject2 != null)
					&& (!StringUtils
							.isEmpty(((OrderItem) localObject2).getSn())))
				continue;
			localObject1.remove();
		}
		order.setArea((Area) this.areaService.find(areaId));
		order.setPaymentMethod((PaymentMethod) this.paymentMethodService
				.find(paymentMethodId));
		order.setShippingMethod((ShippingMethod) this.shippingMethodService
				.find(shippingMethodId));
		if (!beanValidator(order, new Class[0])) {
			localHashMap.put("message",
					Message.warn("admin.common.invalid", new Object[0]));
			return localHashMap;
		}
		Order localOrder = (Order) this.orderService.find(order.getId());
		if (localOrder == null) {
			localHashMap.put("message",
					Message.error("admin.common.invalid", new Object[0]));
			return localHashMap;
		}
		Iterator<OrderItem> localObject3 = order.getOrderItems().iterator();
		while (localObject3.hasNext()) {
			OrderItem localObject2 = (OrderItem) localObject3.next();
			if (((OrderItem) localObject2).getId() != null) {
				OrderItem localObject4 = (OrderItem) this.orderItemService
						.find(((OrderItem) localObject2).getId());
				if ((localObject4 == null)
						|| (!((Order) localOrder)
								.equals(((OrderItem) localObject4).getOrder()))) {
					localHashMap.put("message", Message.error(
							"admin.common.invalid", new Object[0]));
					return localHashMap;
				}
				Product localProduct = ((OrderItem) localObject4).getProduct();
				if ((localProduct == null) || (localProduct.getStock() == null))
					continue;
				if (((Order) localOrder).getIsAllocatedStock().booleanValue()) {
					if (((OrderItem) localObject2).getQuantity().intValue() <= localProduct
							.getAvailableStock().intValue()
							+ ((OrderItem) localObject4).getQuantity()
									.intValue())
						continue;
					localHashMap
							.put("message", Message.warn(
									"admin.order.lowStock", new Object[0]));
					return localHashMap;
				}
				if (((OrderItem) localObject2).getQuantity().intValue() <= localProduct
						.getAvailableStock().intValue())
					continue;
				localHashMap.put("message",
						Message.warn("admin.order.lowStock", new Object[0]));
				return localHashMap;
			}
			Product localObject4 = this.productService
					.findBySn(((OrderItem) localObject2).getSn());
			if (localObject4 == null) {
				localHashMap.put("message",
						Message.error("admin.common.invalid", new Object[0]));
				return localHashMap;
			}
			if ((((Product) localObject4).getStock() == null)
					|| (((OrderItem) localObject2).getQuantity().intValue() <= ((Product) localObject4)
							.getAvailableStock().intValue()))
				continue;
			localHashMap.put("message",
					Message.warn("admin.order.lowStock", new Object[0]));
			return localHashMap;
		}
		HashMap<String, OrderItem> localObject2 = new HashMap<String, OrderItem>();
		Iterator<OrderItem> orderItemIterator = order.getOrderItems()
				.iterator();
		while (orderItemIterator.hasNext()) {
			OrderItem orderItem = (OrderItem) orderItemIterator.next();
			localObject2.put(orderItem.getSn(), orderItem);
		}
		localHashMap.put("weight", Integer.valueOf(order.getWeight()));
		localHashMap.put("price", order.getPrice());
		localHashMap.put("quantity", Integer.valueOf(order.getQuantity()));
		localHashMap.put("amount", order.getAmount());
		localHashMap.put("orderItems", localObject2);
		localHashMap.put("message", ADMIN_SUCCESS);
		return localHashMap;
	}

	@RequestMapping(value = { "/update" }, method = RequestMethod.POST)
	public String update(Order order, Long areaId, Long paymentMethodId,
			Long shippingMethodId, RedirectAttributes redirectAttributes) {
		Iterator<OrderItem> localObject1 = order.getOrderItems().iterator();
		while (localObject1.hasNext()) {
			OrderItem localObject2 = (OrderItem) localObject1.next();
			if ((localObject2 != null)
					&& (!StringUtils
							.isEmpty(((OrderItem) localObject2).getSn())))
				continue;
			localObject1.remove();
		}
		order.setArea((Area) this.areaService.find(areaId));
		order.setPaymentMethod((PaymentMethod) this.paymentMethodService
				.find(paymentMethodId));
		order.setShippingMethod((ShippingMethod) this.shippingMethodService
				.find(shippingMethodId));
		if (!beanValidator(redirectAttributes, order, new Class[0]))
			return ERROR_PAGE;
		Order localOrder = (Order) this.orderService.find(order.getId());
		if (localOrder == null)
			return ERROR_PAGE;
		if ((((Order) localOrder).isExpired())
				|| (((Order) localOrder).getOrderStatus() != Order.OrderStatus.unconfirmed))
			return ERROR_PAGE;
		Object localObject2 = this.adminService.getCurrent();
		if (((Order) localOrder).isLocked((Admin) localObject2))
			return ERROR_PAGE;
		if (!order.getIsInvoice().booleanValue()) {
			order.setInvoiceTitle(null);
			order.setTax(new BigDecimal(0));
		}
		Iterator<OrderItem> localIterator = order.getOrderItems().iterator();
		while (localIterator.hasNext()) {
			OrderItem localOrderItem = (OrderItem) localIterator.next();
			if (localOrderItem.getId() != null) {
				OrderItem localObject3 = (OrderItem) this.orderItemService
						.find(localOrderItem.getId());
				if ((localObject3 == null)
						|| (!((Order) localOrder)
								.equals(((OrderItem) localObject3).getOrder())))
					return ERROR_PAGE;
				Product localProduct = ((OrderItem) localObject3).getProduct();
				if ((localProduct != null) && (localProduct.getStock() != null))
					if (((Order) localOrder).getIsAllocatedStock()
							.booleanValue()) {
						if (localOrderItem.getQuantity().intValue() > localProduct
								.getAvailableStock().intValue()
								+ ((OrderItem) localObject3).getQuantity()
										.intValue())
							return ERROR_PAGE;
					} else if (localOrderItem.getQuantity().intValue() > localProduct
							.getAvailableStock().intValue())
						return ERROR_PAGE;
				BeanUtils.copyProperties(localObject3, localOrderItem,
						new String[] { "price", "quantity" });
				if (!((OrderItem) localObject3).getIsGift().booleanValue())
					continue;
				localOrderItem.setPrice(new BigDecimal(0));
			} else {
				Product localObject3 = this.productService
						.findBySn(localOrderItem.getSn());
				if (localObject3 == null)
					return ERROR_PAGE;
				if ((((Product) localObject3).getStock() != null)
						&& (localOrderItem.getQuantity().intValue() > ((Product) localObject3)
								.getAvailableStock().intValue()))
					return ERROR_PAGE;
				localOrderItem.setName(((Product) localObject3).getName());
				localOrderItem.setFullName(((Product) localObject3)
						.getFullName());
				if (((Product) localObject3).getIsGift().booleanValue())
					localOrderItem.setPrice(new BigDecimal(0));
				localOrderItem.setWeight(((Product) localObject3).getWeight());
				localOrderItem.setThumbnail(((Product) localObject3)
						.getThumbnail());
				localOrderItem.setIsGift(((Product) localObject3).getIsGift());
				localOrderItem.setShippedQuantity(Integer.valueOf(0));
				localOrderItem.setReturnQuantity(Integer.valueOf(0));
				localOrderItem.setProduct((Product) localObject3);
				localOrderItem.setOrder((Order) localOrder);
			}
		}
		order.setSn(((Order) localOrder).getSn());
		order.setOrderStatus(((Order) localOrder).getOrderStatus());
		order.setPaymentStatus(((Order) localOrder).getPaymentStatus());
		order.setShippingStatus(((Order) localOrder).getShippingStatus());
		order.setFee(((Order) localOrder).getFee());
		order.setAmountPaid(((Order) localOrder).getAmountPaid());
		order.setPromotion(((Order) localOrder).getPromotion());
		order.setExpire(((Order) localOrder).getExpire());
		order.setLockExpire(null);
		order.setIsAllocatedStock(((Order) localOrder).getIsAllocatedStock());
		order.setOperator(null);
		order.setMember(((Order) localOrder).getMember());
		order.setCouponCode(((Order) localOrder).getCouponCode());
		order.setCoupons(((Order) localOrder).getCoupons());
		order.setOrderLogs(((Order) localOrder).getOrderLogs());
		order.setDeposits(((Order) localOrder).getDeposits());
		order.setPayments(((Order) localOrder).getPayments());
		order.setRefunds(((Order) localOrder).getRefunds());
		order.setShippings(((Order) localOrder).getShippings());
		order.setReturns(((Order) localOrder).getReturns());
		this.orderService.update(order, (Admin) localObject2);
		addMessage(redirectAttributes, ADMIN_SUCCESS);
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = RequestMethod.GET)
	public String list(Order.OrderStatus orderStatus,
			Order.PaymentStatus paymentStatus,
			Order.ShippingStatus shippingStatus, Boolean hasExpired,
			Pageable pageable, ModelMap model) {
		model.addAttribute("orderStatus", orderStatus);
		model.addAttribute("paymentStatus", paymentStatus);
		model.addAttribute("shippingStatus", shippingStatus);
		model.addAttribute("hasExpired", hasExpired);
		model.addAttribute("page", this.orderService.findPage(orderStatus,
				paymentStatus, shippingStatus, hasExpired, pageable));
		return "/admin/order/list";
	}

	@RequestMapping(value = { "/delete" }, method = RequestMethod.POST)
	@ResponseBody
	public Message delete(Long[] ids) {
		if (ids != null) {
			Admin localAdmin = this.adminService.getCurrent();
			for (Long localLong : ids) {
				Order localOrder = (Order) this.orderService.find(localLong);
				if ((localOrder != null) && (localOrder.isLocked(localAdmin)))
					return Message.error("admin.order.deleteLockedNotAllowed",
							new Object[] { localOrder.getSn() });
			}
			this.orderService.delete(ids);
		}
		return ADMIN_SUCCESS;
	}
}