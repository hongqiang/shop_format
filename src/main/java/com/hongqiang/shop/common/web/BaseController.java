/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.hongqiang.shop.common.web;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hongqiang.shop.common.beanvalidator.BeanValidators;
import com.hongqiang.shop.common.template.FlashMessageDirective;
import com.hongqiang.shop.common.utils.DateUtils;
import com.hongqiang.shop.common.utils.Message;
import com.hongqiang.shop.common.utils.SpringContextHolder;
import com.hongqiang.shop.modules.sys.entity.Log;


/**
 * 控制器支持类
 * @author ThinkGem
 * @version 2013-3-23
 */
public abstract class BaseController {
	
	 protected static final String ERROR_PAGE = "/admin/common/error";
//	  protected static final Message ADMIN_ERROR = Message.error("admin.message.error", new Object[0]);
//	  protected static final Message ADMIN_SUCCESS = Message.success("admin.message.success", new Object[0]);
	  private static final String CONSTRAINT_VIOLATIONS = "constraintViolations";//违反约束的集合
	
	/**
	 * 验证Bean实例对象
	 */
	@Autowired
	protected Validator validator;

	/**
	 * 服务端参数有效性验证
	 * @param object 验证的实体对象
	 * @param groups 验证组
	 * @return 验证成功：返回true；严重失败：将错误信息添加到 message 中
	 */
	protected boolean beanValidator(Model model, Object object, Class<?>... groups) {
		try{
			BeanValidators.validateWithException(validator, object, groups);
		}catch(ConstraintViolationException ex){
			List<String> list = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
			list.add(0, "数据验证失败：");
			addMessage(model, list.toArray(new String[]{}));
			return false;
		}
		return true;
	}
	
	/**
	 * 服务端参数有效性验证
	 * @param object 验证的实体对象
	 * @param groups 验证组
	 * @return 验证成功：返回true；严重失败：将错误信息添加到 flash message 中
	 */
	protected boolean beanValidator(RedirectAttributes redirectAttributes, Object object, Class<?>... groups) {
		try{
			BeanValidators.validateWithException(validator, object, groups);
		}catch(ConstraintViolationException ex){
			List<String> list = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
			list.add(0, "数据验证失败：");
			addMessage(redirectAttributes, list.toArray(new String[]{}));
			return false;
		}
		return true;
	}
	
	/**
	 * 添加Model消息
	 * @param message
	 */
	protected void addMessage(Model model, String... messages) {
		StringBuilder sb = new StringBuilder();
		for (String message : messages){
			sb.append(message).append(messages.length>1?"<br/>":"");
		}
		model.addAttribute("message", sb.toString());
	}
	
	/**
	 * 添加Flash消息
	 * @param message
	 */
	protected void addMessage(RedirectAttributes redirectAttributes, String... messages) {
		StringBuilder sb = new StringBuilder();
		for (String message : messages){
			sb.append(message).append(messages.length>1?"<br/>":"");
		}
		redirectAttributes.addFlashAttribute("message", sb.toString());
	}
	
	/**
	 * 添加Flash消息-shop
	 * @param message
	 */
	protected void addMessage(RedirectAttributes redirectAttributes, Message messages) {
		if ((redirectAttributes != null) && (messages != null))
			redirectAttributes.addFlashAttribute(FlashMessageDirective.FLASH_MESSAGE_ATTRIBUTE_NAME, messages);
	}
	
//	 protected String addMessage(BigDecimal paramBigDecimal, boolean paramBoolean1, boolean paramBoolean2)
//	  {
//	    Setting localSetting = SettingUtils.get();
//	    String str = localSetting.setScale(paramBigDecimal).toString();
//	    if (paramBoolean1)
//	      str = localSetting.getCurrencySign() + str;
//	    if (paramBoolean2)
//	      str = str + localSetting.getCurrencyUnit();
//	    return str;
//	  }

//	  protected String addMessage(String paramString, Object[] paramArrayOfObject)
//	  {
//	    return SpringContextHolder.getMessage(paramString, paramArrayOfObject);
//	  }
	
	  protected void addMessage(String paramString)
	  {
	    if (paramString != null)
	    {
	      RequestAttributes localRequestAttributes = RequestContextHolder.currentRequestAttributes();
	      localRequestAttributes.setAttribute(Log.LOG_CONTENT_ATTRIBUTE_NAME, paramString, 0);
	    }
	  }
	  
	/**
	 * 初始化数据绑定
	 * 1. 将所有传递进来的String进行HTML编码，防止XSS攻击
	 * 2. 将字段中Date类型转换为String类型
	 */
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		// String类型转换，将所有传递进来的String进行HTML编码，防止XSS攻击
		binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(text == null ? null : StringEscapeUtils.escapeHtml4(text.trim()));
			}
			@Override
			public String getAsText() {
				Object value = getValue();
				return value != null ? value.toString() : "";
			}
		});
		// Date 类型转换
		binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(DateUtils.parseDate(text));
			}
		});
	}
	
}
