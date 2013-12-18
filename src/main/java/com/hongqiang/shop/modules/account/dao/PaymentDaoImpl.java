package com.hongqiang.shop.modules.account.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Payment;

@Repository
public class PaymentDaoImpl extends BaseDaoImpl<Payment>
  implements PaymentDaoCustom
{
	@Override
	public Page<Payment>  findPage(Pageable pageable){
		Page<Payment> paymentPage = new Page<Payment>(pageable.getPageNumber(),pageable.getPageSize());
		String qlString = "select payment from Payment payment where 1=1 ";
		List<Object> parameter = new ArrayList<Object>();
		return super.findPage(paymentPage,  qlString,  parameter, pageable);
	}
}