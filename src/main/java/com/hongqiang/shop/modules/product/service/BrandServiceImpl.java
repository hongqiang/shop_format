package com.hongqiang.shop.modules.product.service;

import java.util.List;

import javax.annotation.Resource;

import com.hongqiang.shop.common.utils.Filter;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Order;
import com.hongqiang.shop.modules.product.dao.BrandDao;
import com.hongqiang.shop.modules.entity.Brand;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.service.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class BrandServiceImpl extends BaseService
  implements BrandService
{

  @Autowired
  private BrandDao brandDao;

   @Transactional
  @CacheEvict(value={"brand"}, allEntries=true)
	public Brand find(Long id) {
		return this.brandDao.findById(id);
	}
  
//  //这个地方不会写
//   @Transactional
//  @CacheEvict(value={"brand"}, allEntries=true)
//  public Page<Brand[]> findPage(Page<Brand[]> pageBrand){
//		return this.brandDao.find(objPage);
//  }
   public Page<Brand> findPage(Pageable pageable){
	   return this.brandDao.findPage(pageable);
   }
//  //修改
//  @Transactional(readOnly=true)
//  @Cacheable({"brand"})
//  public List<Brand> findList(Integer count, List<Filter> filters, List<Order> orders, String cacheRegion)
//  {
//    return this.brandDao.findList(null, count, filters, orders);
//  }

  @Transactional
  @CacheEvict(value={"brand"}, allEntries=true)
  public void save(Brand brand)
  {
	this.brandDao.persist(brand);
  }

  @Transactional
  @CacheEvict(value={"brand"}, allEntries=true)
  public Brand update(Brand brand)
  {
    return (Brand)this.brandDao.merge(brand);
  }

//  //忽视
//  @Transactional
//  @CacheEvict(value={"brand"}, allEntries=true)
//  public Brand update(Brand brand, String[] ignoreProperties)
//  {
//    return (Brand)super.update(brand, ignoreProperties);
//  }

  @Transactional
  @CacheEvict(value={"brand"}, allEntries=true)
  public void delete(Long id)
  {

    this.brandDao.delete(id);
  }

  @Transactional
  @CacheEvict(value={"brand"}, allEntries=true)
  public void delete(Long[] ids)
  {
	  if (ids != null)
			for (Long localSerializable : ids)
				this.brandDao.delete(localSerializable);
  }

  @Transactional
  @CacheEvict(value={"brand"}, allEntries=true)
  public void delete(Brand brand)
  {
    this.brandDao.delete(brand);
  }
}