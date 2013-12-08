package com.hongqiang.shop.modules.product.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Filter;
import com.hongqiang.shop.common.utils.Order;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.cms.entity.Category;
import com.hongqiang.shop.modules.entity.Attribute;
import com.hongqiang.shop.modules.entity.Brand;
import com.hongqiang.shop.modules.entity.Goods;
import com.hongqiang.shop.modules.entity.Product;
import com.hongqiang.shop.modules.entity.ProductCategory;
import com.hongqiang.shop.modules.entity.Promotion;
import com.hongqiang.shop.modules.entity.Tag;

public interface ProductDao extends ProductDaoCustom,
		CrudRepository<Product, Long> {
	@Query("from Product product where lower(product.sn) = lower(?)")
	public Product findBySn(String paramSn);

	public Product findById(Long id);

	
}

interface ProductDaoCustom extends BaseDao<Product> {

	public boolean snExists(String paramString);

	public List<Product> search(String paramString, Boolean paramBoolean,
			Integer paramInteger);

	public List<Product> findList(ProductCategory paramProductCategory,
			Brand paramBrand, Promotion paramPromotion, List<Tag> paramList,
			Map<Attribute, String> paramMap, BigDecimal paramBigDecimal1,
			BigDecimal paramBigDecimal2, Boolean paramBoolean1,
			Boolean paramBoolean2, Boolean paramBoolean3,
			Boolean paramBoolean4, Boolean paramBoolean5,
			Boolean paramBoolean6, Product.OrderType paramOrderType,
			Integer paramInteger, List<Filter> paramList1,
			List<Order> paramList2);

	public List<Product> findList(ProductCategory paramProductCategory,
			Date paramDate1, Date paramDate2, Integer paramInteger1,
			Integer paramInteger2);

	public List<Product> findList(Goods paramGoods, Set<Product> paramSet);

	public Page<Product> findPage(ProductCategory productCategory, Brand brand,
			Promotion promotion, List<Tag> tags,
			Map<Attribute, String> attributeValue, BigDecimal startPrice,
			BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock,
			Boolean isStockAlert, Product.OrderType orderType, Pageable pageable);

	// public Page<Product> findPage(Member paramMember,
	// Pageable paramPageable);
	//
	// public Page<Object> findSalesPage(Date paramDate1,
	// Date paramDate2, Pageable paramPageable);
	//
	// public Long count(Member paramMember, Boolean paramBoolean1,
	// Boolean paramBoolean2, Boolean paramBoolean3,
	// Boolean paramBoolean4, Boolean paramBoolean5, Boolean paramBoolean6);
	//
	// public boolean isPurchased(Member paramMember, Product paramProduct);

	public void persist(Product product);

	public Product merge(Product product);

	public void remove(Product product);
	
	public void  deleteAttributeOfProduct(Attribute attribute);
	
	public void  updateAttributeOfProduct(Attribute attribute);
}