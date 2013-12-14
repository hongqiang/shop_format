package com.hongqiang.shop.modules.product.dao;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Brand;

import org.springframework.stereotype.Repository;

@Repository
class BrandDaoImpl extends BaseDaoImpl<Brand>  implements BrandDaoCustom{
  
	@Override
	public Page<Brand>  findPage(Pageable pageable){
		Page<Brand> brandPage = new Page<Brand>(pageable.getPageNumber(),pageable.getPageSize());
		String qlString = "select brand from Brand brand";
		return super.find(brandPage, qlString);
	}
}