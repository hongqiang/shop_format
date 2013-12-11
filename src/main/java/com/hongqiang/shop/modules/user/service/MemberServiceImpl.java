package com.hongqiang.shop.modules.user.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.LockModeType;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.service.BaseService;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.common.utils.Setting;
import com.hongqiang.shop.common.utils.SettingUtils;
import com.hongqiang.shop.modules.entity.Admin;
import com.hongqiang.shop.modules.entity.Deposit;
import com.hongqiang.shop.modules.entity.Member;
import com.hongqiang.shop.modules.sys.security.SystemAuthorizingRealm.Principal;
import com.hongqiang.shop.modules.user.dao.DepositDao;
import com.hongqiang.shop.modules.user.dao.MemberDao;

@Service
public class MemberServiceImpl extends BaseService
  implements MemberService
{

  @Autowired
  private MemberDao memberDao;

  @Autowired
  private DepositDao depositDao;

  @Transactional(readOnly=true)
  public boolean usernameExists(String username)
  {
    return this.memberDao.usernameExists(username);
  }

  @Transactional(readOnly=true)
  public boolean usernameDisabled(String username)
  {
    Assert.hasText(username);
    Setting localSetting = SettingUtils.get();
    if (localSetting.getDisabledUsernames() != null)
      for (String str : localSetting.getDisabledUsernames())
        if (StringUtils.containsIgnoreCase(username, str))
          return true;
    return false;
  }

  @Transactional(readOnly=true)
  public boolean emailExists(String email)
  {
    return this.memberDao.emailExists(email);
  }

  @Transactional(readOnly=true)
  public boolean emailUnique(String previousEmail, String currentEmail)
  {
    if (StringUtils.equalsIgnoreCase(previousEmail, currentEmail))
      return true;
    return !this.memberDao.emailExists(currentEmail);
  }

  public void save(Member member, Admin operator)
  {
    Assert.notNull(member);
    this.memberDao.persist(member);
    if (member.getBalance().compareTo(new BigDecimal(0)) > 0)
    {
      Deposit localDeposit = new Deposit();
      localDeposit.setType(operator != null ? Deposit.Type.adminRecharge : Deposit.Type.memberRecharge);
      localDeposit.setCredit(member.getBalance());
      localDeposit.setDebit(new BigDecimal(0));
      localDeposit.setBalance(member.getBalance());
      localDeposit.setOperator(operator != null ? operator.getUsername() : null);
      localDeposit.setMember(member);
      this.depositDao.persist(localDeposit);
    }
  }

  public void update(Member member, Integer modifyPoint, BigDecimal modifyBalance, String depositMemo, Admin operator)
  {
    Assert.notNull(member);
    this.memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);
    if ((modifyPoint != null) && (modifyPoint.intValue() != 0) && (member.getPoint().longValue() + modifyPoint.intValue() >= 0L))
      member.setPoint(Long.valueOf(member.getPoint().longValue() + modifyPoint.intValue()));
    if ((modifyBalance != null) && (modifyBalance.compareTo(new BigDecimal(0)) != 0) && (member.getBalance().add(modifyBalance).compareTo(new BigDecimal(0)) >= 0))
    {
      member.setBalance(member.getBalance().add(modifyBalance));
      Deposit localDeposit = new Deposit();
      if (modifyBalance.compareTo(new BigDecimal(0)) > 0)
      {
        localDeposit.setType(operator != null ? Deposit.Type.adminRecharge : Deposit.Type.memberRecharge);
        localDeposit.setCredit(modifyBalance);
        localDeposit.setDebit(new BigDecimal(0));
      }
      else
      {
        localDeposit.setType(operator != null ? Deposit.Type.adminChargeback : Deposit.Type.memberPayment);
        localDeposit.setCredit(new BigDecimal(0));
        localDeposit.setDebit(modifyBalance);
      }
      localDeposit.setBalance(member.getBalance());
      localDeposit.setOperator(operator != null ? operator.getUsername() : null);
      localDeposit.setMemo(depositMemo);
      localDeposit.setMember(member);
      this.depositDao.persist(localDeposit);
    }
    this.memberDao.merge(member);
  }

  @Transactional(readOnly=true)
   public Member find(Long id){
	return this.memberDao.findById(id);
   }

   @Transactional(readOnly=true)
  public Page<Member> findPage(Pageable pageable){
	return this.memberDao.findPage(pageable);
  }
  
  @Transactional(readOnly=true)
  public Member findByUsername(String username)
  {
    return this.memberDao.findByUsername(username);
  }

  @Transactional(readOnly=true)
  public List<Member> findListByEmail(String email)
  {
    return this.memberDao.findListByEmail(email);
  }

  @Transactional(readOnly=true)
  public Page<Member> findPurchasePage(Date beginDate, Date endDate, Pageable pageable)
  {
    return this.memberDao.findPurchasePage(beginDate, endDate, pageable);
  }

  @Transactional(readOnly=true)
  public boolean isAuthenticated()
  {
    RequestAttributes localRequestAttributes = RequestContextHolder.currentRequestAttributes();
    if (localRequestAttributes != null)
    {
      HttpServletRequest localHttpServletRequest = ((ServletRequestAttributes)localRequestAttributes).getRequest();
      Principal localPrincipal = (Principal)localHttpServletRequest.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
      if (localPrincipal != null)
        return true;
    }
    return false;
  }

  @Transactional(readOnly=true)
  public Member getCurrent()
  {
    RequestAttributes localRequestAttributes = RequestContextHolder.currentRequestAttributes();
    if (localRequestAttributes != null)
    {
      HttpServletRequest localHttpServletRequest = ((ServletRequestAttributes)localRequestAttributes).getRequest();
      Principal localPrincipal = (Principal)localHttpServletRequest.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
      if (localPrincipal != null)
        return (Member)this.memberDao.findById(localPrincipal.getId());
    }
    return null;
  }

  @Transactional(readOnly=true)
  public String getCurrentUsername()
  {
    RequestAttributes localRequestAttributes = RequestContextHolder.currentRequestAttributes();
    if (localRequestAttributes != null)
    {
      HttpServletRequest localHttpServletRequest = ((ServletRequestAttributes)localRequestAttributes).getRequest();
      Principal localPrincipal = (Principal)localHttpServletRequest.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
      if (localPrincipal != null)
        return localPrincipal.getUsername();
    }
    return null;
  }
  
  @Transactional(readOnly=true)
  public void delete(Long id){
	this.memberDao.delete(id);
  }

  @Transactional(readOnly=true)
	public void delete(Long[] ids){
	if (ids != null)
			for (Long localSerializable : ids)
				this.memberDao.delete(localSerializable);
  }

  @Transactional(readOnly=true)
	public void delete(Member member){
	this.memberDao.delete(member);
  }
}
