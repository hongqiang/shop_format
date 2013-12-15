/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.hongqiang.shop.modules.cms.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.modules.cms.entity.JArticle;

/**
 * 文章DAO接口
 * @author ThinkGem
 * @version 2013-01-15
 */
public interface JArticleDao extends JArticleDaoCustom, CrudRepository<JArticle, Long> {

	@Modifying
	@Query("update JArticle set delFlag=?2 where id = ?1")
	public int updateDelFlag(Long id, String status);
	
	public List<JArticle> findByIdIn(Long[] ids);
	
	@Modifying
	@Query("update JArticle set hits=hits+1 where id = ?1")
	public int updateHitsAddOne(Long id);
	
	@Modifying
	@Query("update JArticle set weight=0 where weight > 0 and weightDate < current_timestamp()")
	public int updateExpiredWeight();
	
}

/**
 * DAO自定义接口
 * @author ThinkGem
 */
interface JArticleDaoCustom extends BaseDao<JArticle> {

}

/**
 * DAO自定义接口实现
 * @author ThinkGem
 */
@Repository
class JArticleDaoImpl extends BaseDaoImpl<JArticle> implements JArticleDaoCustom {

}
