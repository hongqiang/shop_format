package com.hongqiang.shop.common.utils.plugin.alipay;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.hongqiang.shop.common.utils.plugin.PaymentPlugin;
import com.hongqiang.shop.website.entity.PluginConfig;

@Component("alipayTradePlugin")
public class AlipayTradePlugin extends PaymentPlugin{
	
	public String getName() {
		return "支付宝担保交易";
	}

	public String getVersion() {
		return "1.0";
	}

	public String getAuthor() {
		return "Alipay";
	}

	public String getSiteUrl() {
		return "http://www.alipay.com";
	}

	public String getInstallUrl() {
		return "alipay_trade/install.jhtml";
	}

	public String getUninstallUrl() {
		return "alipay_trade/uninstall.jhtml";
	}

	public String getSettingUrl() {
		return "alipay_trade/setting.jhtml";
	}

	public String getUrl() {
		return "https://mapi.alipay.com/gateway.do";
	}

	public PaymentPlugin.Method getMethod() {
		return PaymentPlugin.Method.get;
	}

	public Integer getTimeout() {
		return Integer.valueOf(21600);
	}

	public Map<String, String> getConsigneeInfo(
			com.hongqiang.shop.modules.entity.Order order) {
		Map<String, String> paramsMap = new HashMap<String, String>();
		// 物流费用
		BigDecimal logistics_fee = order.getFreight().setScale(2,  BigDecimal.ROUND_HALF_UP);
		paramsMap.put("logistics_fee", logistics_fee.toString());
		// 物流类型，三个值可选：EXPRESS（快递）、POST（平邮）、EMS（EMS）
		String shipping_method_name = order.getShippingMethodName();
		String logistics_type;
		if (shipping_method_name.compareTo("普通快递")== 0) {
			logistics_type = "POST";
		}else if (shipping_method_name.compareTo("EMS")== 0) {
			logistics_type = "EMS";
		}else {
			 logistics_type = "EXPRESS";
		}
		paramsMap.put("logistics_type", logistics_type);
		// 物流支付方式，两个值可选：SELLER_PAY（卖家承担运费）、BUYER_PAY（买家承担运费）
		String logistics_payment = "SELLER_PAY";
		paramsMap.put("logistics_payment", logistics_payment);
		// 收货人姓名 如：张三
		String receive_name = order.getConsignee();
		// 收货人地址
		String receive_address = order.getAddress();
		// 收货人邮编
		String receive_zip = order.getZipCode();
		// 收货人手机号码
		String receive_mobile = order.getPhone();
		paramsMap.put("receive_name", receive_name);
		paramsMap.put("receive_address", receive_address);
		paramsMap.put("receive_zip", receive_zip);
		paramsMap.put("receive_mobile", receive_mobile);
		return paramsMap;
	}
	
	public Map<String, String> getParameterMap(String sn, BigDecimal amount,
			String description, HttpServletRequest request) {
		BigDecimal price = amount.setScale(2,  BigDecimal.ROUND_HALF_UP);
		Map<String, String> paramsMap = new HashMap<String, String>();
		//配送费以及配送方式先不写
		// 支付类型
		String payment_type = "1";
		// 服务器异步通知页面路径,根据付款编号获得页面路径
		String notify_url = getNotifyUrl(sn);
		// 页面跳转同步通知页面路径
		String return_url = getReturnUrl(sn);
		// 商品数量,建议默认为1，不改变值，把一次交易看成是一次下订单而非购买一件商品
		String quantity = "1";
		// 字符集
		String input_charset = "utf-8";
		// 订单描述
		if (getIsEnabled()) {
			PluginConfig pluginConfig = getPluginConfig();
			String key = (pluginConfig != null) ? pluginConfig.getAttribute("key") : null;
			String partner = (pluginConfig != null) ? pluginConfig.getAttribute("partner") : null;
			String seller_email = (pluginConfig != null) ? pluginConfig.getAttribute("seller_email") : null;
			paramsMap.put("out_trade_no", sn);
			paramsMap.put("price", price.toString());
			paramsMap.put("_input_charset", input_charset);
			paramsMap.put("partner", partner);
			paramsMap.put("seller_email", seller_email);
			paramsMap.put("notify_url", notify_url);
			paramsMap.put("return_url", return_url);
			paramsMap.put("service", "create_partner_trade_by_buyer");
			paramsMap.put("payment_type", payment_type);
			paramsMap.put("subject", description);
			paramsMap.put("quantity", quantity);
			if (getTradeInfoMap()!=null && !getTradeInfoMap().isEmpty()) {
				paramsMap.putAll(getTradeInfoMap());
			}
			String mysign = AlipayUtils.buildRequestMysign(paramsMap, key, input_charset);
			paramsMap.put("sign", mysign);
			paramsMap.put("sign_type", "MD5");
		}
		getTradeInfoMap().clear();
		getTradeInfoMap().putAll(paramsMap);
		System.out.println(paramsMap);
		return paramsMap;
	}

	public boolean verify(String sn, HttpServletRequest request) {
//		BigDecimal amount = getAmount(sn, request);
//		String description = null;
//		Map<String, String> paramsMap = getParameterMap(sn, amount, description, request);
		PluginConfig pluginConfig = getPluginConfig();
		String key = (pluginConfig != null) ? pluginConfig.getAttribute("key") : null;
//		return AlipayUtils.verify(paramsMap, key);
		System.out.println(getTradeInfoMap());
		return AlipayUtils.verify(getTradeInfoMap(), key);
	}

	public BigDecimal getAmount(String sn, HttpServletRequest request) {
		return new BigDecimal(request.getParameter("price"));
	}

	public String getNotifyContext(String sn, HttpServletRequest request) {
		return "success";
	}
}
