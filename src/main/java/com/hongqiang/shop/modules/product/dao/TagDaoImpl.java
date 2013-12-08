package com.hongqiang.shop.modules.product.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Tag;

@Repository
 class TagDaoImpl extends BaseDaoImpl<Tag>
  implements TagDaoCustom
{

	@Override
	public Page<Tag>  findPage(Pageable pageable){
		Page<Tag> brandPage = new Page<Tag>(pageable.getPageNumber(),pageable.getPageSize());
		String qlString = "select tag from Tag tag";
		return super.find(brandPage, qlString);
	}
	
	@Override
  public List<Tag> findList(Tag.Type type)
  {
	String sqlString="select tag from Tag tag where tag.type =?  order by tag.order ASC";
	return super.find(sqlString,type);
  }
}