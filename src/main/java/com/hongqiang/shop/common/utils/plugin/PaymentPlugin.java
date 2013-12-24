package com.hongqiang.shop.common.utils.plugin;

import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hongqiang.shop.common.utils.Setting;
import com.hongqiang.shop.common.utils.SettingUtils;
import com.hongqiang.shop.common.utils.plugin.service.PluginConfigService;
import com.hongqiang.shop.website.entity.PluginConfig;

public abstract class PaymentPlugin implements Comparable<PaymentPlugin> {
	public enum FeeType {
		scale, fixed;
	}

	public enum Method {
		post, get;
	}

	public static final String PAYMENT_NAME_ATTRIBUTE_NAME = "paymentName";
	public static final String FEE_TYPE_ATTRIBUTE_NAME = "feeType";
	public static final String FEE_ATTRIBUTE_NAME = "fee";
	public static final String LOGO_ATTRIBUTE_NAME = "logo";
	public static final String DESCRIPTION_ATTRIBUTE_NAME = "description";

	@Autowired
	private PluginConfigService pluginConfigService;

	public final String getId() {
		return ((Component) getClass().getAnnotation(Component.class)).value();
	}

	public abstract String getName();

	public abstract String getVersion();

	public abstract String getAuthor();

	public abstract String getSiteUrl();

	public abstract String getInstallUrl();

	public abstract String getUninstallUrl();

	public abstract String getSettingUrl();

	public boolean getIsInstalled() {
		return this.pluginConfigService.pluginIdExists(getId());
	}

	public PluginConfig getPluginConfig() {
		return this.pluginConfigService.findByPluginId(getId());
	}

	public boolean getIsEnabled() {
		PluginConfig localPluginConfig = getPluginConfig();
		return localPluginConfig != null ? localPluginConfig.getIsEnabled()
				.booleanValue() : false;
	}

	public String getAttribute(String name) {
		PluginConfig localPluginConfig = getPluginConfig();
		return localPluginConfig != null ? localPluginConfig.getAttribute(name)
				: null;
	}

	public Integer getOrder() {
		PluginConfig localPluginConfig = getPluginConfig();
		return localPluginConfig != null ? localPluginConfig.getOrder() : null;
	}

	public String getPaymentName() {
		PluginConfig localPluginConfig = getPluginConfig();
		return localPluginConfig != null ? localPluginConfig
				.getAttribute(PAYMENT_NAME_ATTRIBUTE_NAME) : null;
	}

	public FeeType getFeeType() {
		PluginConfig localPluginConfig = getPluginConfig();
		return localPluginConfig != null ? FeeType.valueOf(localPluginConfig
				.getAttribute(FEE_TYPE_ATTRIBUTE_NAME)) : null;
	}

	public BigDecimal getFee() {
		PluginConfig localPluginConfig = getPluginConfig();
		return localPluginConfig != null ? new BigDecimal(
				localPluginConfig.getAttribute(FEE_ATTRIBUTE_NAME)) : null;
	}

	public String getLogo() {
		PluginConfig localPluginConfig = getPluginConfig();
		return localPluginConfig != null ? localPluginConfig
				.getAttribute(LOGO_ATTRIBUTE_NAME) : null;
	}

	public String getDescription() {
		PluginConfig localPluginConfig = getPluginConfig();
		return localPluginConfig != null ? localPluginConfig
				.getAttribute(DESCRIPTION_ATTRIBUTE_NAME) : null;
	}

	public abstract String getUrl();

	public abstract Method getMethod();

	public abstract Integer getTimeout();

	public abstract Map<String, String> getParameterMap(String paramString1,
			BigDecimal paramBigDecimal, String paramString2,
			HttpServletRequest paramHttpServletRequest);

	public abstract boolean verify(String paramString,
			HttpServletRequest paramHttpServletRequest);

	public abstract BigDecimal getAmount(String paramString,
			HttpServletRequest paramHttpServletRequest);

	public abstract String getNotifyContext(String paramString,
			HttpServletRequest paramHttpServletRequest);

	protected String getReturnUrl(String paramString) {
		Setting localSetting = SettingUtils.get();
		return localSetting.getSiteUrl() + "/payment/return/" + paramString
				+ ".jhtml";
	}

	protected String getNotifyUrl(String paramString) {
		Setting localSetting = SettingUtils.get();
		return localSetting.getSiteUrl() + "/payment/notify/" + paramString
				+ ".jhtml";
	}

	public final BigDecimal getFee(BigDecimal amount) {
		Setting localSetting = SettingUtils.get();
		BigDecimal localBigDecimal;
		if (getFeeType() == FeeType.scale)
			localBigDecimal = amount.multiply(getFee());
		else
			localBigDecimal = getFee();
		return localSetting.setScale(localBigDecimal);
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (this == obj)
			return true;
		PaymentPlugin localPaymentPlugin = (PaymentPlugin) obj;
		return new EqualsBuilder().append(getId(), localPaymentPlugin.getId())
				.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(getId()).toHashCode();
	}

	public int compareTo(PaymentPlugin paymentPlugin) {
		return new CompareToBuilder()
				.append(getOrder(), paymentPlugin.getOrder())
				.append(getId(), paymentPlugin.getId()).toComparison();
	}
}