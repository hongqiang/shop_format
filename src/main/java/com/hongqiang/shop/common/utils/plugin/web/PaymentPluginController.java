package com.hongqiang.shop.common.utils.plugin.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hongqiang.shop.common.config.Global;
import com.hongqiang.shop.common.utils.plugin.PaymentPlugin;
import com.hongqiang.shop.common.utils.plugin.service.PluginService;
import com.hongqiang.shop.common.web.BaseController;

@Controller("adminPaymentPluginController")
@RequestMapping({"${adminPath}/payment_plugin"})
public class PaymentPluginController extends BaseController
{

  @Autowired
  private PluginService pluginService;

  @RequestMapping(value={"/list"}, method=RequestMethod.GET)
  public String list(ModelMap model)
  {
    model.addAttribute("paymentPlugins", this.pluginService.getPaymentPlugins());
   List<PaymentPlugin> paymentPlugins =  this.pluginService.getPaymentPlugins();
   System.out.println(paymentPlugins.size());
    return "admin/payment_plugin/list";
  }
}