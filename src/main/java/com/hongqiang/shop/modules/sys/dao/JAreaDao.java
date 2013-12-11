/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.hongqiang.shop.modules.sys.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.modules.sys.entity.JArea;

/**
 * 区域DAO接口
 * @author ThinkGem
 * @version 2013-01-15
 */
public interface JAreaDao extends JAreaDaoCustom, CrudRepository<JArea, Long> {

	@Modifying
	@Query("update JArea set delFlag='" + JArea.DEL_FLAG_DELETE + "' where id = ?1 or parentIds like ?2")
	public int deleteById(Long id, String likeParentIds);
	
	public List<JArea> findByParentIdsLike(String parentIds);

	@Query("from JArea where delFlag='" + JArea.DEL_FLAG_NORMAL + "' order by code")
	public List<JArea> findAllList();
	
	@Query("from JArea where (id=?1 or parent.id=?1 or parentIds like ?2) and delFlag='" + JArea.DEL_FLAG_NORMAL + "' order by code")
	public List<JArea> findAllChild(Long parentId, String likeParentIds);
	
}

/**
 * DAO自定义接口
 * @author ThinkGem
 */
interface JAreaDaoCustom extends BaseDao<JArea> {

}

/**
 * DAO自定义接口实现
 * @author ThinkGem
 */
@Repository
class JAreaDaoImpl extends BaseDaoImpl<JArea> implements JAreaDaoCustom {

}
