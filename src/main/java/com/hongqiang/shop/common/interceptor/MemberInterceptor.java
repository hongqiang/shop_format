package com.hongqiang.shop.common.interceptor;

import java.io.IOException;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.hongqiang.shop.modules.entity.Member;
import com.hongqiang.shop.modules.sys.security.SystemAuthorizingRealm.Principal;
import com.hongqiang.shop.modules.user.service.MemberService;

public class MemberInterceptor extends HandlerInterceptorAdapter
{
  private static final String REDIRECT = "redirect:";
  private static final String REDIRECT_URL = "redirectUrl";
  private static final String MEMBER = "member";
  private static final String LOGIN_URL = "/login.jhtml";
  private String loginUrl = LOGIN_URL;

  @Value("${url_escaping_charset}")
  private String url_escaping_charset;

  @Resource(name="memberServiceImpl")
  private MemberService memberService;

  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException
  {
    HttpSession localHttpSession = request.getSession();
    Principal localPrincipal = (Principal)localHttpSession.getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
    if (localPrincipal != null)
      return true;
    String str1 = request.getHeader("X-Requested-With");
    if ((str1 != null) && (str1.equalsIgnoreCase("XMLHttpRequest")))
    {
      response.addHeader("loginStatus", "accessDenied");
      response.sendError(403);
      return false;
    }
    if (request.getMethod().equalsIgnoreCase("GET"))
    {
      String str2 = request.getQueryString() != null ? request.getRequestURI() + "?" + request.getQueryString() : request.getRequestURI();
      response.sendRedirect(request.getContextPath() + this.loginUrl + "?" + REDIRECT_URL + "=" + URLEncoder.encode(str2, this.url_escaping_charset));
    }
    else
    {
      response.sendRedirect(request.getContextPath() + this.loginUrl);
    }
    return false;
  }

  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
  {
    if (modelAndView != null)
    {
      String str = modelAndView.getViewName();
      if (!StringUtils.startsWith(str, REDIRECT))
        modelAndView.addObject(MEMBER, this.memberService.getCurrent());
    }
  }

  public String getLoginUrl()
  {
    return this.loginUrl;
  }

  public void setLoginUrl(String loginUrl)
  {
    this.loginUrl = loginUrl;
  }
}