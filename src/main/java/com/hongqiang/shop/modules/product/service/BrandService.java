package com.hongqiang.shop.modules.product.service;

import java.util.List;

import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Filter;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Order;
import com.hongqiang.shop.modules.entity.Brand;

public abstract interface BrandService 
{
	public Brand find(Long id);
	
//	public Page<Brand[]> findPage(Page<Brand[]> pageBrand);
	public Page<Brand> findPage(Pageable pageable);

  //修改
//  public List<Brand> findList(Integer count, List<Filter> filters, 
//						List<Order> orders, String cacheRegion);

  public void save(Brand brand);

  public Brand update(Brand brand);

//  //忽视
//  public Brand update(Brand brand, String[] ignoreProperties);

  public void delete(Long id);

  public void delete(Long[] ids);

  public void delete(Brand brand);
  
}