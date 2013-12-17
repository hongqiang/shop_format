package com.hongqiang.shop.modules.shipping.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Refunds;

@Repository
public class RefundsDaoImpl extends BaseDaoImpl<Refunds>
  implements RefundsDaoCustom{
	
	@Override
	public Page<Refunds>  findPage(Pageable pageable){
		Page<Refunds> refundsPage = new Page<Refunds>(pageable.getPageNumber(),pageable.getPageSize());
		String qlString = "select refunds from Refunds refunds where 1=1 ";
		List<Object> parameter = new ArrayList<Object>();
		return super.findPage(refundsPage,  qlString,  parameter, pageable);
	}
}