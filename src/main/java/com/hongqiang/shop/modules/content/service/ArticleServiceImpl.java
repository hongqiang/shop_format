package com.hongqiang.shop.modules.content.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

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
import com.hongqiang.shop.modules.content.dao.ArticleDao;
import com.hongqiang.shop.modules.entity.Article;
import com.hongqiang.shop.modules.entity.ArticleCategory;
import com.hongqiang.shop.modules.entity.Tag;
import com.hongqiang.shop.modules.util.service.StaticService;

@Service
public class ArticleServiceImpl extends BaseService
  implements ArticleService, DisposableBean
{
  private long systemTime = System.currentTimeMillis();

//  @Autowired
//  @Resource(name="ehCacheManager")
//  private CacheManager cacheManager;

  @Autowired
  private ArticleDao articleDao;

  @Autowired
  private StaticService staticService;

  @Transactional(readOnly=true)
  public List<Article> findList(ArticleCategory articleCategory, List<Tag> tags, Integer count, List<Filter> filters, List<Order> orders)
  {
    return this.articleDao.findList(articleCategory, tags, count, filters, orders);
  }

  @Transactional(readOnly=true)
  @Cacheable({"article"})
  public List<Article> findList(ArticleCategory articleCategory, List<Tag> tags, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion)
  {
    return this.articleDao.findList(articleCategory, tags, count, filters, orders);
  }

  @Transactional(readOnly=true)
  public List<Article> findList(ArticleCategory articleCategory, Date beginDate, Date endDate, Integer first, Integer count)
  {
    return this.articleDao.findList(articleCategory, beginDate, endDate, first, count);
  }

  @Transactional(readOnly=true)
  public Page<Article> findPage(ArticleCategory articleCategory, List<Tag> tags, Pageable pageable)
  {
    return this.articleDao.findPage(articleCategory, tags, pageable);
  }
  
  @Transactional(readOnly=true)
  public Page<Article> findPage(Pageable pageable){
	return this.articleDao.findPage(pageable);
  }
  
  @Transactional(readOnly=true)
  public Article find(Long id){
	return this.articleDao.find(id);
  }
  
  public long viewHits(Long id)
  {
//    Ehcache localEhcache = this.cacheManager.getEhcache("articleHits");
//    Element localElement = localEhcache.get(id);
	Long tempLong = 0L;
//    if (localElement != null)
//    {
//      tempLong = (Long)localElement.getObjectValue();
//    }
//    else
//    {
//      Article localArticle = (Article)this.articleDao.find(id);
//      if (localArticle == null)
//        return 0L;
//      tempLong = localArticle.getHits();
//    }
//    Long localLong = Long.valueOf(tempLong.longValue() + 1L);
//    localEhcache.put(new Element(id, localLong));
//    long l = System.currentTimeMillis();
//    if (l > this.systemTime + 600000L)
//    {
//      this.systemTime = l;
//      destroyCache();
//      localEhcache.removeAll();
//    }
//    return localLong.longValue();
	return tempLong;
  }

  public void destroy()
  {
    destroyCache();
  }

  private void destroyCache()
  {
//    Ehcache localEhcache = this.cacheManager.getEhcache("articleHits");
//    @SuppressWarnings("unchecked")
//	List<Long> localList = localEhcache.getKeys();
//    Iterator<Long> localIterator = localList.iterator();
//    while (localIterator.hasNext())
//    {
//      Long localLong = (Long)localIterator.next();
//      Article localArticle = (Article)this.articleDao.find(localLong);
//      if (localArticle == null)
//        continue;
//      Element localElement = localEhcache.get(localLong);
//      long l = ((Long)localElement.getObjectValue()).longValue();
//      localArticle.setHits(Long.valueOf(l));
//      this.articleDao.merge(localArticle);
//    }
  }

  @Transactional
  @CacheEvict(value={"article", "articleCategory"}, allEntries=true)
  public void save(Article article)
  {
    Assert.notNull(article);
    this.articleDao.persist(article);
    this.articleDao.flush();
    this.staticService.build(article);
  }

  @Transactional
  @CacheEvict(value={"article", "articleCategory"}, allEntries=true)
  public Article update(Article article)
  {
    Assert.notNull(article);
    Article localArticle = (Article)this.articleDao.merge(article);
    this.articleDao.flush();
    this.staticService.build(localArticle);
    return localArticle;
  }

  @Transactional
  @CacheEvict(value={"article", "articleCategory"}, allEntries=true)
  public Article update(Article article, String[] ignoreProperties)
  {
    return (Article)this.articleDao.update(article, ignoreProperties);
  }

  @Transactional
  @CacheEvict(value={"article", "articleCategory"}, allEntries=true)
  public void delete(Long id)
  {
    this.articleDao.delete(id);
  }

  @Transactional
  @CacheEvict(value={"article", "articleCategory"}, allEntries=true)
  public void delete(Long[] ids)
  {
     if (ids != null)
		for (Long localSerializable : ids)
			this.articleDao.delete(localSerializable);
  }

  @Transactional
  @CacheEvict(value={"article", "articleCategory"}, allEntries=true)
  public void delete(Article article)
  {
    if (article != null)
      this.staticService.delete(article);
    this.articleDao.delete(article);
  }
}