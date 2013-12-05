package com.hongqiang.shop.modules.product.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.modules.entity.Attribute;
import com.hongqiang.shop.modules.entity.Brand;
import com.hongqiang.shop.modules.entity.Goods;
import com.hongqiang.shop.modules.entity.Member;
import com.hongqiang.shop.modules.entity.Order;
import com.hongqiang.shop.modules.entity.OrderEntity;
import com.hongqiang.shop.modules.entity.Product;
import com.hongqiang.shop.modules.entity.ProductCategory;
import com.hongqiang.shop.modules.entity.Promotion;
import com.hongqiang.shop.modules.entity.Tag;
import com.hongqiang.shop.modules.util.Filter;

public interface ProductDao extends ProductDaoCustom,
		CrudRepository<ProductCategory, Long> {

}

interface ProductDaoCustom extends BaseDao<Product> {
	public  boolean snExists(String paramString);

	public  Product findBySn(String paramString);



	public  List<Product> findList(
			ProductCategory paramProductCategory, Brand paramBrand,
			Promotion paramPromotion, List<Tag> paramList,
			Map<Attribute, String> paramMap, BigDecimal paramBigDecimal1,
			BigDecimal paramBigDecimal2, Boolean paramBoolean1,
			Boolean paramBoolean2, Boolean paramBoolean3,
			Boolean paramBoolean4, Boolean paramBoolean5,
			Boolean paramBoolean6, Product.OrderType paramOrderType,
			Integer paramInteger, List<Filter> paramList1,
			List<OrderEntity> paramList2);
	
//	public  List<Product> search(String paramString,
//			Boolean paramBoolean, Integer paramInteger);
	
//	public  List<Product> findList(
//			ProductCategory paramProductCategory, Date paramDate1,
//			Date paramDate2, Integer paramInteger1, Integer paramInteger2);
//
//	public  List<Product> findList(Goods paramGoods,
//			Set<Product> paramSet);
//
//	public  Page<Product> findPage(
//			ProductCategory paramProductCategory, Brand paramBrand,
//			Promotion paramPromotion, List<Tag> paramList,
//			Map<Attribute, String> paramMap, BigDecimal paramBigDecimal1,
//			BigDecimal paramBigDecimal2, Boolean paramBoolean1,
//			Boolean paramBoolean2, Boolean paramBoolean3,
//			Boolean paramBoolean4, Boolean paramBoolean5,
//			Boolean paramBoolean6, Product.OrderType paramOrderType,
//			Pageable paramPageable);
//
//	public  Page<Product> findPage(Member paramMember,
//			Pageable paramPageable);
//
//	public  Page<Object> findSalesPage(Date paramDate1,
//			Date paramDate2, Pageable paramPageable);
//
//	public  Long count(Member paramMember, Boolean paramBoolean1,
//			Boolean paramBoolean2, Boolean paramBoolean3,
//			Boolean paramBoolean4, Boolean paramBoolean5, Boolean paramBoolean6);
//
//	public  boolean isPurchased(Member paramMember, Product paramProduct);
}