package com.hongqiang.shop.website.dao;

import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.website.entity.Log;

public abstract interface LogDao extends LogDaoCustom, CrudRepository<Log, Long> {

}

/**
 * DAO自定义接口
 * 
 * @author Jack
 * 
 */
interface LogDaoCustom extends BaseDao<Log> {
  public void removeAll();
  
  public Page<Log> findPage(Pageable pageable);
}