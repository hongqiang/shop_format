package com.hongqiang.shop.modules.product.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
//import net.shopxx.Setting;
//import net.shopxx.dao.GoodsDao;
//import net.shopxx.dao.ProductDao;
//import net.shopxx.dao.SnDao;
//import net.shopxx.util.SettingUtils;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.FlushModeType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Filter;
import com.hongqiang.shop.common.utils.Order;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Attribute;
import com.hongqiang.shop.modules.entity.Brand;
import com.hongqiang.shop.modules.entity.Goods;
import com.hongqiang.shop.modules.entity.Product;
import com.hongqiang.shop.modules.entity.ProductCategory;
import com.hongqiang.shop.modules.entity.Promotion;
import com.hongqiang.shop.modules.entity.SpecificationValue;
import com.hongqiang.shop.modules.entity.Tag;

@Repository
class ProductDaoImpl extends BaseDaoImpl<Product> implements ProductDaoCustom {

	class SortSpecificationValue implements Comparator<SpecificationValue> {
		public int compare(SpecificationValue a1, SpecificationValue a2) {
			return new CompareToBuilder().append(a1.getSpecification(),
					a2.getSpecification()).toComparison();
		}
	}

	private static final Pattern pattern = Pattern.compile("\\d*");

	 @Autowired
	 private GoodsDao goodsDao;
	
	 @Autowired
	 private SnDao snDao;

	public boolean snExists(String sn) {
		if (sn == null)
			return false;
		String str = "select count(*) from Product product where lower(product.sn) = lower(:sn)";
		Long localLong = (Long) this.getEntityManager()
				.createQuery(str, Long.class)
				.setFlushMode(FlushModeType.COMMIT).setParameter("sn", sn)
				.getSingleResult();
		return localLong.longValue() > 0L;
	}

	@Override
	public List<Product> search(String keyword, Boolean isGift, Integer count) {
		if (StringUtils.isEmpty(keyword))
			return Collections.emptyList();
		String sqlString = "select DISTINCT product from Product product where 1=1 ";
		List<Object> params = new ArrayList<Object>();
		if (pattern.matcher(keyword).matches()) {
			sqlString += " and (product.id = ? or product.sn like (?) or product.fullName like (?)) ";
			params.add(Long.valueOf(keyword));
			params.add("%" + keyword + "%");
			params.add("%" + keyword + "%");
		} else {
			sqlString += " and (product.sn like (?) or product.fullName like (?)) ";
			params.add("%" + keyword + "%");
			params.add("%" + keyword + "%");
		}
		if (isGift != null) {
			sqlString += " and product.isGift =? ";
			params.add(isGift);
		}
		sqlString += " order by product.isTop DESC, product.updateDate DESC ";
		return this.findList(sqlString, params, null, count,null,null);
	}

	@Override
	public List<Product> findList(ProductCategory productCategory, Brand brand,
			Promotion promotion, List<Tag> tags,
			Map<Attribute, String> attributeValue, BigDecimal startPrice,
			BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock,
			Boolean isStockAlert, Product.OrderType orderType, Integer count,
			List<Filter> filters, List<Order> orders) {
		String sqlString = "select DISTINCT product from Product product where 1=1 ";
		List<Object> params = new ArrayList<Object>();
		if (productCategory != null) {
			sqlString += " and (product.productCategory = ? ";
			params.add(productCategory);
			sqlString += " or product.productCategory.treePath like ?) ";
			params.add("%," + productCategory.getId() + ",%");
		}
		if (brand != null) {
			sqlString += " and product.brand=?";
			params.add(brand);
		}
		if (promotion != null) {
			sqlString += " and product.promotions =?";
			params.add(promotion);// 和源码不同,这里把左连接promotion表，左连接productCategory表，以及左连接brand表省略。

		}
		if (attributeValue != null) {
			Iterator localObject2 = attributeValue.entrySet().iterator();
			while (((Iterator) localObject2).hasNext()) {
				Map.Entry localObject1 = (Map.Entry) ((Iterator) localObject2)
						.next();
				Object localObject3 = "attributeValue"
						+ ((Attribute) ((Map.Entry) localObject1).getKey())
								.getPropertyIndex();
				sqlString += " and product." + (String) localObject3 + " = ? ";
				params.add(((Map.Entry) localObject1).getValue());
			}
		}
		if ((startPrice != null) && (endPrice != null)
				&& (startPrice.compareTo(endPrice) > 0)) {
			Object localObject1 = startPrice;
			startPrice = endPrice;
			endPrice = (BigDecimal) localObject1;
		}
		if ((startPrice != null)
				&& (startPrice.compareTo(new BigDecimal(0)) >= 0)) {
			sqlString += " and product.price>=?";
			params.add(startPrice);
		}
		if ((endPrice != null) && (endPrice.compareTo(new BigDecimal(0)) >= 0)) {
			sqlString += " and product.price<=?";
			params.add(endPrice);
		}
		if ((tags != null) && (!tags.isEmpty())) {
			sqlString += " and product.tags in (?)";
			params.add(tags);// Specify whether duplicate query results will be
								// eliminated
		}
		if (isMarketable != null) {
			sqlString += " and product.isMarketable=?";
			params.add(isMarketable);
		}
		if (isList != null) {
			sqlString += " and product.isList=?";
			params.add(isList);
		}
		if (isTop != null) {
			sqlString += " and product.isTop=?";
			params.add(isTop);
		}
		if (isGift != null) {
			sqlString += " and product.isGift=?";
			params.add(isGift);
		}
		if (isOutOfStock != null) {
			if (isOutOfStock.booleanValue()) {
				sqlString += " and (product.stock !=null and product.stock<= product.allocatedStock) ";
			} else {
				sqlString += " and (product.stock !=null or product.stock> product.allocatedStock)";
			}
		}
		if (isStockAlert != null) {
			// Long stockAlertCount= SettingUtils.get().getStockAlertCount();
			Long stockAlertCount = 1L;
			if (isStockAlert.booleanValue()) {
				sqlString += " and (product.stock !=null and product.stock<= (product.allocatedStock+?)) ";
				params.add(stockAlertCount);
			} else {
				sqlString += " and (product.stock !=null or product.stock> (product.allocatedStock+?)) ";
				params.add(stockAlertCount);
			}
		}

		if (orderType == Product.OrderType.priceAsc) {
			sqlString += " order by product.price ASC, product.createDate DESC ";
		} else if (orderType == Product.OrderType.priceDesc) {
			sqlString += " order by product.price DESC, product.createDate DESC ";
		} else if (orderType == Product.OrderType.salesDesc) {
			sqlString += " order by product.sales DESC, product.createDate DESC ";
		} else if (orderType == Product.OrderType.scoreDesc) {
			sqlString += " order by product.score DESC, product.createDate DESC ";
		} else if (orderType == Product.OrderType.dateDesc) {
			sqlString += " order by product.createDate DESC ";
		} else {
			sqlString += " order by product.isTop DESC, product.updateDate DESC ";
		}
		System.out.println(sqlString);
		System.out.println(params.toArray());
		String sql = "select product from Product product where 1=1 and product.productCategory.id = "
				+ productCategory.getId();
		// return this.findList(sqlString,params.toArray());
		return this.find(sqlString, params.toArray());// 测试下jeesite的函数能用不
	}

	@Override
	public List<Product> findList(ProductCategory productCategory,
			Date beginDate, Date endDate, Integer first, Integer count) {
		String sqlString = "select DISTINCT product from Product product where product.isMarketable= '1' ";
		List<Object> params = new ArrayList<Object>();
		if (productCategory != null) {
			sqlString += " and (product.productCategory = ? ";
			params.add(productCategory);
			sqlString += " or product.productCategory.treePath like ?) ";
			params.add("%," + productCategory.getId() + ",%");
		}
		if (beginDate != null) {
			sqlString += " and product.createDate>=?";
			params.add(beginDate);
		}
		if (endDate != null) {
			sqlString += " and product.createDate<=?";
			params.add(endDate);
		}
		sqlString += " order by product.isTop DESC, product.updateDate DESC ";
		return this.findList(sqlString, params, first, count,null,null);
	}

	@Override
	public List<Product> findList(Goods goods, Set<Product> excludes) {
		String sqlString = "select DISTINCT product from Product product where 1=1 ";
		List<Object> params = new ArrayList<Object>();
		if (goods != null) {
			sqlString += " and product.goods = ? ";
			params.add(goods);
		}
		if ((excludes != null) && (!excludes.isEmpty())) {
			sqlString += " and product not in (?) ";
			params.add(excludes);
		}
		return this.findList(sqlString, params, null, null,null,null);
	}

	
	public Page<Product> findPage(ProductCategory productCategory, Brand brand,
			Promotion promotion, List<Tag> tags,
			Map<Attribute, String> attributeValue, BigDecimal startPrice,
			BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock,
			Boolean isStockAlert, Product.OrderType orderType, Pageable pageable) {
		String sqlString = "select DISTINCT product from Product product where 1=1 ";
		List<Object> params = new ArrayList<Object>();
		if (productCategory != null) {
			sqlString += " and (product.productCategory = ? ";
			params.add(productCategory);
			sqlString += " or product.productCategory.treePath like ?) ";
			params.add("%," + productCategory.getId() + ",%");
		}
		if (brand != null) {
			sqlString += " and product.brand=?";
			params.add(brand);
		}
		if (promotion != null) {
			sqlString += " and product.promotions =?";
			params.add(promotion);// 和源码不同,这里把左连接promotion表，左连接productCategory表，以及左连接brand表省略。

		}
		if (attributeValue != null) {
			Iterator localObject2 = attributeValue.entrySet().iterator();
			while (((Iterator) localObject2).hasNext()) {
				Map.Entry localObject1 = (Map.Entry) ((Iterator) localObject2)
						.next();
				Object localObject3 = "attributeValue"
						+ ((Attribute) ((Map.Entry) localObject1).getKey())
								.getPropertyIndex();
				sqlString += " and product." + (String) localObject3 + " = ? ";
				params.add(((Map.Entry) localObject1).getValue());
			}
		}
		if ((startPrice != null) && (endPrice != null)
				&& (startPrice.compareTo(endPrice) > 0)) {
			Object localObject1 = startPrice;
			startPrice = endPrice;
			endPrice = (BigDecimal) localObject1;
		}
		if ((startPrice != null)
				&& (startPrice.compareTo(new BigDecimal(0)) >= 0)) {
			sqlString += " and product.price>=?";
			params.add(startPrice);
		}
		if ((endPrice != null) && (endPrice.compareTo(new BigDecimal(0)) >= 0)) {
			sqlString += " and product.price<=?";
			params.add(endPrice);
		}
		if ((tags != null) && (!tags.isEmpty())) {
			sqlString += " and product.tags in (?)";
			params.add(tags);// Specify whether duplicate query results will be
								// eliminated
		}
		if (isMarketable != null) {
			sqlString += " and product.isMarketable=?";
			params.add(isMarketable);
		}
		if (isList != null) {
			sqlString += " and product.isList=?";
			params.add(isList);
		}
		if (isTop != null) {
			sqlString += " and product.isTop=?";
			params.add(isTop);
		}
		if (isGift != null) {
			sqlString += " and product.isGift=?";
			params.add(isGift);
		}
		if (isOutOfStock != null) {
			if (isOutOfStock.booleanValue()) {
				sqlString += " and (product.stock !=null and product.stock<= product.allocatedStock) ";
			} else {
				sqlString += " and (product.stock !=null or product.stock> product.allocatedStock)";
			}
		}
		if (isStockAlert != null) {
			// Long stockAlertCount= SettingUtils.get().getStockAlertCount();
			int stockAlertCount = 1;
			if (isStockAlert.booleanValue()) {
				sqlString += " and (product.stock !=null and product.stock<= (product.allocatedStock+?)) ";
				params.add(stockAlertCount);
			} else {
				sqlString += " and (product.stock !=null or product.stock> (product.allocatedStock+?)) ";
				params.add(stockAlertCount);
			}
		}

		if (orderType == Product.OrderType.priceAsc) {
			sqlString += " order by product.price ASC, product.createDate DESC ";
		} else if (orderType == Product.OrderType.priceDesc) {
			sqlString += " order by product.price DESC, product.createDate DESC ";
		} else if (orderType == Product.OrderType.salesDesc) {
			sqlString += " order by product.sales DESC, product.createDate DESC ";
		} else if (orderType == Product.OrderType.scoreDesc) {
			sqlString += " order by product.score DESC, product.createDate DESC ";
		} else if (orderType == Product.OrderType.dateDesc) {
			sqlString += " order by product.createDate DESC ";
		} else {
			sqlString += " order by product.isTop DESC, product.updateDate DESC ";
		}
		Page<Product> brandPage = new Page<Product>(pageable.getPageNumber(),
				pageable.getPageSize());
		return this.find(brandPage, sqlString, params.toArray());// 测试这样是否可行，如果不行，需要重写该函数
	}

	// public Page<Product> findPage(Member member, Pageable pageable) {
	// if (member == null)
	// return new Page(Collections.emptyList(), 0L, pageable);
	// CriteriaBuilder localCriteriaBuilder = this.entityManager
	// .getCriteriaBuilder();
	// CriteriaQuery localCriteriaQuery = localCriteriaBuilder
	// .createQuery(Product.class);
	// Root localRoot = localCriteriaQuery.from(Product.class);
	// localCriteriaQuery.select(localRoot);
	// localCriteriaQuery.where(localCriteriaBuilder.equal(
	// localRoot.join("favoriteMembers"), member));
	// return super.entityManager(localCriteriaQuery, pageable);
	// }
	//
	// public Page<Object> findSalesPage(Date beginDate, Date endDate,
	// Pageable pageable) {
	// CriteriaBuilder localCriteriaBuilder = this.entityManager
	// .getCriteriaBuilder();
	// CriteriaQuery localCriteriaQuery1 = localCriteriaBuilder
	// .createQuery(Object.class);
	// Root localRoot1 = localCriteriaQuery1.from(Product.class);
	// Join localJoin1 = localRoot1.join("orderItems");
	// Join localJoin2 = localJoin1.join("order");
	// localCriteriaQuery1
	// .multiselect(new Selection[] {
	// localRoot1,
	// localCriteriaBuilder.sum(localJoin1.get("quantity")),
	// localCriteriaBuilder.sum(localCriteriaBuilder.prod(
	// localJoin1.get("quantity"),
	// localJoin1.get("price"))) });
	// Predicate localPredicate1 = localCriteriaBuilder.conjunction();
	// if (beginDate != null)
	// localPredicate1 = localCriteriaBuilder.and(
	// localPredicate1,
	// localCriteriaBuilder.greaterThanOrEqualTo(
	// localJoin1.get("createDate"), beginDate));
	// if (endDate != null)
	// localPredicate1 = localCriteriaBuilder.and(
	// localPredicate1,
	// localCriteriaBuilder.lessThanOrEqualTo(
	// localJoin1.get("createDate"), endDate));
	// localPredicate1 = localCriteriaBuilder.and(localPredicate1,
	// localCriteriaBuilder.equal(localJoin2.get("orderStatus"),
	// Order.OrderStatus.completed));
	// localPredicate1 = localCriteriaBuilder.and(localPredicate1,
	// localCriteriaBuilder.equal(localJoin2.get("paymentStatus"),
	// Order.PaymentStatus.paid));
	// localCriteriaQuery1.where(localPredicate1);
	// localCriteriaQuery1.groupBy(new Expression[] { localRoot1.get("id") });
	// CriteriaQuery localCriteriaQuery2 = localCriteriaBuilder
	// .createQuery(Long.class);
	// Root localRoot2 = localCriteriaQuery2.from(Product.class);
	// Join localJoin3 = localRoot2.join("orderItems");
	// Join localJoin4 = localJoin3.join("order");
	// Predicate localPredicate2 = localCriteriaBuilder.conjunction();
	// if (beginDate != null)
	// localPredicate2 = localCriteriaBuilder.and(
	// localPredicate2,
	// localCriteriaBuilder.greaterThanOrEqualTo(
	// localJoin3.get("createDate"), beginDate));
	// if (endDate != null)
	// localPredicate2 = localCriteriaBuilder.and(
	// localPredicate2,
	// localCriteriaBuilder.lessThanOrEqualTo(
	// localJoin3.get("createDate"), endDate));
	// localPredicate2 = localCriteriaBuilder.and(localPredicate2,
	// localCriteriaBuilder.equal(localJoin4.get("orderStatus"),
	// Order.OrderStatus.completed));
	// localCriteriaQuery2.select(localCriteriaBuilder
	// .countDistinct(localRoot2));
	// localCriteriaQuery2.where(localPredicate2);
	// Long localLong = (Long) this.entityManager
	// .createQuery(localCriteriaQuery2)
	// .setFlushMode(FlushModeType.COMMIT).getSingleResult();
	// int i = (int) Math.ceil(localLong.longValue() / pageable.getPageSize());
	// if (i < pageable.getPageNumber())
	// pageable.setPageNumber(i);
	// localCriteriaQuery1
	// .orderBy(new javax.persistence.criteria.Order[] { localCriteriaBuilder
	// .desc(localCriteriaBuilder.sum(localCriteriaBuilder
	// .prod(localJoin1.get("quantity"),
	// localJoin1.get("price")))) });
	// TypedQuery localTypedQuery = this.entityManager.createQuery(
	// localCriteriaQuery1).setFlushMode(FlushModeType.COMMIT);
	// localTypedQuery.setFirstResult((pageable.getPageNumber() - 1)
	// * pageable.getPageSize());
	// localTypedQuery.setMaxResults(pageable.getPageSize());
	// return new Page(localTypedQuery.getResultList(), localLong.longValue(),
	// pageable);
	// }
	//
	// public Long count(Member favoriteMember, Boolean isMarketable,
	// Boolean isList, Boolean isTop, Boolean isGift,
	// Boolean isOutOfStock, Boolean isStockAlert) {
	// CriteriaBuilder localCriteriaBuilder = this.entityManager
	// .getCriteriaBuilder();
	// CriteriaQuery localCriteriaQuery = localCriteriaBuilder
	// .createQuery(Product.class);
	// Root localRoot = localCriteriaQuery.from(Product.class);
	// localCriteriaQuery.select(localRoot);
	// Predicate localPredicate = localCriteriaBuilder.conjunction();
	// if (favoriteMember != null)
	// localPredicate = localCriteriaBuilder.and(
	// localPredicate,
	// localCriteriaBuilder.equal(
	// localRoot.join("favoriteMembers"), favoriteMember));
	// if (isMarketable != null)
	// localPredicate = localCriteriaBuilder.and(localPredicate,
	// localCriteriaBuilder.equal(localRoot.get("isMarketable"),
	// isMarketable));
	// if (isList != null)
	// localPredicate = localCriteriaBuilder
	// .and(localPredicate, localCriteriaBuilder.equal(
	// localRoot.get("isList"), isList));
	// if (isTop != null)
	// localPredicate = localCriteriaBuilder.and(localPredicate,
	// localCriteriaBuilder.equal(localRoot.get("isTop"), isTop));
	// if (isGift != null)
	// localPredicate = localCriteriaBuilder
	// .and(localPredicate, localCriteriaBuilder.equal(
	// localRoot.get("isGift"), isGift));
	// Path localPath1 = localRoot.get("stock");
	// Path localPath2 = localRoot.get("allocatedStock");
	// if (isOutOfStock != null)
	// if (isOutOfStock.booleanValue())
	// localPredicate = localCriteriaBuilder.and(new Predicate[] {
	// localPredicate,
	// localCriteriaBuilder.isNotNull(localPath1),
	// localCriteriaBuilder.lessThanOrEqualTo(localPath1,
	// localPath2) });
	// else
	// localPredicate = localCriteriaBuilder.and(localPredicate,
	// localCriteriaBuilder.or(localCriteriaBuilder
	// .isNull(localPath1), localCriteriaBuilder
	// .greaterThan(localPath1, localPath2)));
	// if (isStockAlert != null) {
	// Setting localSetting = SettingUtils.get();
	// if (isStockAlert.booleanValue())
	// localPredicate = localCriteriaBuilder.and(new Predicate[] {
	// localPredicate,
	// localCriteriaBuilder.isNotNull(localPath1),
	// localCriteriaBuilder.lessThanOrEqualTo(localPath1,
	// localCriteriaBuilder.sum(localPath2,
	// localSetting.getStockAlertCount())) });
	// else
	// localPredicate = localCriteriaBuilder.and(localPredicate,
	// localCriteriaBuilder.or(localCriteriaBuilder
	// .isNull(localPath1), localCriteriaBuilder
	// .greaterThan(localPath1, localCriteriaBuilder
	// .sum(localPath2, localSetting
	// .getStockAlertCount()))));
	// }
	// localCriteriaQuery.where(localPredicate);
	// return super.entityManager(localCriteriaQuery, null);
	// }
	//
	// public boolean isPurchased(Member member, Product product) {
	// if ((member == null) || (product == null))
	// return false;
	// String str =
	// "select count(*) from OrderItem orderItem where orderItem.product = :product and orderItem.order.member = :member and orderItem.order.orderStatus = :orderStatus";
	// Long localLong = (Long) this.entityManager.createQuery(str, Long.class)
	// .setFlushMode(FlushModeType.COMMIT)
	// .setParameter("product", product)
	// .setParameter("member", member)
	// .setParameter("orderStatus", Order.OrderStatus.completed)
	// .getSingleResult();
	// return localLong.longValue() > 0L;
	// }

	@Override
	public void persist(Product product) {
		Assert.notNull(product);
		setProductFullName(product);
		System.out.println("persist product there.");
		super.persist(product);
	}

	@Override
	public Product merge(Product product) {
		Assert.notNull(product);
		String str;
		if (!product.getIsGift().booleanValue()) {
			str = "delete from GiftItem giftItem where giftItem.gift = :product";
			this.getEntityManager().createQuery(str)
					.setFlushMode(FlushModeType.COMMIT)
					.setParameter("product", product).executeUpdate();
		}
		if ((!product.getIsMarketable().booleanValue())
				|| (product.getIsGift().booleanValue())) {
			str = "delete from CartItem cartItem where cartItem.product = :product";
			this.getEntityManager().createQuery(str)
					.setFlushMode(FlushModeType.COMMIT)
					.setParameter("product", product).executeUpdate();
		}
		setProductFullName(product);
		return (Product) super.merge(product);
	}

	@Override
	public void remove(Product product) {
		if (product != null) {
			Goods localGoods = product.getGoods();
			if ((localGoods != null) && (localGoods.getProducts() != null)) {
				localGoods.getProducts().remove(product);
				if (localGoods.getProducts().isEmpty())
					// this.goodsDao.remove(localGoods);
					System.out.println("goodsDao nothing");
			}
		}
		super.remove(product);
	}
	
	@Override
	public void  deleteAttributeOfProduct(Attribute attribute){
		String str1 = "attributeValue" + attribute.getPropertyIndex();
		String str2 = "update Product product set product."
				+ str1
				+ " = null where product.productCategory = :productCategory";
		this.getEntityManager()
		.createQuery(str2)
		.setFlushMode(FlushModeType.COMMIT)
		.setParameter("productCategory",
				attribute.getProductCategory()).executeUpdate();
	}
	
	@Override
	public void  updateAttributeOfProduct(Attribute attribute){
		String str1 = "attributeValue" + attribute.getPropertyIndex();
		String str2 = "update Product product set product."
				+ str1
				+ " = '"+attribute.getName()
				+"' where product.productCategory = :productCategory";
		this.getEntityManager()
		.createQuery(str2)
		.setFlushMode(FlushModeType.COMMIT)
		.setParameter("productCategory",
				attribute.getProductCategory()).executeUpdate();
	}
	
	private void setProductFullName(Product paramProduct) {
		if (paramProduct == null)
			return;
		if (StringUtils.isEmpty(paramProduct.getSn())) {
			// do
			// localObject = this.snDao.generate(Sn.Type.product);
			// while (snExists((String)localObject));
			// paramProduct.setSn((String)localObject);
		}
		Object localObject = new StringBuffer(paramProduct.getName());
		if ((paramProduct.getSpecificationValues() != null)
				&& (!paramProduct.getSpecificationValues().isEmpty())) {
			List<SpecificationValue> localArrayList = new ArrayList<SpecificationValue>(
					paramProduct.getSpecificationValues());
			Collections.sort(localArrayList, new SortSpecificationValue());
			((StringBuffer) localObject).append("[");
			int i = 0;
			Iterator<SpecificationValue> localIterator = localArrayList
					.iterator();
			while (localIterator.hasNext()) {
				if (i != 0)
					((StringBuffer) localObject).append(" ");
				((StringBuffer) localObject)
						.append(((SpecificationValue) localIterator.next())
								.getName());
				i++;
			}
			((StringBuffer) localObject).append("]");
		}
		paramProduct.setFullName(((StringBuffer) localObject).toString());
	}

}