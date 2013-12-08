package com.hongqiang.shop.modules.product.dao;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Specification;

@Repository
public class SpecificationDaoImpl extends BaseDaoImpl<Specification>
  implements SpecificationDaoCustom{
  	@Override
	public Page<Specification>  findPage(Pageable pageable){
		Page<Specification> brandPage = new Page<Specification>(pageable.getPageNumber(),pageable.getPageSize());
		String qlString = "select specification from Specification specification";
		return super.find(brandPage, qlString);
	}
}