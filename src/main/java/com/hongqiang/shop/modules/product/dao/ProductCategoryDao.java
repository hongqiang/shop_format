package com.hongqiang.shop.modules.product.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.modules.product.entity.ProductCategory;

public interface ProductCategoryDao extends ProductCategoryDaoCustom, CrudRepository<ProductCategory, Long> {
	
	@Query("from ProductCategory where parent is null order by order asc")
	public List<ProductCategory> findRoots();
	
	public ProductCategory findById(Long id);
	
	public ProductCategory findByParent(ProductCategory parent);
}
	

/**
 * DAO自定义接口
 * 
 * @author ThinkGem
 */
interface ProductCategoryDaoCustom extends BaseDao<ProductCategory> {
	
	/**
	 * 获取所有顶级商品分类集合;
	 * 
	 * @param paramInteger
	 *            返回的顶级商品分类数目，若为null表示返回所有
	 * @return 所有顶级商品分类集合
	 * 
	 */
	List<ProductCategory> findRoots(Integer paramInteger);

	/**
	 * 根据ProductCategory对象，获取他的所有父类集合;
	 * 
	 * @param paramProductCategory
	 *            ProductCategory对象
	 * @param paramInteger
	 *            返回的顶级商品分类数目，若为null表示返回所有
	 * @return 所有父类集合
	 * 
	 */
	List<ProductCategory> findParents(ProductCategory paramProductCategory,
			Integer paramInteger);

	/**
	 * 根据ProductCategory对象，获取他的所有子类集合
	 * 
	 * @param paramProductCategory
	 *            ProductCategory对象
	 * @param paramInteger
	 *            返回的顶级商品分类数目，若为null表示返回所有
	 * @return 所有子类集合
	 * 
	 */
	List<ProductCategory> findChildren(ProductCategory paramProductCategory,
			Integer paramInteger);
}
