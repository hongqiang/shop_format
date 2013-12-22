package com.hongqiang.shop.modules.ad.dao;

import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.website.entity.Ad;

public abstract interface AdDao extends AdDaoCustom, CrudRepository<Ad, Long> {

}

/**
 * DAO自定义接口
 * 
 * @author Jack
 * 
 */
interface AdDaoCustom extends BaseDao<Ad> {

	public Page<Ad>  findPage(Pageable pageable);
}