package com.hongqiang.shop.modules.product.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hongqiang.shop.common.service.BaseService;
import com.hongqiang.shop.modules.entity.ProductCategory;
import com.hongqiang.shop.modules.product.dao.ProductCategoryDao;


@Service("productCategoryServiceImpl")
public class ProductCategoryServiceImpl extends BaseService implements
		ProductCategoryService {

	@Autowired
	private ProductCategoryDao productCategoryDao;

	@Transactional(readOnly = true)
	public List<ProductCategory> findRoots() {
		return this.productCategoryDao.findRoots(null);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findRoots(Integer count) {
		return this.productCategoryDao.findRoots(count);
	}

	@Transactional(readOnly = true)
	 @Cacheable({"productCategory"})
	public List<ProductCategory> findRoots(Integer count, String cacheRegion) {
		return this.productCategoryDao.findRoots(count);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findParents(ProductCategory productCategory) {
		return this.productCategoryDao.findParents(productCategory, null);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findParents(ProductCategory productCategory,
			Integer count) {
		return this.productCategoryDao.findParents(productCategory, count);
	}

	@Transactional(readOnly = true)
	 @Cacheable({"productCategory"})
	public List<ProductCategory> findParents(ProductCategory productCategory,
			Integer count, String cacheRegion) {
		return this.productCategoryDao.findParents(productCategory, count);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findTree() {
		return this.productCategoryDao.findChildren(null, null);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findChildren(ProductCategory productCategory) {
		return this.productCategoryDao.findChildren(productCategory, null);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findChildren(ProductCategory productCategory,
			Integer count) {
		return this.productCategoryDao.findChildren(productCategory, count);
	}

	@Transactional(readOnly = true)
	 @Cacheable({"productCategory"})
	public List<ProductCategory> findChildren(ProductCategory productCategory,
			Integer count, String cacheRegion) {
		return this.productCategoryDao.findChildren(productCategory, count);
	}

	@Transactional
	 @CacheEvict(value={"product", "productCategory", "review",
	 "consultation"}, allEntries=true)
	public void save(ProductCategory productCategory) {
		this.productCategoryDao.persist(productCategory);
	}

	@Transactional
	 @CacheEvict(value={"product", "productCategory", "review",
	 "consultation"}, allEntries=true)
	public ProductCategory update(ProductCategory productCategory) {
		return (ProductCategory) this.productCategoryDao
				.merge(productCategory);
	}

	@Transactional
	 @CacheEvict(value={"product", "productCategory", "review",
	 "consultation"}, allEntries=true)
	 public ProductCategory update(ProductCategory productCategory,String[] paramArrayOfString){
		 return (ProductCategory)this.productCategoryDao.update(productCategory, paramArrayOfString);
	 }
	 
	
	@Transactional
	 @CacheEvict(value={"product", "productCategory", "review",
	 "consultation"}, allEntries=true)
	public void delete(Long id) {
		this.productCategoryDao.delete(id);// Remove the entity instance by id.
	}

	@Transactional
	 @CacheEvict(value={"product", "productCategory", "review",
	 "consultation"}, allEntries=true)
	public void delete(Long[] ids) {
		if (ids != null)
			for (Long localSerializable : ids)
				this.productCategoryDao.delete(localSerializable);
	}

	@Transactional
	 @CacheEvict(value={"product", "productCategory", "review",
	 "consultation"}, allEntries=true)
	public void delete(ProductCategory productCategory) {
		this.productCategoryDao.delete(productCategory);
	}

	@Transactional
	public ProductCategory find(Long id) {
		return this.productCategoryDao.find(id);
	}
}