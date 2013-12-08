package com.hongqiang.shop.modules.product.service;

import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Specification;

public abstract interface SpecificationService{

	public Specification find(Long id); 

	public Page<Specification> findPage(Pageable pageable);

	public void save(Specification specification);

	public Specification update(Specification specification);

	// //忽视
	// public Specification update(Specification brand, String[] ignoreProperties);

	public void delete(Long id);

	public void delete(Long[] ids);

	public void delete(Specification specification);
}