package com.hongqiang.shop.modules.product.dao;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Brand;

import org.springframework.stereotype.Repository;

@Repository
class BrandDaoImpl extends BaseDaoImpl<Brand>  implements BrandDaoCustom{
  
//  //或者采用productcategoryimpl那种方式，但那种方式得到的数组，需要封装成page
//  @Override
//  public Page<Brand[]> find(Page<Brand[]> pageBrand){
//	String qlString = "select brand from Brand brand";
//	return super.find(pageBrand, qlString);
//  }
	@Override
	public Page<Brand>  findPage(Pageable pageable){
		Page<Brand> brandPage = new Page<Brand>(pageable.getPageNumber(),pageable.getPageSize());
		String qlString = "select brand from Brand brand";
		return super.find(brandPage, qlString);
	}
}