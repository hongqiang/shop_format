package com.hongqiang.shop.modules.product.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.modules.entity.Brand;
import com.hongqiang.shop.common.utils.Pageable;

public interface BrandDao extends BrandDaoCustom, CrudRepository<Brand, Long>{
	public Brand findById(Long id);
	
}


/**
 * DAO自定义接口
 * @author Jack
 *
 */
interface BrandDaoCustom extends BaseDao<Brand> {



	public Page<Brand>  findPage(Pageable pageable);

}