package com.hongqiang.shop.modules.shipping.web.member;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongqiang.shop.common.utils.Message;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.common.utils.Setting;
import com.hongqiang.shop.common.utils.SettingUtils;
import com.hongqiang.shop.common.utils.plugin.PaymentPlugin;
import com.hongqiang.shop.common.utils.plugin.service.PluginService;
import com.hongqiang.shop.common.web.BaseController;
import com.hongqiang.shop.modules.account.service.CartService;
import com.hongqiang.shop.modules.account.service.CouponCodeService;
import com.hongqiang.shop.modules.account.service.PaymentMethodService;
import com.hongqiang.shop.modules.entity.Area;
import com.hongqiang.shop.modules.entity.Cart;
import com.hongqiang.shop.modules.entity.Coupon;
import com.hongqiang.shop.modules.entity.CouponCode;
import com.hongqiang.shop.modules.entity.Member;
import com.hongqiang.shop.modules.entity.PaymentMethod;
import com.hongqiang.shop.modules.entity.Receiver;
import com.hongqiang.shop.modules.entity.Shipping;
import com.hongqiang.shop.modules.entity.ShippingMethod;
import com.hongqiang.shop.modules.shipping.service.OrderService;
import com.hongqiang.shop.modules.shipping.service.ReceiverService;
import com.hongqiang.shop.modules.shipping.service.ShippingMethodService;
import com.hongqiang.shop.modules.shipping.service.ShippingService;
import com.hongqiang.shop.modules.user.service.AreaService;
import com.hongqiang.shop.modules.user.service.MemberService;

@Controller("shopMemberOrderController")
@RequestMapping({ "${memberPath}/order" })
public class OrderController extends BaseController {
	private static final int PAGE_SIZE = 10;

	@Autowired
	private MemberService memberService;

	@Autowired
	private AreaService areaService;

	@Autowired
	private ReceiverService receiverService;

	@Autowired
	private CartService cartService;

	@Autowired
	private PaymentMethodService paymentMethodService;

	@Autowired
	private ShippingMethodService shippingMethodService;

	@Autowired
	private CouponCodeService couponCodeService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private ShippingService shippingService;

	@Autowired
	private PluginService pluginService;

	@RequestMapping(value = { "/save_receiver" }, method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveReceiver(Receiver receiver, Long areaId) {
		HashMap<String,Object> localHashMap = new HashMap<String,Object>();
		receiver.setArea((Area) this.areaService.find(areaId));
		if (!beanValidator(receiver, new Class[0])) {
			localHashMap.put("message", SHOP_ERROR);
			return localHashMap;
		}
		Member localMember = this.memberService.getCurrent();
		if ((Receiver.MAX_RECEIVER_COUNT != null)
				&& (localMember.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT
						.intValue())) {
			localHashMap.put("message", Message.error(
					"shop.order.addReceiverCountNotAllowed",
					new Object[] { Receiver.MAX_RECEIVER_COUNT }));
			return localHashMap;
		}
		receiver.setMember(localMember);
		this.receiverService.save(receiver);
		localHashMap.put("message", SHOP_SUCCESS);
		localHashMap.put("receiver", receiver);
		return localHashMap;
	}

	@RequestMapping(value = { "/check_lock" }, method = RequestMethod.POST)
	@ResponseBody
	public Message checkLock(String sn) {
		com.hongqiang.shop.modules.entity.Order localOrder = this.orderService
				.findBySn(sn);
		if ((localOrder != null)
				&& (localOrder.getMember() == this.memberService.getCurrent())
				&& (!localOrder.isExpired())
				&& (localOrder.getPaymentMethod() != null)
				&& (localOrder.getPaymentMethod().getType() == PaymentMethod.Type.online)
				&& ((localOrder.getPaymentStatus() == com.hongqiang.shop.modules.entity.Order.PaymentStatus.unpaid) || (localOrder
						.getPaymentStatus() == com.hongqiang.shop.modules.entity.Order.PaymentStatus.partialPayment))) {
			if (localOrder.isLocked(null))
				return Message.warn("shop.order.locked", new Object[0]);
			localOrder.setLockExpire(DateUtils.addSeconds(new Date(), 60));
			localOrder.setOperator(null);
			this.orderService.update(localOrder);
			return SHOP_SUCCESS;
		}
		return SHOP_ERROR;
	}

	@RequestMapping(value = { "/check_payment" }, method = RequestMethod.POST)
	@ResponseBody
	public boolean checkPayment(String sn) {
		com.hongqiang.shop.modules.entity.Order localOrder = this.orderService
				.findBySn(sn);
		return (localOrder != null)
				&& (localOrder.getMember() == this.memberService.getCurrent())
				&& (localOrder.getPaymentStatus() == com.hongqiang.shop.modules.entity.Order.PaymentStatus.paid);
	}

	@RequestMapping(value = { "/coupon_info" }, method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> couponInfo(String code) {
		HashMap<String,Object> localHashMap = new HashMap<String,Object>();
		Cart localCart = this.cartService.getCurrent();
		if ((localCart == null) || (localCart.isEmpty())) {
			localHashMap.put("message",
					Message.warn("shop.order.cartNotEmpty", new Object[0]));
			return localHashMap;
		}
		if (!localCart.isCouponAllowed()) {
			localHashMap.put("message",
					Message.warn("shop.order.couponNotAllowed", new Object[0]));
			return localHashMap;
		}
		CouponCode localCouponCode = this.couponCodeService.findByCode(code);
		if ((localCouponCode != null) && (localCouponCode.getCoupon() != null)) {
			Coupon localCoupon = localCouponCode.getCoupon();
			if (!localCoupon.getIsEnabled().booleanValue()) {
				localHashMap.put("message", Message.warn(
						"shop.order.couponDisabled", new Object[0]));
				return localHashMap;
			}
			if (!localCoupon.hasBegun()) {
				localHashMap.put("message", Message.warn(
						"shop.order.couponNotBegin", new Object[0]));
				return localHashMap;
			}
			if (localCoupon.hasExpired()) {
				localHashMap.put("message", Message.warn(
						"shop.order.couponHasExpired", new Object[0]));
				return localHashMap;
			}
			if (!localCart.isValid(localCoupon)) {
				localHashMap
						.put("message", Message.warn(
								"shop.order.couponInvalid", new Object[0]));
				return localHashMap;
			}
			if (localCouponCode.getIsUsed().booleanValue()) {
				localHashMap.put("message", Message.warn(
						"shop.order.couponCodeUsed", new Object[0]));
				return localHashMap;
			}
			localHashMap.put("message", SHOP_SUCCESS);
			localHashMap.put("couponName", localCoupon.getName());
			return localHashMap;
		}
		localHashMap.put("message",
				Message.warn("shop.order.couponCodeNotExist", new Object[0]));
		return localHashMap;
	}

	@RequestMapping(value = { "/info" }, method = RequestMethod.GET)
	public String info(ModelMap model) {
		Cart localCart = this.cartService.getCurrent();
		if ((localCart == null) || (localCart.isEmpty()))
			return "redirect:/cart/list.jhtml";
		if (!beanValidator( localCart, new Class[0]))
			return SHOP_ERROR_PAGE;
		com.hongqiang.shop.modules.entity.Order localOrder = this.orderService
				.build(localCart, null, null, null, null, false, null, false,
						null);
		model.addAttribute("order", localOrder);
		model.addAttribute("cartToken", localCart.getToken());
		model.addAttribute("paymentMethods",
				this.paymentMethodService.findAll());
		model.addAttribute("shippingMethods",
				this.shippingMethodService.findAll());
		return "/shop/member/order/info";
	}

	@RequestMapping(value = { "/calculate" }, method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> calculate(Long paymentMethodId,
			Long shippingMethodId, String code,
			@RequestParam(defaultValue = "false") Boolean isInvoice,
			String invoiceTitle,
			@RequestParam(defaultValue = "false") Boolean useBalance,
			String memo) {
		HashMap<String,Object> localHashMap = new HashMap<String,Object>();
		Cart localCart = this.cartService.getCurrent();
		if ((localCart == null) || (localCart.isEmpty())) {
			localHashMap.put("message",
					Message.error("shop.order.cartNotEmpty", new Object[0]));
			return localHashMap;
		}
		PaymentMethod localPaymentMethod = (PaymentMethod) this.paymentMethodService
				.find(paymentMethodId);
		ShippingMethod localShippingMethod = (ShippingMethod) this.shippingMethodService
				.find(shippingMethodId);
		CouponCode localCouponCode = this.couponCodeService.findByCode(code);
		com.hongqiang.shop.modules.entity.Order localOrder = this.orderService
				.build(localCart, null, localPaymentMethod,
						localShippingMethod, localCouponCode,
						isInvoice.booleanValue(), invoiceTitle,
						useBalance.booleanValue(), memo);
		localHashMap.put("message", SHOP_SUCCESS);
		localHashMap.put("quantity", Integer.valueOf(localOrder.getQuantity()));
		localHashMap.put("price", localOrder.getPrice());
		localHashMap.put("freight", localOrder.getFreight());
		localHashMap.put("tax", localOrder.getTax());
		localHashMap.put("amountPayable", localOrder.getAmountPayable());
		return localHashMap;
	}

	@RequestMapping(value = { "/create" }, method = RequestMethod.POST)
	@ResponseBody
	public Message create(String cartToken, Long receiverId,
			Long paymentMethodId, Long shippingMethodId, String code,
			@RequestParam(defaultValue = "false") Boolean isInvoice,
			String invoiceTitle,
			@RequestParam(defaultValue = "false") Boolean useBalance,
			String memo) {
		
		Cart localCart = this.cartService.getCurrent();
		System.out.println("cartToken="+cartToken);
		System.out.println("localCart.getToken()="+localCart.getToken());
		if ((localCart == null) || (localCart.isEmpty()))
			return Message.warn("shop.order.cartNotEmpty", new Object[0]);
		if (!StringUtils.equals(localCart.getToken(), cartToken))
			return Message.warn("shop.order.cartHasChanged", new Object[0]);
		if (localCart.getIsLowStock())
			return Message.warn("shop.order.cartLowStock", new Object[0]);
		Receiver localReceiver = (Receiver) this.receiverService
				.find(receiverId);
		if (localReceiver == null)
			return Message.error("shop.order.receiverNotExsit", new Object[0]);
		PaymentMethod localPaymentMethod = (PaymentMethod) this.paymentMethodService
				.find(paymentMethodId);
		if (localPaymentMethod == null)
			return Message.error("shop.order.paymentMethodNotExsit",
					new Object[0]);
		ShippingMethod localShippingMethod = (ShippingMethod) this.shippingMethodService
				.find(shippingMethodId);
		if (localShippingMethod == null)
			return Message.error("shop.order.shippingMethodNotExsit",
					new Object[0]);
		if (!localPaymentMethod.getShippingMethods().contains(
				localShippingMethod))
			return Message.error("shop.order.deliveryUnsupported",
					new Object[0]);
		CouponCode localCouponCode = this.couponCodeService.findByCode(code);
		com.hongqiang.shop.modules.entity.Order localOrder = this.orderService
				.create(localCart, localReceiver, localPaymentMethod,
						localShippingMethod, localCouponCode,
						isInvoice.booleanValue(), invoiceTitle,
						useBalance.booleanValue(), memo, null);
		return Message.success(localOrder.getSn(), new Object[0]);
	}

	@RequestMapping(value = { "/payment" }, method = RequestMethod.GET)
	public String payment(String sn, ModelMap model) {
		com.hongqiang.shop.modules.entity.Order localOrder = this.orderService
				.findBySn(sn);
		if ((localOrder == null)
				|| (localOrder.getMember() != this.memberService.getCurrent())
				|| (localOrder.isExpired())
				|| (localOrder.getPaymentMethod() == null))
			return SHOP_ERROR_PAGE;
		if (localOrder.getPaymentMethod().getType() == PaymentMethod.Type.online) {
			List<PaymentPlugin> localList = this.pluginService.getPaymentPlugins(true);
			if (!localList.isEmpty()) {
				PaymentPlugin localPaymentPlugin = (PaymentPlugin) localList
						.get(0);
				localOrder.setFee(localPaymentPlugin.getFee(localOrder
						.getAmountPayable()));
				model.addAttribute("defaultPaymentPlugin", localPaymentPlugin);
				model.addAttribute("paymentPlugins", localList);
			}
		}
		model.addAttribute("order", localOrder);
		return "/shop/member/order/payment";
	}

	@RequestMapping(value = { "/payment_plugin_select" }, method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> paymentPluginSelect(String sn,
			String paymentPluginId) {
		HashMap<String,Object> localHashMap = new HashMap<String,Object>();
		com.hongqiang.shop.modules.entity.Order localOrder = this.orderService
				.findBySn(sn);
		PaymentPlugin localPaymentPlugin = this.pluginService
				.getPaymentPlugin(paymentPluginId);
		if ((localOrder == null)
				|| (localOrder.getMember() != this.memberService.getCurrent())
				|| (localOrder.isExpired())
				|| (localOrder.isLocked(null))
				|| (localOrder.getPaymentMethod() == null)
				|| (localOrder.getPaymentMethod().getType() == PaymentMethod.Type.offline)
				|| (localPaymentPlugin == null)
				|| (!localPaymentPlugin.getIsEnabled())) {
			localHashMap.put("message", SHOP_ERROR);
			return localHashMap;
		}
		localOrder.setFee(localPaymentPlugin.getFee(localOrder
				.getAmountPayable()));
		localHashMap.put("message", SHOP_SUCCESS);
		localHashMap.put("fee", localOrder.getFee());
		localHashMap.put("amountPayable", localOrder.getAmountPayable());
		return localHashMap;
	}

	@RequestMapping(value = { "/list" }, method = RequestMethod.GET)
	public String list(Integer pageNumber, ModelMap model) {
		Member localMember = this.memberService.getCurrent();
		Pageable localPageable = new Pageable(pageNumber,
				Integer.valueOf(PAGE_SIZE));
		model.addAttribute("page",
				this.orderService.findPage(localMember, localPageable));
		return "shop/member/order/list";
	}

	@RequestMapping(value = { "/view" }, method = RequestMethod.GET)
	public String view(String sn, ModelMap model) {
		com.hongqiang.shop.modules.entity.Order localOrder = this.orderService
				.findBySn(sn);
		if (localOrder == null)
			return SHOP_ERROR_PAGE;
		Member localMember = this.memberService.getCurrent();
		if (!localMember.getOrders().contains(localOrder))
			return SHOP_ERROR_PAGE;
		model.addAttribute("order", localOrder);
		return "shop/member/order/view";
	}

	@RequestMapping(value = { "/cancel" }, method = RequestMethod.POST)
	@ResponseBody
	public Message cancel(String sn) {
		com.hongqiang.shop.modules.entity.Order localOrder = this.orderService
				.findBySn(sn);
		if ((localOrder != null)
				&& (localOrder.getMember() == this.memberService.getCurrent())
				&& (!localOrder.isExpired())
				&& (localOrder.getOrderStatus() == com.hongqiang.shop.modules.entity.Order.OrderStatus.unconfirmed)
				&& (localOrder.getPaymentStatus() == com.hongqiang.shop.modules.entity.Order.PaymentStatus.unpaid)) {
			if (localOrder.isLocked(null))
				return Message.warn("shop.member.order.locked", new Object[0]);
			this.orderService.cancel(localOrder, null);
			return SHOP_SUCCESS;
		}
		return SHOP_ERROR;
	}

	@RequestMapping(value = { "/delivery_query" }, method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> deliveryQuery(String sn) {
		Map<String, Object> localObject = new HashMap<String, Object>();
		Shipping localShipping = this.shippingService.findBySn(sn);
		Setting localSetting = SettingUtils.get();
		if ((localShipping != null)
				&& (localShipping.getOrder() != null)
				&& (localShipping.getOrder().getMember() == this.memberService
						.getCurrent())
				&& (StringUtils.isNotEmpty(localSetting
						.getKuaidi100Key()))
				&& (StringUtils.isNotEmpty(localShipping.getDeliveryCorpCode()))
				&& (StringUtils.isNotEmpty(localShipping.getTrackingNo())))
			localObject = this.shippingService.query(localShipping);
		return localObject;
	}
}