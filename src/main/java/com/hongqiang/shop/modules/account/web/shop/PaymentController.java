package com.hongqiang.shop.modules.account.web.shop;

import java.math.BigDecimal;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hongqiang.shop.common.utils.plugin.PaymentPlugin;
import com.hongqiang.shop.common.utils.plugin.service.PluginService;
import com.hongqiang.shop.common.web.BaseController;
import com.hongqiang.shop.modules.account.service.PaymentService;
import com.hongqiang.shop.modules.entity.Member;
import com.hongqiang.shop.modules.entity.Payment;
import com.hongqiang.shop.modules.entity.PaymentMethod;
import com.hongqiang.shop.modules.entity.Sn;
import com.hongqiang.shop.modules.product.service.SnService;
import com.hongqiang.shop.modules.shipping.service.OrderService;
import com.hongqiang.shop.modules.user.service.MemberService;

@Controller("shopPaymentController")
@RequestMapping({ "${frontPath}/payment" })
public class PaymentController extends BaseController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private PluginService pluginService;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private SnService snService;

	@RequestMapping(value = { "/submit" }, method = RequestMethod.POST)
	public String submit(String sn, String paymentPluginId,
			HttpServletRequest request, ModelMap model) {
		com.hongqiang.shop.modules.entity.Order localOrder = this.orderService
				.findBySn(sn);
		if (localOrder == null)
			return SHOP_ERROR_PAGE;
		Member localMember = this.memberService.getCurrent();
		if ((localMember == null) || (localOrder.getMember() != localMember)
				|| (localOrder.isExpired()))
			return SHOP_ERROR_PAGE;
		if ((localOrder.getPaymentMethod() == null)
				|| (localOrder.getPaymentMethod().getType() == PaymentMethod.Type.offline))
			return SHOP_ERROR_PAGE;
		if ((localOrder.getPaymentStatus() != com.hongqiang.shop.modules.entity.Order.PaymentStatus.unpaid)
				&& (localOrder.getPaymentStatus() != com.hongqiang.shop.modules.entity.Order.PaymentStatus.partialPayment))
			return SHOP_ERROR_PAGE;
		if (localOrder.getAmountPayable().compareTo(new BigDecimal(0)) <= 0)
			return SHOP_ERROR_PAGE;
		PaymentPlugin localPaymentPlugin = this.pluginService
				.getPaymentPlugin(paymentPluginId);
		if ((localPaymentPlugin == null)
				|| (!localPaymentPlugin.getIsEnabled()))
			return SHOP_ERROR_PAGE;
		BigDecimal localBigDecimal1 = localPaymentPlugin.getFee(localOrder
				.getAmountPayable());
		BigDecimal localBigDecimal2 = localOrder.getAmountPayable().add(
				localBigDecimal1);
		Payment localPayment = new Payment();
		localPayment.setSn(this.snService.generate(Sn.Type.payment));
		localPayment.setType(Payment.Type.online);
		localPayment.setStatus(Payment.Status.wait);
		localPayment.setPaymentMethod(localOrder.getPaymentMethodName() + "-"
				+ localPaymentPlugin.getPaymentName());
		localPayment.setFee(localBigDecimal1);
		localPayment.setAmount(localBigDecimal2);
		localPayment.setPaymentPluginId(paymentPluginId);
		localPayment
				.setExpire(localPaymentPlugin.getTimeout() != null ? DateUtils
						.addMinutes(new Date(), localPaymentPlugin.getTimeout()
								.intValue()) : null);
		localPayment.setMember(null);
		localPayment.setOrder(localOrder);
		this.paymentService.save(localPayment);
		model.addAttribute("url", localPaymentPlugin.getUrl());
		model.addAttribute("method", localPaymentPlugin.getMethod());
		model.addAttribute("parameterMap", localPaymentPlugin.getParameterMap(
				localPayment.getSn(), localBigDecimal2,
				localOrder.getProductName(), request));
		return "shop/payment/submit";
	}

	@RequestMapping({ "/return/{sn}" })
	public String returns(@PathVariable String sn, HttpServletRequest request,
			ModelMap model) {
		Payment localPayment = this.paymentService.findBySn(sn);
		if (localPayment == null)
			return SHOP_ERROR_PAGE;
		if (localPayment.getStatus() == Payment.Status.wait) {
			PaymentPlugin localPaymentPlugin = this.pluginService
					.getPaymentPlugin(localPayment.getPaymentPluginId());
			if ((localPaymentPlugin != null)
					&& (localPaymentPlugin.verify(sn, request))) {
				BigDecimal localBigDecimal1 = localPaymentPlugin.getAmount(sn,
						request);
				if (localBigDecimal1.compareTo(localPayment.getAmount()) >= 0) {
					com.hongqiang.shop.modules.entity.Order localOrder = localPayment
							.getOrder();
					if (localOrder != null) {
						if (localBigDecimal1.compareTo(localOrder
								.getAmountPayable()) >= 0)
							this.orderService.payment(localOrder, localPayment,
									null);
					} else {
						Member localMember = localPayment.getMember();
						if (localMember != null) {
							BigDecimal localBigDecimal2 = localPayment
									.getAmount()
									.subtract(localPayment.getFee());
							this.memberService.update(
									localMember,
									null,
									localBigDecimal2,
									addMessage("shop.payment.paymentName",
											new Object[] { localPaymentPlugin
													.getPaymentName() }), null);
						}
					}
				}
				localPayment.setStatus(Payment.Status.success);
				localPayment.setAmount(localBigDecimal1);
				localPayment.setPaymentDate(new Date());
			} else {
				localPayment.setStatus(Payment.Status.failure);
				localPayment.setPaymentDate(new Date());
			}
			this.paymentService.update(localPayment);
		}
		model.addAttribute("payment", localPayment);
		return "shop/payment/return";
	}

	@RequestMapping({ "/notify/{sn}" })
	public String notify(@PathVariable String sn, HttpServletRequest request,
			ModelMap model) {
		Payment localPayment = this.paymentService.findBySn(sn);
		if (localPayment != null) {
			PaymentPlugin localPaymentPlugin = this.pluginService
					.getPaymentPlugin(localPayment.getPaymentPluginId());
			if (localPaymentPlugin != null) {
				if ((localPayment.getStatus() == Payment.Status.wait)
						&& (localPaymentPlugin.verify(sn, request))) {
					BigDecimal localBigDecimal1 = localPaymentPlugin.getAmount(
							sn, request);
					if (localBigDecimal1.compareTo(localPayment.getAmount()) >= 0) {
						com.hongqiang.shop.modules.entity.Order localOrder = localPayment
								.getOrder();
						if (localOrder != null) {
							if (localBigDecimal1.compareTo(localOrder
									.getAmountPayable()) >= 0)
								this.orderService.payment(localOrder,
										localPayment, null);
						} else {
							Member localMember = localPayment.getMember();
							if (localMember != null) {
								BigDecimal localBigDecimal2 = localPayment
										.getAmount().subtract(
												localPayment.getFee());
								this.memberService
										.update(localMember,
												null,
												localBigDecimal2,
												addMessage(
														"shop.payment.paymentName",
														new Object[] { localPaymentPlugin
																.getPaymentName() }),
												null);
							}
						}
					}
					localPayment.setStatus(Payment.Status.success);
					localPayment.setAmount(localBigDecimal1);
					localPayment.setPaymentDate(new Date());
					this.paymentService.update(localPayment);
				}
				model.addAttribute("notifyContext",
						localPaymentPlugin.getNotifyContext(sn, request));
			}
		}
		return "shop/payment/notify";
	}
}