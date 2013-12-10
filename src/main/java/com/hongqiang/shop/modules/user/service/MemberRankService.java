package com.hongqiang.shop.modules.user.service;

import java.math.BigDecimal;

import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.MemberRank;

public interface MemberRankService{
  public  boolean nameExists(String paramString);

  public  boolean nameUnique(String paramString1, String paramString2);

  public  boolean amountExists(BigDecimal paramBigDecimal);

  public  boolean amountUnique(BigDecimal paramBigDecimal1, BigDecimal paramBigDecimal2);

  public  MemberRank findDefault();

  public  MemberRank findByAmount(BigDecimal paramBigDecimal);
  
  public MemberRank find(Long id); 

  public Page<MemberRank> findPage(Pageable pageable);
  
  public void save(MemberRank memberRank);
  
  	public void delete(Long id);

	public void delete(Long[] ids);

	public void delete(MemberRank memberRank);
	
	public MemberRank update(MemberRank memberRank);
	
//	public long count();
}
