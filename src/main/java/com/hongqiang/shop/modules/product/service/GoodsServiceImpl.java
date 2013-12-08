package com.hongqiang.shop.modules.product.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import com.google.common.base.Predicate;
import com.hongqiang.shop.common.service.BaseService;
import com.hongqiang.shop.modules.entity.Goods;
import com.hongqiang.shop.modules.entity.Product;
import com.hongqiang.shop.modules.product.dao.GoodsDao;
import com.hongqiang.shop.modules.product.dao.ProductDao;

@Service
public class GoodsServiceImpl extends BaseService
  implements GoodsService
{
  @Autowired
  private GoodsDao goodsDao;

  @Autowired
  private ProductDao productDao;

//  @Autowired
//  private StaticService staticService;

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public void save(Goods goods)
  {
    Assert.notNull(goods);
    this.goodsDao.save(goods);
    this.goodsDao.flush();
//    if (goods.getProducts() != null)
//    {
//      Iterator <Product>localIterator = goods.getProducts().iterator();
//      while (localIterator.hasNext())
//      {
//        Product localProduct = (Product)localIterator.next();
//        this.staticService.build(localProduct);
//      }
//    }
  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public Goods update(Goods goods)
  {
    Assert.notNull(goods);
    HashSet<Product> localHashSet = new HashSet<Product>();
    Set<Product>products = goods.getProducts();
    for (Product tempProduct : products) {
    	if ((tempProduct !=null) && (tempProduct.getId() !=null)) {
			localHashSet.add(tempProduct);
		}
	}
    List<Product> localList = this.productDao.findList(goods, localHashSet);
//    Object localObject2 = localList.iterator();
//    while (((Iterator)localObject2).hasNext())
//    {
//      localObject1 = (Product)((Iterator)localObject2).next();
//      this.staticService.delete((Product)localObject1);
//    }
    Object localObject1 = (Goods)this.goodsDao.merge(goods);
    this.goodsDao.flush();
//    if (((Goods)localObject1).getProducts() != null)
//    {
//      Iterator localIterator = ((Goods)localObject1).getProducts().iterator();
//      while (localIterator.hasNext())
//      {
//        localObject2 = (Product)localIterator.next();
//        this.staticService.build((Product)localObject2);
//      }
//    }
    return (Goods)localObject1;
  }

//  @Transactional
//  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
//  public Goods update(Goods goods, String[] ignoreProperties)
//  {
//    return (Goods)this.goodsDao.update(goods, ignoreProperties);
//  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public void delete(Long id)
  {
    this.goodsDao.delete(id);
  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public void delete(Long[] ids)
  {
	  if (ids != null)
			for (Long localSerializable : ids)
				this.goodsDao.delete(localSerializable);
  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public void delete(Goods goods)
  {
//    if ((goods != null) && (goods.getProducts() != null))
//    {
//      Iterator localIterator = goods.getProducts().iterator();
//      while (localIterator.hasNext())
//      {
//        Product localProduct = (Product)localIterator.next();
//        this.staticService.delete(localProduct);
//      }
//    }
    this.goodsDao.delete(goods);
  }
}