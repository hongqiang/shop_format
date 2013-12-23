package com.hongqiang.shop.modules.util.web;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongqiang.shop.common.web.BaseController;
import com.hongqiang.shop.modules.content.service.ArticleCategoryService;
import com.hongqiang.shop.modules.content.service.ArticleService;
import com.hongqiang.shop.modules.entity.Article;
import com.hongqiang.shop.modules.entity.ArticleCategory;
import com.hongqiang.shop.modules.entity.Product;
import com.hongqiang.shop.modules.entity.ProductCategory;
import com.hongqiang.shop.modules.product.service.ProductCategoryService;
import com.hongqiang.shop.modules.product.service.ProductService;
import com.hongqiang.shop.modules.util.service.StaticService;

@Controller("adminStaticController")
@RequestMapping({"${adminPath}/static"})
public class StaticController extends BaseController
{

public enum BuildType
{
  index, article, product, other;
}


  @Autowired
  private ArticleService articleService;

  @Autowired
  private ArticleCategoryService articleCategoryService;

  @Autowired
  private ProductService productService;

  @Autowired
  private ProductCategoryService productCategoryService;

  @Autowired
  private StaticService staticService;

  @RequestMapping(value={"/build"}, method=RequestMethod.GET)
  public String build(ModelMap model)
  {
    model.addAttribute("buildTypes", BuildType.values());
    model.addAttribute("defaultBeginDate", DateUtils.addDays(new Date(), -7));
    model.addAttribute("defaultEndDate", new Date());
    model.addAttribute("articleCategoryTree", this.articleCategoryService.findChildren(null, null));
    model.addAttribute("productCategoryTree", this.productCategoryService.findChildren(null, null));
    return "/admin/static/build";
  }

  @RequestMapping(value={"/build"}, method=RequestMethod.POST)
  @ResponseBody
  public Map<String, Object> build(BuildType buildType, Long articleCategoryId, Long productCategoryId, Date beginDate, Date endDate, Integer first, Integer count)
  {
    long l1 = System.currentTimeMillis();
    Calendar localCalendar=Calendar.getInstance();
    if (beginDate != null)
    {
//      localCalendar = DateUtils.toCalendar(beginDate);
      localCalendar.setTime(beginDate);
      localCalendar.set(11, localCalendar.getActualMinimum(11));
      localCalendar.set(12, localCalendar.getActualMinimum(12));
      localCalendar.set(13, localCalendar.getActualMinimum(13));
      beginDate = localCalendar.getTime();
    }
    if (endDate != null)
    {
//      localCalendar = DateUtils.toCalendar(endDate);
      localCalendar.setTime(endDate);
      localCalendar.set(11, localCalendar.getActualMaximum(11));
      localCalendar.set(12, localCalendar.getActualMaximum(12));
      localCalendar.set(13, localCalendar.getActualMaximum(13));
      endDate = localCalendar.getTime();
    }
    if ((first == null) || (first.intValue() < 0))
      first = Integer.valueOf(0);
    if ((count == null) || (count.intValue() <= 0))
      count = Integer.valueOf(50);
    int i = 0;
    boolean bool = true;
    if (buildType == BuildType.index)
    {
      i = this.staticService.buildIndex();
    }
    else
    {
      if (buildType == BuildType.article)
      {
        ArticleCategory localObject1 = (ArticleCategory)this.articleCategoryService.find(articleCategoryId);
        List<Article> localList = this.articleService.findList((ArticleCategory)localObject1, beginDate, endDate, first, count);
        Iterator<Article> localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          Article localObject2 = (Article)localIterator.next();
          i += this.staticService.build((Article)localObject2);
        }
        first = Integer.valueOf(first.intValue() + localList.size());
        if (localList.size() == count.intValue())
          bool = false;
      }
      else if (buildType == BuildType.product)
      {
        ProductCategory localObject1 = (ProductCategory)this.productCategoryService.find(productCategoryId);
        List<Product> localList = this.productService.findList((ProductCategory)localObject1, beginDate, endDate, first, count);
        Iterator<Product> localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          Product localObject2 = (Product)localIterator.next();
          i += this.staticService.build((Product)localObject2);
        }
        first = Integer.valueOf(first.intValue() + localList.size());
        if (localList.size() == count.intValue())
          bool = false;
      }
      else if (buildType == BuildType.other)
      {
        i = this.staticService.buildOther();
      }
    }
    long l2 = System.currentTimeMillis();
    Map<String, Object> localObject2 = new HashMap<String, Object>();
    localObject2.put("first", first);
    localObject2.put("buildCount", Integer.valueOf(i));
    localObject2.put("buildTime", Long.valueOf(l2 - l1));
    localObject2.put("isCompleted", Boolean.valueOf(bool));
    return localObject2;
  }
}