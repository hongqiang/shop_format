package com.hongqiang.shop.modules.user.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Deposit;
import com.hongqiang.shop.modules.entity.Member;

@Repository
public class DepositDaoImpl extends BaseDaoImpl<Deposit>
  implements DepositDaoCustom
{
	@Override
  public Page<Deposit> findPage(Member member, Pageable pageable)
  {
    if (member == null){
		return new Page<Deposit>(0,0);
	}
	Page<Deposit> depositPage = new Page<Deposit>(pageable.getPageNumber(),pageable.getPageSize());
	String sqlString = "select deposit from Deposit deposit where deposit.member = ?";
	List<Object> params = new ArrayList<Object>();
	params.add(member);
	return super.findPage(depositPage,  sqlString,  params, pageable);
  }
  
  @Override
  public Page<Deposit> findPage(Pageable pageable){
	Page<Deposit> depositPage = new Page<Deposit>(pageable.getPageNumber(),pageable.getPageSize());
		String sqlString = "select deposit from Deposit deposit";
		List<Object> params = new ArrayList<Object>();
		return super.findPage(depositPage,  sqlString,  params, pageable);
  }
}