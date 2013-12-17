package com.hongqiang.shop.modules.shipping.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Filter;
import com.hongqiang.shop.common.utils.Order;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.DeliveryCorp;

public abstract interface DeliveryCorpDao extends DeliveryCorpDaoCustom, CrudRepository<DeliveryCorp, Long> {

}

/**
 * DAO自定义接口
 * 
 * @author Jack
 * 
 */
interface DeliveryCorpDaoCustom extends BaseDao<DeliveryCorp> {

	public Page<DeliveryCorp>  findPage(Pageable pageable);
	
	public  List<DeliveryCorp> findList(Integer first, Integer count, List<Filter> filters, List<Order> orders);
	
	public List<DeliveryCorp> findAll();
}