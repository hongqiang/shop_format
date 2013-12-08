package com.hongqiang.shop.modules.product.dao;

import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.modules.entity.SpecificationValue;

public interface SpecificationValueDao extends SpecificationValueCustom, CrudRepository<SpecificationValue, Long> {
	public SpecificationValue findById(Long id);
}

/**
 * DAO自定义接口
 * 
 * @author Jack
 * 
 */
interface SpecificationValueCustom extends BaseDao<SpecificationValue> {

}