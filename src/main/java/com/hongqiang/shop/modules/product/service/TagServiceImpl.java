package com.hongqiang.shop.modules.product.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.service.BaseService;
import com.hongqiang.shop.common.utils.Filter;
import com.hongqiang.shop.common.utils.Order;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Tag;
import com.hongqiang.shop.modules.product.dao.TagDao;

@Service
public class TagServiceImpl extends BaseService
  implements TagService
{

   @Autowired
  private TagDao tagDao;
   
   @Transactional
   @CacheEvict(value={"Tag"}, allEntries=true)
 	public Tag find(Long id) {
 		return this.tagDao.findById(id);
 	}

   @Transactional(readOnly=true)
   public Page<Tag> findPage(Pageable pageable){
	   return this.tagDao.findPage(pageable);
   }

  @Transactional(readOnly=true)
  public List<Tag> findList(Tag.Type type)
  {
    return this.tagDao.findList(type);
  }

  @Transactional(readOnly=true)
  @Cacheable({"tag"})
  public List<Tag> findList(Integer count, List<Filter> filters, List<Order> orders, String cacheRegion)
  {
    return this.tagDao.findList(null, count, filters, orders);
  }

  @Transactional
  @CacheEvict(value={"tag"}, allEntries=true)
  public void save(Tag tag)
  {
    this.tagDao.persist(tag);
  }

  @Transactional
  @CacheEvict(value={"tag"}, allEntries=true)
  public Tag update(Tag tag)
  {
    return (Tag)this.tagDao.merge(tag);
  }

  @Transactional
  @CacheEvict(value={"tag"}, allEntries=true)
  public Tag update(Tag tag, String[] ignoreProperties)
  {
    return (Tag)this.tagDao.update(tag, ignoreProperties);
  }

  @Transactional
  @CacheEvict(value={"tag"}, allEntries=true)
  public void delete(Long id)
  {
    this.tagDao.delete(id);
  }

  @Transactional
  @CacheEvict(value={"tag"}, allEntries=true)
  public void delete(Long[] ids)
  {
     if (ids != null)
			for (Long localSerializable : ids)
				this.tagDao.delete(localSerializable);
  }

  @Transactional
  @CacheEvict(value={"tag"}, allEntries=true)
  public void delete(Tag tag)
  {
    this.tagDao.delete(tag);
  }
}