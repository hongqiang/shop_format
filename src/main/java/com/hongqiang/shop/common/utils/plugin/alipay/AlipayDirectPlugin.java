package com.hongqiang.shop.common.utils.plugin.alipay;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hongqiang.shop.common.utils.StringUtils;
import com.hongqiang.shop.common.utils.plugin.PaymentPlugin;
import com.hongqiang.shop.common.utils.plugin.service.PluginConfigService;
import com.hongqiang.shop.website.entity.PluginConfig;

@Component("alipayDirectPlugin")
public class AlipayDirectPlugin extends PaymentPlugin {
	@Autowired
	private PluginConfigService pluginConfigService;

	public String getName() {
		return "支付宝即时交易";
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
		return "alipay_direct/install.jhtml";
	}

	public String getUninstallUrl() {
		return "alipay_direct/uninstall.jhtml";
	}

	public String getSettingUrl() {
		return "alipay_direct/setting.jhtml";
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

	public Map<String, String> getParameterMap(String sn, BigDecimal amount,
			String description, HttpServletRequest request) {
		BigDecimal price = amount.setScale(2,  BigDecimal.ROUND_HALF_UP);
		Map<String, String> paramsMap = new HashMap<String, String>();
		// 支付类型
		String payment_type = "1";
		// 服务器异步通知页面路径
		String notify_url = "http://www.xxx.com/create_partner_trade_by_buyer-JAVA-UTF-8/notify_url.jsp";
		// 需http://格式的完整路径，不能加?id=123这类自定义参数

		// 页面跳转同步通知页面路径
		String return_url = "http://www.xxx.com/create_partner_trade_by_buyer-JAVA-UTF-8/return_url.jsp";
		// 需http://格式的完整路径，不能加?id=123这类自定义参数，不能写成http://localhost/
		// 订单名称
		String subject = "testForShopHQ";
		// 商品数量
		String quantity = "1";
		// 必填，建议默认为1，不改变值，把一次交易看成是一次下订单而非购买一件商品
		// 物流费用
		String logistics_fee = "0.00";
		// 必填，即运费
		// 物流类型
		String logistics_type = "EXPRESS";
		// 必填，三个值可选：EXPRESS（快递）、POST（平邮）、EMS（EMS）
		// 物流支付方式
		String logistics_payment = "SELLER_PAY";
		// 必填，两个值可选：SELLER_PAY（卖家承担运费）、BUYER_PAY（买家承担运费）
		String input_charset = "utf-8";
		// 订单描述
		if (getIsEnabled()) {
			PluginConfig pluginConfig = getPluginConfig();
			String key = (pluginConfig != null) ? pluginConfig
					.getAttribute("key") : null;
			String partner = (pluginConfig != null) ? pluginConfig
					.getAttribute("partner") : null;
			String seller_email = (pluginConfig != null) ? pluginConfig
					.getAttribute("seller_email") : null;
			paramsMap.put("out_trade_no", sn);
			paramsMap.put("price", price.toString());
			paramsMap.put("_input_charset", input_charset);
			paramsMap.put("partner", partner);
			paramsMap.put("seller_email", seller_email);
			paramsMap.put("notify_url", notify_url);
			paramsMap.put("return_url", return_url);
			paramsMap.put("service", "create_partner_trade_by_buyer");
			paramsMap.put("payment_type", payment_type);
			paramsMap.put("subject", subject);
			paramsMap.put("quantity", quantity);
			paramsMap.put("logistics_fee", logistics_fee);
			paramsMap.put("logistics_type", logistics_type);
			paramsMap.put("logistics_payment", logistics_payment);
			System.out.println(paramsMap);
			String mysign = AlipayUtils.buildRequestMysign(paramsMap, key, input_charset);
			paramsMap.put("sign", mysign);
			paramsMap.put("sign_type", "MD5");
		}
		System.out.println(paramsMap);
		return paramsMap;
	}

	public boolean verify(String sn, HttpServletRequest request) {
		return false;
	}

	public BigDecimal getAmount(String sn, HttpServletRequest request) {
		return new BigDecimal(request.getParameter("total_fee"));
	}

	public String getNotifyContext(String sn, HttpServletRequest request) {
		return "success";
	}
}