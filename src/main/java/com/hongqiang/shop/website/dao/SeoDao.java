package com.hongqiang.shop.website.dao;

import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.website.entity.Seo;

public interface SeoDao extends SeoDaoCustom, CrudRepository<Seo, Long> {
	public Seo findByType(Seo.Type paramType);
}

/**
 * DAO自定义接口
 * 
 * @author Jack
 * 
 */
interface SeoDaoCustom extends BaseDao<Seo> {
	public Page<Seo>  findPage(Pageable pageable);
}