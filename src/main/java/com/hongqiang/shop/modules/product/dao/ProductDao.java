package com.hongqiang.shop.modules.product.dao;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.modules.product.entity.Product;
import com.hongqiang.shop.modules.product.entity.ProductCategory;

public interface ProductDao extends ProductDaoCustom,
		CrudRepository<ProductCategory, Long> {

}

interface ProductDaoCustom extends BaseDao<Product> {
	public abstract boolean snExists(String paramString);

	public abstract Product findBySn(String paramString);

	public abstract List<Product> search(String paramString,
			Boolean paramBoolean, Integer paramInteger);

//	public abstract List<Product> findList(
//			ProductCategory paramProductCategory, Brand paramBrand,
//			Promotion paramPromotion, List<Tag> paramList,
//			Map<Attribute, String> paramMap, BigDecimal paramBigDecimal1,
//			BigDecimal paramBigDecimal2, Boolean paramBoolean1,
//			Boolean paramBoolean2, Boolean paramBoolean3,
//			Boolean paramBoolean4, Boolean paramBoolean5,
//			Boolean paramBoolean6, Product.OrderType paramOrderType,
//			Integer paramInteger, List<Filter> paramList1,
//			List<Order> paramList2);
//
//	public abstract List<Product> findList(
//			ProductCategory paramProductCategory, Date paramDate1,
//			Date paramDate2, Integer paramInteger1, Integer paramInteger2);
//
//	public abstract List<Product> findList(Goods paramGoods,
//			Set<Product> paramSet);
//
//	public abstract Page<Product> findPage(
//			ProductCategory paramProductCategory, Brand paramBrand,
//			Promotion paramPromotion, List<Tag> paramList,
//			Map<Attribute, String> paramMap, BigDecimal paramBigDecimal1,
//			BigDecimal paramBigDecimal2, Boolean paramBoolean1,
//			Boolean paramBoolean2, Boolean paramBoolean3,
//			Boolean paramBoolean4, Boolean paramBoolean5,
//			Boolean paramBoolean6, Product.OrderType paramOrderType,
//			Pageable paramPageable);
//
//	public abstract Page<Product> findPage(Member paramMember,
//			Pageable paramPageable);
//
//	public abstract Page<Object> findSalesPage(Date paramDate1,
//			Date paramDate2, Pageable paramPageable);
//
//	public abstract Long count(Member paramMember, Boolean paramBoolean1,
//			Boolean paramBoolean2, Boolean paramBoolean3,
//			Boolean paramBoolean4, Boolean paramBoolean5, Boolean paramBoolean6);
//
//	public abstract boolean isPurchased(Member paramMember, Product paramProduct);
}