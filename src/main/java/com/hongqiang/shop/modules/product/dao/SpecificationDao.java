package com.hongqiang.shop.modules.product.dao;

import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Specification;

public interface SpecificationDao extends SpecificationDaoCustom, CrudRepository<Specification, Long> {
	public Specification findById(Long id);
}

/**
 * DAO自定义接口
 * 
 * @author Jack
 * 
 */
interface SpecificationDaoCustom extends BaseDao<Specification> {

	public Page<Specification> findPage(Pageable pageable);

}