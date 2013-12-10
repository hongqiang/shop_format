package com.hongqiang.shop.modules.user.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.MemberAttribute;

public  interface MemberAttributeDao extends MemberAttributeDaoCustom, CrudRepository<MemberAttribute, Long>{
	public MemberAttribute findById(Long id);
}


/**
 * DAO自定义接口
 * 
 * @author Jack
 * 
 */
interface MemberAttributeDaoCustom extends BaseDao<MemberAttribute> {
  public  Integer findUnusedPropertyIndex();

  public Page<MemberAttribute> findPage(Pageable pageable);
  
  public  List<MemberAttribute> findList();
  
  public void remove(MemberAttribute memberAttribute);
}