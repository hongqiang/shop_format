package com.hongqiang.shop.modules.user.service;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.service.BaseService;
import com.hongqiang.shop.common.utils.Filter;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.MemberRank;
import com.hongqiang.shop.modules.user.dao.MemberRankDao;

@Service
public class MemberRankServiceImpl extends BaseService
  implements MemberRankService
{

  @Autowired
  private MemberRankDao memberRankDao;

  @Transactional(readOnly=true)
  public boolean nameExists(String name)
  {
    return this.memberRankDao.nameExists(name);
  }

  @Transactional(readOnly=true)
  public boolean nameUnique(String previousName, String currentName)
  {
    if (StringUtils.equalsIgnoreCase(previousName, currentName))
      return true;
    return !this.memberRankDao.nameExists(currentName);
  }

  @Transactional(readOnly=true)
  public boolean amountExists(BigDecimal amount)
  {
    return this.memberRankDao.amountExists(amount);
  }

  @Transactional(readOnly=true)
  public boolean amountUnique(BigDecimal previousAmount, BigDecimal currentAmount)
  {
    if ((previousAmount != null) && (previousAmount.compareTo(currentAmount) == 0))
      return true;
    return !this.memberRankDao.amountExists(currentAmount);
  }

  @Transactional(readOnly=true)
  public MemberRank findDefault()
  {
    return this.memberRankDao.findDefault();
  }

  @Transactional(readOnly=true)
  public MemberRank findByAmount(BigDecimal amount)
  {
    return this.memberRankDao.findByAmount(amount);
  }
  
  @Transactional(readOnly=true)
  public MemberRank find(Long id){
	return this.memberRankDao.findById(id);
  }

  @Transactional(readOnly=true)
  public Page<MemberRank> findPage(Pageable pageable){
  return this.memberRankDao.findPage(pageable);
  }
  
  @Transactional(readOnly=true)
  public void save(MemberRank memberRank){
  this.memberRankDao.persist(memberRank);
  }
  
  @Transactional(readOnly=true)
  	public void delete(Long id){
  MemberRank memberRank = find(id);
  this.memberRankDao.remove(memberRank);
  }

	@Transactional(readOnly=true)
	public void delete(Long[] ids){
  if (ids != null)
			for (Long localSerializable : ids)
				this.memberRankDao.delete(localSerializable);
  }

	@Transactional(readOnly=true)
	public void delete(MemberRank memberRank){
  this.memberRankDao.remove(memberRank);
  }
	
	@Transactional(readOnly=true)
	public MemberRank update(MemberRank memberRank){
  return (MemberRank)this.memberRankDao.merge(memberRank);
  }
	
//	@Transactional(readOnly=true)
//	public long count(){
//  return count(new Filter[0]);
//  }
  
}