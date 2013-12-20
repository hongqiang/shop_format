package com.hongqiang.shop.website.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.website.entity.Seo;

@Repository
public class SeoDaoImpl extends BaseDaoImpl<Seo>
  implements SeoDaoCustom
{
	@Override
	public Page<Seo>  findPage(Pageable pageable){
		Page<Seo> seoPage = new Page<Seo>(pageable.getPageNumber(),pageable.getPageSize());
		String qlString = "select seo from Seo seo where 1=1 ";
		List<Object> parameter = new ArrayList<Object>();
		return super.findPage(seoPage,  qlString,  parameter, pageable) ;
	}
}