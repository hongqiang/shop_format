/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.hongqiang.shop.modules.sys.interceptor;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.hongqiang.shop.common.config.Global;
import com.hongqiang.shop.common.service.BaseService;
import com.hongqiang.shop.common.utils.SpringContextHolder;
import com.hongqiang.shop.common.utils.StringUtils;
import com.hongqiang.shop.modules.sys.dao.JLogDao;
import com.hongqiang.shop.modules.sys.entity.JLog;
import com.hongqiang.shop.modules.sys.entity.User;
import com.hongqiang.shop.modules.sys.utils.UserUtils;

/**
 * 系统拦截器
 * @author ThinkGem
 * @version 2013-6-6
 */
public class LogInterceptor extends BaseService implements HandlerInterceptor {

	private static JLogDao logDao = SpringContextHolder.getBean(JLogDao.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
			Object handler) throws Exception {
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, 
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
			Object handler, Exception ex) throws Exception {
		
		String requestRri = request.getRequestURI();
		String uriPrefix = request.getContextPath() + Global.getAdminPath();
		
		if ((StringUtils.startsWith(requestRri, uriPrefix) && (StringUtils.endsWith(requestRri, "/save")
				|| StringUtils.endsWith(requestRri, "/delete") || StringUtils.endsWith(requestRri, "/import")
				|| StringUtils.endsWith(requestRri, "/updateSort"))) || ex!=null){
		
			User user = UserUtils.getUser();
			if (user!=null && user.getId()!=null){
				
				StringBuilder params = new StringBuilder();
				int index = 0;
				for (Object param : request.getParameterMap().keySet()){ 
					params.append((index++ == 0 ? "" : "&") + param + "=");
					params.append(StringUtils.abbr(StringUtils.endsWithIgnoreCase((String)param, "password")
							? "" : request.getParameter((String)param), 100));
				}
				
				JLog log = new JLog();
				log.setType(ex == null ? JLog.TYPE_ACCESS : JLog.TYPE_EXCEPTION);
				log.setCreateBy(user);
				log.setCreateDate(new Date());
				log.setRemoteAddr(StringUtils.getRemoteAddr(request));
				log.setUserAgent(request.getHeader("user-agent"));
				log.setRequestUri(request.getRequestURI());
				log.setMethod(request.getMethod());
				log.setParams(params.toString());
				log.setException(ex != null ? ex.toString() : "");
				logDao.save(log);
				
				logger.debug("save log {type: {}, loginName: {}, uri: {}}, ", log.getType(), user.getLoginName(), log.getRequestUri());
				
			}
		}
		
//		logger.debug("最大内存: {}, 已分配内存: {}, 已分配内存中的剩余空间: {}, 最大可用内存: {}", 
//				Runtime.getRuntime().maxMemory(), Runtime.getRuntime().totalMemory(), Runtime.getRuntime().freeMemory(), 
//				Runtime.getRuntime().maxMemory()-Runtime.getRuntime().totalMemory()+Runtime.getRuntime().freeMemory()); 
		
	}

}
