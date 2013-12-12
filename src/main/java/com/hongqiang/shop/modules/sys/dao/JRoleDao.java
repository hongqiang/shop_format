/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.hongqiang.shop.modules.sys.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.modules.sys.entity.JRole;

/**
 * 角色DAO接口
 * @author ThinkGem
 * @version 2013-05-15
 */
public interface JRoleDao extends JRoleDaoCustom, CrudRepository<JRole, Long> {
	
	@Query("from Role where name = ?1 and delFlag = '" + JRole.DEL_FLAG_NORMAL + "'")
	public JRole findByName(String name);

	@Modifying
	@Query("update Role set delFlag='" + JRole.DEL_FLAG_DELETE + "' where id = ?1")
	public int deleteById(Long id);

//	@Query("from Role where delFlag='" + Role.DEL_FLAG_NORMAL + "' order by name")
//	public List<Role> findAllList();
//
//	@Query("select distinct r from Role r, User u where r in elements (u.roleList) and r.delFlag='" + Role.DEL_FLAG_NORMAL +
//			"' and u.delFlag='" + User.DEL_FLAG_NORMAL + "' and u.id=?1 or (r.user.id=?1 and r.delFlag='" + Role.DEL_FLAG_NORMAL +
//			"') order by r.name")
//	public List<Role> findByUserId(Long userId);
}

/**
 * DAO自定义接口
 * @author ThinkGem
 */
interface JRoleDaoCustom extends BaseDao<JRole> {
	
//	void deleteWithReference(Long id);

}

/**
 * DAO自定义接口实现
 * @author ThinkGem
 */
@Repository
class JRoleDaoImpl extends BaseDaoImpl<JRole> implements JRoleDaoCustom {

//	private static final String QUERY_USER_BY_GROUPID = "select u from User u left join u.roleList g where g.id=?";
//
//	@Override
//	public void deleteWithReference(Long id) {
//		Role role = getEntityManager().find(Role.class, id);
//		@SuppressWarnings("unchecked")
//		List<User> users = getEntityManager().createQuery(QUERY_USER_BY_GROUPID).setParameter(1, id).getResultList();
//		for (User u : users) {
//			u.getRoleList().remove(role);
//		}
//		getEntityManager().remove(role);
//		
//	}

}
