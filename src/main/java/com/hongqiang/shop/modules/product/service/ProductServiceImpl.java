package com.hongqiang.shop.modules.product.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.LockModeType;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.service.BaseService;
import com.hongqiang.shop.common.utils.Filter;
import com.hongqiang.shop.common.utils.Order;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Attribute;
import com.hongqiang.shop.modules.entity.Brand;
import com.hongqiang.shop.modules.entity.Member;
import com.hongqiang.shop.modules.entity.Product;
import com.hongqiang.shop.modules.entity.ProductCategory;
import com.hongqiang.shop.modules.entity.Promotion;
import com.hongqiang.shop.modules.entity.Tag;
import com.hongqiang.shop.modules.product.dao.ProductDao;

@Service
public class ProductServiceImpl extends BaseService implements ProductService,
		DisposableBean {
	private long currentTimeMillis = System.currentTimeMillis();

//	@Autowired
//	private CacheManager cacheManager;

	@Autowired
	private ProductDao productDao;

	// @Autowired
	// private StaticService staticService;

	@Transactional(readOnly = true)
	public boolean snExists(String sn) {
		return this.productDao.snExists(sn);
	}

	@Transactional(readOnly = true)
	public Product findBySn(String sn) {
		return this.productDao.findBySn(sn);
	}

	@Transactional(readOnly = true)
	public boolean snUnique(String previousSn, String currentSn) {
		if (StringUtils.equalsIgnoreCase(previousSn, currentSn))
			return true;
		return !this.productDao.snExists(currentSn);
	}

	@Transactional(readOnly = true)
	public List<Product> search(String keyword, Boolean isGift, Integer count) {
		return this.productDao.search(keyword, isGift, count);
	}
	
	public List<Product> findList(Long[] ids) {
		List<Product> localArrayList = new ArrayList<Product>();
		int i=0;
		if (ids != null)
			for (Long id : ids) {
				Product localObject = this.productDao.findById(id);
				if (localObject == null)
					continue;
				localArrayList.add(localObject);
			}
		return localArrayList;
	}
	
	@Transactional(readOnly = true)
	public List<Product> findList(ProductCategory productCategory, Brand brand,
			Promotion promotion, List<Tag> tags,
			Map<Attribute, String> attributeValue, BigDecimal startPrice,
			BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock,
			Boolean isStockAlert, Product.OrderType orderType, Integer count,
			List<Filter> filters, List<Order> orders) {
		return this.productDao.findList(productCategory, brand, promotion,
				tags, attributeValue, startPrice, endPrice, isMarketable,
				isList, isTop, isGift, isOutOfStock, isStockAlert, orderType,
				count, filters, orders);
	}

	@Transactional(readOnly = true)
//	@Cacheable({ "product" })
	public List<Product> findList(ProductCategory productCategory, Brand brand,
			Promotion promotion, List<Tag> tags,
			Map<Attribute, String> attributeValue, BigDecimal startPrice,
			BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock,
			Boolean isStockAlert, Product.OrderType orderType, Integer count,
			List<Filter> filters, List<Order> orders, String cacheRegion) {
		return this.productDao.findList(productCategory, brand, promotion,
				tags, attributeValue, startPrice, endPrice, isMarketable,
				isList, isTop, isGift, isOutOfStock, isStockAlert, orderType,
				count, filters, orders);
	}

	@Transactional(readOnly = true)
	public List<Product> findList(ProductCategory productCategory,
			Date beginDate, Date endDate, Integer first, Integer count) {
		return this.productDao.findList(productCategory, beginDate, endDate,
				first, count);
	}

	@Transactional(readOnly = true)
	public Page<Product> findPage(ProductCategory productCategory, Brand brand,
			Promotion promotion, List<Tag> tags,
			Map<Attribute, String> attributeValue, BigDecimal startPrice,
			BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock,
			Boolean isStockAlert, Product.OrderType orderType, Pageable pageable) {
		return this.productDao.findPage(productCategory, brand, promotion,
				tags, attributeValue, startPrice, endPrice, isMarketable,
				isList, isTop, isGift, isOutOfStock, isStockAlert, orderType,
				pageable);
	}

//	@Transactional(readOnly = true)
//	public Page<Product> findPage(Member member, Pageable pageable) {
//		return this.productDao.findPage(member, pageable);
//	}
//
//	@Transactional(readOnly = true)
//	public Page<Object> findSalesPage(Date beginDate, Date endDate,
//			Pageable pageable) {
//		return this.productDao.findSalesPage(beginDate, endDate, pageable);
//	}
//
//	@Transactional(readOnly = true)
//	public Long count(Member favoriteMember, Boolean isMarketable,
//			Boolean isList, Boolean isTop, Boolean isGift,
//			Boolean isOutOfStock, Boolean isStockAlert) {
//		return this.productDao.count(favoriteMember, isMarketable, isList,
//				isTop, isGift, isOutOfStock, isStockAlert);
//	}
//
//	@Transactional(readOnly = true)
//	public boolean isPurchased(Member member, Product product) {
//		return this.productDao.isPurchased(member, product);
//	}

//	public long viewHits(Long id) {
//		Ehcache localEhcache = this.cacheManager.getEhcache("productHits");
//		Element localElement = localEhcache.get(id);
//		if (localElement != null) {
//			localLong = (Long) localElement.getObjectValue();
//		} else {
//			Product localProduct = (Product) this.productDao.find(id);
//			if (localProduct == null)
//				return 0L;
//			localLong = localProduct.getHits();
//		}
//		Long localLong = Long.valueOf(localLong.longValue() + 1L);
//		localEhcache.put(new Element(id, localLong));
//		long l = System.currentTimeMillis();
//		if (l > this.currentTimeMillis + 600000L) {
//			this.currentTimeMillis = l;
//			currentTimeMillis();
//			localEhcache.removeAll();
//		}
//		return localLong.longValue();
//	}
//
	public void destroy() {
//		currentTimeMillis();
	}
//
//	private void currentTimeMillis() {
//		Ehcache localEhcache = this.cacheManager.getEhcache("productHits");
//		List localList = localEhcache.getKeys();
//		Iterator localIterator = localList.iterator();
//		while (localIterator.hasNext()) {
//			Long localLong = (Long) localIterator.next();
//			Product localProduct = (Product) this.productDao.find(localLong);
//			if (localProduct == null)
//				continue;
//			this.productDao.lock(localProduct, LockModeType.PESSIMISTIC_WRITE);
//			Element localElement = localEhcache.get(localLong);
//			long l1 = ((Long) localElement.getObjectValue()).longValue();
//			long l2 = l1 - localProduct.getHits().longValue();
//			Calendar localCalendar1 = Calendar.getInstance();
//			Calendar localCalendar2 = DateUtils.toCalendar(localProduct
//					.getWeekHitsDate());
//			Calendar localCalendar3 = DateUtils.toCalendar(localProduct
//					.getMonthHitsDate());
//			if ((localCalendar1.get(1) != localCalendar2.get(1))
//					|| (localCalendar1.get(3) > localCalendar2.get(3)))
//				localProduct.setWeekHits(Long.valueOf(l2));
//			else
//				localProduct.setWeekHits(Long.valueOf(localProduct
//						.getWeekHits().longValue() + l2));
//			if ((localCalendar1.get(1) != localCalendar3.get(1))
//					|| (localCalendar1.get(2) > localCalendar3.get(2)))
//				localProduct.setMonthHits(Long.valueOf(l2));
//			else
//				localProduct.setMonthHits(Long.valueOf(localProduct
//						.getMonthHits().longValue() + l2));
//			localProduct.setHits(Long.valueOf(l1));
//			localProduct.setWeekHitsDate(new Date());
//			localProduct.setMonthHitsDate(new Date());
//			this.productDao.merge(localProduct);
//		}
//	}


	
	@Transactional
//	@CacheEvict(value = { "product", "productCategory", "review",
//			"consultation" }, allEntries = true)
	public void save(Product product) {
		Assert.notNull(product);
		this.productDao.persist(product);
		this.productDao.flush();
//		this.staticService.build(product);
	}

	@Transactional
//	@CacheEvict(value = { "product", "productCategory", "review",
//			"consultation" }, allEntries = true)
	public Product update(Product product) {
		Assert.notNull(product);
		Product localProduct = (Product)  this.productDao.merge(product);
		this.productDao.flush();
//		this.staticService.build(localProduct);
		return localProduct;
	}

	@Transactional
//	@CacheEvict(value = { "product", "productCategory", "review",
//			"consultation" }, allEntries = true)
	public Product update(Product product, String[] ignoreProperties) {
//		return (Product) super.update(product, ignoreProperties);
		return (Product) this.productDao.merge(product);
	}

	@Transactional
//	@CacheEvict(value = { "product", "productCategory", "review",
//			"consultation" }, allEntries = true)
	public void delete(Long id) {
		this.productDao.delete(id);
	}

	@Transactional
//	@CacheEvict(value = { "product", "productCategory", "review",
//			"consultation" }, allEntries = true)
	public void delete(Long[] ids) {
		  if (ids != null)
				for (Long localSerializable : ids)
					this.productDao.delete(localSerializable);
	}

	@Transactional
//	@CacheEvict(value = { "product", "productCategory", "review",
//			"consultation" }, allEntries = true)
	public void delete(Product product) {
//		if (product != null)
//			this.staticService.delete(product);
		this.productDao.delete(product);
	}
}