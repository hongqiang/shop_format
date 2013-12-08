package com.hongqiang.shop.modules.product.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.modules.entity.Parameter;
import com.hongqiang.shop.modules.entity.ParameterGroup;

public abstract interface ParameterDao extends ParameterDaoCustom, CrudRepository<Parameter, Long>{
	

}

/**
 * DAO自定义接口
 * @author Jack
 *
 */
interface ParameterDaoCustom extends BaseDao<Parameter> {

	  public abstract List<Parameter> findList(ParameterGroup paramParameterGroup, Set<Parameter> paramSet);
}