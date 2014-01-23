package com.hongqiang.shop.modules.user.web.shop;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongqiang.shop.common.utils.CookieUtils;
import com.hongqiang.shop.common.utils.Message;
import com.hongqiang.shop.common.utils.Principal;
import com.hongqiang.shop.common.utils.Setting;
import com.hongqiang.shop.common.utils.SettingUtils;
import com.hongqiang.shop.common.web.BaseController;
import com.hongqiang.shop.modules.account.service.CartService;
import com.hongqiang.shop.modules.entity.Cart;
import com.hongqiang.shop.modules.entity.Member;
import com.hongqiang.shop.modules.user.service.MemberService;
import com.hongqiang.shop.modules.util.service.CaptchaService;
import com.hongqiang.shop.modules.util.service.RSAService;

@Controller("shopLoginController")
@RequestMapping({ "${frontPath}/login" })
public class LoginController extends BaseController {

	@Autowired
	private CaptchaService captchaService;

	@Autowired
	private RSAService rsaService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private CartService cartService;

	@RequestMapping(value = { "/check" }, method = RequestMethod.GET)
	@ResponseBody
	public Boolean check() {
		return Boolean.valueOf(this.memberService.isAuthenticated());
	}

	@RequestMapping(method = RequestMethod.GET)
	public String index(String redirectUrl, HttpServletRequest request,
			ModelMap model) {
		Setting localSetting = SettingUtils.get();
		if ((redirectUrl != null)
				&& (!redirectUrl.equalsIgnoreCase(localSetting.getSiteUrl()))
				&& (!redirectUrl.startsWith(request.getContextPath() + "/"))
				&& (!redirectUrl.startsWith(localSetting.getSiteUrl() + "/")))
			redirectUrl = null;
		model.addAttribute("redirectUrl", redirectUrl);
		model.addAttribute("captchaId", UUID.randomUUID().toString());
		return "/shop/login/index";
	}

	@RequestMapping(value = { "/submit" }, method =RequestMethod.POST)
	@ResponseBody
	public Message submit(String captchaId, String captcha, String username,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		String str = this.rsaService.decryptParameter("enPassword", request);
		this.rsaService.removePrivateKey(request);
		if (!this.captchaService.isValid(Setting.CaptchaType.memberLogin,
				captchaId, captcha))
			return Message.error("shop.captcha.invalid", new Object[0]);
		if ((StringUtils.isEmpty(username)) || (StringUtils.isEmpty(str)))
			return Message.error("shop.common.invalid", new Object[0]);
		Setting localSetting = SettingUtils.get();
		Member localMember;
		if ((localSetting.getIsEmailLogin().booleanValue())
				&& (username.contains("@"))) {
			List<Member> localList = this.memberService.findListByEmail(username);
			if (localList.isEmpty())
				localMember = null;
			else if (localList.size() == 1)
				localMember = (Member) localList.get(0);
			else
				return Message.error("shop.login.unsupportedAccount",
						new Object[0]);
		} else {
			localMember = this.memberService.findByUsername(username);
		}
		if (localMember == null)
			return Message.error("shop.login.unknownAccount", new Object[0]);
		if (!localMember.getIsEnabled().booleanValue())
			return Message.error("shop.login.disabledAccount", new Object[0]);
		int i=0;
		if (localMember.getIsLocked().booleanValue()){
			if (ArrayUtils.contains(localSetting.getAccountLockTypes(),
					Setting.AccountLockType.member)) {
				i = localSetting.getAccountLockTime().intValue();
				if (i == 0)
					return Message.error("shop.login.lockedAccount",
							new Object[0]);
				Date lockedDate = localMember.getLockedDate();
				Date lockingDate = DateUtils.addMinutes((Date) lockedDate, i);
				if (new Date().after(lockingDate)) {
					localMember.setLoginFailureCount(Integer.valueOf(0));
					localMember.setIsLocked(Boolean.valueOf(false));
					localMember.setLockedDate(null);
					this.memberService.update(localMember);
				} else {
					return Message.error("shop.login.lockedAccount",
							new Object[0]);
				}
			} else {
				localMember.setLoginFailureCount(Integer.valueOf(0));
				localMember.setIsLocked(Boolean.valueOf(false));
				localMember.setLockedDate(null);
				this.memberService.update(localMember);
			}
		}
		if (!DigestUtils.md5Hex(str).equals(localMember.getPassword())) {
			i = localMember.getLoginFailureCount().intValue() + 1;
			if (i >= localSetting.getAccountLockCount().intValue()) {
				localMember.setIsLocked(Boolean.valueOf(true));
				localMember.setLockedDate(new Date());
			}
			localMember.setLoginFailureCount(Integer.valueOf(i));
			this.memberService.update(localMember);
			if (ArrayUtils.contains(localSetting.getAccountLockTypes(),
					Setting.AccountLockType.member))
				return Message.error("shop.login.accountLockCount",
						new Object[] { localSetting.getAccountLockCount() });
			return Message.error("shop.login.incorrectCredentials",
					new Object[0]);
		}
		localMember.setLoginIp(request.getRemoteAddr());
		localMember.setLoginDate(new Date());
		localMember.setLoginFailureCount(Integer.valueOf(0));
		this.memberService.update(localMember);
		System.out.println("login here");
		Cart localCart = this.cartService.getCurrent();
		if ((localCart != null) && (localCart.getMember() == null)) {
			this.cartService.merge(localMember, localCart);
			CookieUtils.removeCookie(request, response, "cartId");
			CookieUtils.removeCookie(request, response, "cartKey");
		}
		Map<String, Object> localMap = new HashMap<String, Object>();
		@SuppressWarnings("rawtypes")
		Enumeration enumer = session.getAttributeNames();
		while (enumer.hasMoreElements()) {
			String attribute = (String) enumer.nextElement();
			 localMap.put(attribute,session.getAttribute(attribute));
		}
		session.invalidate();
		session = request.getSession();
		Iterator<Entry<String, Object>> localIterator = localMap.entrySet().iterator();
		while (localIterator.hasNext()) {
			Entry<String, Object> pairs = (Entry<String, Object>) localIterator.next();
			session.setAttribute((String)pairs.getKey(),pairs.getValue());
		}
		session.setAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME, new Principal(
				localMember.getId(), username));
		CookieUtils.setCookie(request, response, "username",
				localMember.getUsername());
		System.out.println("we submit, message = "+SHOP_SUCCESS.toString());
		
		return  SHOP_SUCCESS;
	}
	
	@RequestMapping(value = { "/tt" }, method = RequestMethod.GET)
	public void test() {
		System.out.println("we are here.");
		Member member = this.memberService.find(11L);
		System.out.println("member="+member.getUsername()+", "+member.getEmail()+" ,"+member.getName());
		member.setEmail("liamn@163.com");
		member.setName("liman");
		this.memberService.update(member);
		Member member2 = this.memberService.find(11L);
		System.out.println("member="+member2.getUsername()+", "+member2.getEmail()+" ,"+member2.getName());
	}
}