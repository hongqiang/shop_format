package com.hongqiang.shop.modules.user.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Member;

public interface MemberDao extends MemberDaoCustom, CrudRepository<Member, Long>{

	public Member findById(Long id);
}

/**
 * DAO自定义接口
 * 
 * @author Jack
 * 
 */
interface MemberDaoCustom extends BaseDao<Member> {


  public  Member findByUsername(String paramString);

  public  List<Member> findListByEmail(String paramString);

  public  Page<Member> findPurchasePage(Date paramDate1, Date paramDate2, Pageable paramPageable);
  
  public Page<Member> findPage(Pageable pageable);
  
  public  boolean usernameExists(String paramString);

  public  boolean emailExists(String paramString);
}