package com.hongqiang.shop.modules.product.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Tag;

public interface TagDao extends TagDaoCustom, CrudRepository<Tag, Long> {
	public Tag findById(Long id);
}

/**
 * DAO自定义接口
 * 
 * @author Jack
 * 
 */
interface TagDaoCustom extends BaseDao<Tag> {

	public Page<Tag> findPage(Pageable pageable);

	public List<Tag> findList(Tag.Type paramType);

	// public abstract List<Tag> findList(Integer paramInteger, List<Filter>
	// paramList, List<Order> paramList1, String paramString);

}