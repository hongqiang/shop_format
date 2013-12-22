package com.hongqiang.shop.modules.content.service;

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
import com.hongqiang.shop.modules.content.dao.ReviewDao;
import com.hongqiang.shop.modules.entity.Member;
import com.hongqiang.shop.modules.entity.Product;
import com.hongqiang.shop.modules.entity.Review;
import com.hongqiang.shop.modules.product.dao.ProductDao;
import com.hongqiang.shop.modules.util.service.StaticService;

@Service
public class ReviewServiceImpl extends BaseService
  implements ReviewService
{

  @Autowired
  private ReviewDao reviewDao;

 @Autowired
  private ProductDao productDao;

  @Autowired
  private StaticService staticService;

  @Transactional(readOnly=true)
  public List<Review> findList(Member member, Product product, Review.Type type, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders)
  {
    return this.reviewDao.findList(member, product, type, isShow, count, filters, orders);
  }

  @Transactional(readOnly=true)
  @Cacheable({"review"})
  public List<Review> findList(Member member, Product product, Review.Type type, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion)
  {
    return this.reviewDao.findList(member, product, type, isShow, count, filters, orders);
  }

  @Transactional(readOnly=true)
  public Page<Review> findPage(Member member, Product product, Review.Type type, Boolean isShow, Pageable pageable)
  {
    return this.reviewDao.findPage(member, product, type, isShow, pageable);
  }

  @Transactional(readOnly=true)
  public Long count(Member member, Product product, Review.Type type, Boolean isShow)
  {
    return this.reviewDao.count(member, product, type, isShow);
  }

  @Transactional(readOnly=true)
  public boolean isReviewed(Member member, Product product)
  {
    return this.reviewDao.isReviewed(member, product);
  }

  @Transactional(readOnly=true)
  public Review find(Long id){
	return this.reviewDao.find(id);
  }
  
  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public void save(Review review)
  {
    this.reviewDao.persist(review);
    Product localProduct = review.getProduct();
    if (localProduct != null)
    {
      this.reviewDao.flush();
      long l1 = this.reviewDao.calculateTotalScore(localProduct);
      long l2 = this.reviewDao.calculateScoreCount(localProduct);
      localProduct.setTotalScore(Long.valueOf(l1));
      localProduct.setScoreCount(Long.valueOf(l2));
      this.productDao.merge(localProduct);
      this.reviewDao.flush();
      this.staticService.build(localProduct);
    }
  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public Review update(Review review)
  {
    Review localReview = (Review)this.reviewDao.merge(review);
    Product localProduct = localReview.getProduct();
    if (localProduct != null)
    {
      this.reviewDao.flush();
      long l1 = this.reviewDao.calculateTotalScore(localProduct);
      long l2 = this.reviewDao.calculateScoreCount(localProduct);
      localProduct.setTotalScore(Long.valueOf(l1));
      localProduct.setScoreCount(Long.valueOf(l2));
      this.productDao.merge(localProduct);
      this.reviewDao.flush();
      this.staticService.build(localProduct);
    }
    return localReview;
  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public Review update(Review review, String[] ignoreProperties)
  {
    return (Review)this.reviewDao.update(review, ignoreProperties);
  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public void delete(Long id)
  {
    this.reviewDao.delete(id);
  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public void delete(Long[] ids)
  {
    if (ids != null)
		for (Long id : ids)
			this.reviewDao.delete(id);
  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public void delete(Review review)
  {
    if (review != null)
    {
      this.reviewDao.delete(review);
      Product localProduct = review.getProduct();
      if (localProduct != null)
      {
        this.reviewDao.flush();
        long l1 = this.reviewDao.calculateTotalScore(localProduct);
        long l2 = this.reviewDao.calculateScoreCount(localProduct);
        localProduct.setTotalScore(Long.valueOf(l1));
        localProduct.setScoreCount(Long.valueOf(l2));
        this.productDao.merge(localProduct);
        this.reviewDao.flush();
        this.staticService.build(localProduct);
      }
    }
  }
}