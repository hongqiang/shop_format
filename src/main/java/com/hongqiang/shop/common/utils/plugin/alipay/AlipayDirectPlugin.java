package com.hongqiang.shop.common.utils.plugin.alipay;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.hongqiang.shop.common.utils.plugin.PaymentPlugin;

@Component("alipayDirectPlugin")
public class AlipayDirectPlugin extends PaymentPlugin
{
  public String getName()
  {
    return "支付宝即时交易";
  }

  public String getVersion()
  {
    return "1.0";
  }

  public String getAuthor()
  {
    return "Alipay";
  }

  public String getSiteUrl()
  {
    return "http://www.alipay.com";
  }

  public String getInstallUrl()
  {
    return "alipay_direct/install.jhtml";
  }

  public String getUninstallUrl()
  {
    return "alipay_direct/uninstall.jhtml";
  }

  public String getSettingUrl()
  {
    return "alipay_direct/setting.jhtml";
  }

  public String getUrl()
  {
    return "https://mapi.alipay.com/gateway.do";
  }

  public PaymentPlugin.Method getMethod()
  {
    return PaymentPlugin.Method.get;
  }

  public Integer getTimeout()
  {
    return Integer.valueOf(21600);
  }

  public Map<String, String> getParameterMap(String sn, BigDecimal amount, String description, HttpServletRequest request)
  {
    return new HashMap<String, String>();
  }

  public boolean verify(String sn, HttpServletRequest request)
  {
    return false;
  }

  public BigDecimal getAmount(String sn, HttpServletRequest request)
  {
    return new BigDecimal(request.getParameter("total_fee"));
  }

  public String getNotifyContext(String sn, HttpServletRequest request)
  {
    return "success";
  }
}