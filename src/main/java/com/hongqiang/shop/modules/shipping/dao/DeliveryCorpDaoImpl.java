package com.hongqiang.shop.modules.shipping.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Filter;
import com.hongqiang.shop.common.utils.Order;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.DeliveryCorp;

@Repository
public class DeliveryCorpDaoImpl extends BaseDaoImpl<DeliveryCorp>
  implements DeliveryCorpDaoCustom{
  
  @Override
	public Page<DeliveryCorp>  findPage(Pageable pageable){
		Page<DeliveryCorp> deliveryCorpPage = new Page<DeliveryCorp>(pageable.getPageNumber(),pageable.getPageSize());
		String qlString = "select deliveryCorp from DeliveryCorp deliveryCorp where 1=1 ";
		List<Object> parameter = new ArrayList<Object>();
		return super.findPage(deliveryCorpPage,  qlString,  parameter, pageable);
	}
	
	@Override
	public  List<DeliveryCorp> findList(Integer first, Integer count, List<Filter> filters, List<Order> orders){
		String qlString = "select deliveryCorp from DeliveryCorp deliveryCorp where 1=1 ";
		List<Object> parameter = new ArrayList<Object>();
		return super.findList(qlString, parameter, first, count, filters, orders);
	}
	
	@Override
	public List<DeliveryCorp> findAll(){
		return findList(null, null, null, null);
	}
}