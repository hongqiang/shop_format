package com.hongqiang.shop.modules.user.dao;

import javax.persistence.FlushModeType;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Admin;

@Repository
public class AdminDaoImpl extends BaseDaoImpl<Admin>
  implements AdminDaoCustom
{
	@Override
  public boolean usernameExists(String username)
  {
    if (username == null)
      return false;
    String str = "select count(*) from Admin admin where lower(admin.username) = lower(:username)";
    Long localLong = (Long)this.getEntityManager().createQuery(str, Long.class).setFlushMode(FlushModeType.COMMIT).setParameter("username", username).getSingleResult();
    return localLong.longValue() > 0L;
  }

  @Override
	public Page<Admin>  findPage(Pageable pageable){
		Page<Admin> adminPage = new Page<Admin>(pageable.getPageNumber(),pageable.getPageSize());
		String sqlString = "select admin from Admin admin";
		return super.find(adminPage, sqlString);
	}
}