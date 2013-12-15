package com.hongqiang.shop.modules.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.service.BaseService;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Specification;
import com.hongqiang.shop.modules.product.dao.SpecificationDao;

@Service
public class SpecificationServiceImpl extends BaseService
  implements SpecificationService
{
  @Autowired
  private SpecificationDao specificationDao;
  
   @Transactional
 	public Specification find(Long id) {
 		return this.specificationDao.findById(id);
 	}

   @Transactional(readOnly=true)
   public Page<Specification> findPage(Pageable pageable){
	   return this.specificationDao.findPage(pageable);
   }

  @Transactional
  @CacheEvict(value={"specification"}, allEntries=true)
  public void save(Specification specification)
  {
    this.specificationDao.persist(specification);
  }

  @Transactional
  @CacheEvict(value={"specification"}, allEntries=true)
  public Specification update(Specification specification)
  {
    return (Specification)this.specificationDao.merge(specification);
  }

  @Transactional
  @CacheEvict(value={"specification"}, allEntries=true)
  public Specification update(Specification specification, String[] ignoreProperties)
  {
    return (Specification)this.specificationDao.update(specification, ignoreProperties);
  }

  @Transactional
  public void delete(Long id)
  {
    this.specificationDao.delete(id);
  }

  @Transactional
  public void delete(Long[] ids)
  {
     if (ids != null)
			for (Long localSerializable : ids)
				this.specificationDao.delete(localSerializable);
  }

  @Transactional
  public void delete(Specification specification)
  {
    this.specificationDao.delete(specification);
  }
  
}