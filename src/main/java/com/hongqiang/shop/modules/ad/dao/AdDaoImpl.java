package com.hongqiang.shop.modules.ad.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.website.entity.Ad;

@Repository
public class AdDaoImpl extends BaseDaoImpl<Ad>
  implements AdDaoCustom{
  
  @Override
  public Page<Ad>  findPage(Pageable pageable){
	Page<Ad> adPage = new Page<Ad>(pageable.getPageNumber(),pageable.getPageSize());
	String qlString = "select ad from Ad ad where 1=1 ";
	List<Object> parameter = new ArrayList<Object>();
	return super.findPage(adPage, qlString,  parameter, pageable);
  }
}