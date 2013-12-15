package com.hongqiang.shop.modules.product.service;

import java.util.List;

import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Tag;

public abstract interface TagService {
	
	public Tag find(Long id); 

	public Page<Tag> findPage(Pageable pageable);

	public abstract List<Tag> findList(Tag.Type paramType);

	// public abstract List<Tag> findList(Integer paramInteger, List<Filter>
	// paramList, List<Order> paramList1, String paramString);

	public void save(Tag tag);

	public Tag update(Tag tag);

	 public Tag update(Tag brand, String[] ignoreProperties);

	public void delete(Long id);

	public void delete(Long[] ids);

	public void delete(Tag tag);
}