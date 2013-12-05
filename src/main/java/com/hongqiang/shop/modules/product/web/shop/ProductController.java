package com.hongqiang.shop.modules.product.web.shop;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

//import net.shopxx.Pageable;
//import net.shopxx.ResourceNotFoundException;
//import net.shopxx.entity.Attribute;
//import net.shopxx.entity.Brand;
//import net.shopxx.entity.Product;
//import net.shopxx.entity.Product.OrderType;
//import net.shopxx.entity.ProductCategory;
//import net.shopxx.entity.Promotion;
//import net.shopxx.service.BrandService;
//import net.shopxx.service.ProductCategoryService;
//import net.shopxx.service.ProductService;
//import net.shopxx.service.PromotionService;
//import net.shopxx.service.SearchService;
//import net.shopxx.service.TagService;






import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongqiang.shop.common.web.BaseController;
import com.hongqiang.shop.modules.entity.Product;
import com.hongqiang.shop.modules.entity.ProductCategory;
import com.hongqiang.shop.modules.product.dao.ProductCategoryDao;
import com.hongqiang.shop.modules.product.dao.ProductDao;

@Controller("shopProductController")
@RequestMapping(value = "${frontPath}/product")
public class ProductController extends BaseController
{
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private ProductCategoryDao productCategoryDao;
	
//  @Resource(name="productServiceImpl")
//  private ProductService productService;
//
//  @Resource(name="productCategoryServiceImpl")
//  private ProductCategoryService productCategoryService;
//
//  @Resource(name="brandServiceImpl")
//  private BrandService brandService;
//
//  @Resource(name="promotionServiceImpl")
//  private PromotionService promotionService;
//
//  @Resource(name="tagServiceImpl")
//  private TagService tagService;
//
//  @Resource(name="searchServiceImpl")
//  private SearchService searchService;
//
//  @RequestMapping(value={"/history"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
//  @ResponseBody
//  public List<Product> history(Long[] ids)
//  {
//    return this.productService.findList(ids);
//  }
//
//  @RequestMapping(value={"/list/{productCategoryId}"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
//  public String list(@PathVariable Long productCategoryId, Long brandId, Long promotionId, Long[] tagIds, BigDecimal startPrice, BigDecimal endPrice, Product.OrderType orderType, Integer pageNumber, Integer pageSize, HttpServletRequest request, ModelMap model)
//  {
//    ProductCategory localProductCategory = (ProductCategory)this.productCategoryService.find(productCategoryId);
//    if (localProductCategory == null)
//      throw new ResourceNotFoundException();
//    Brand localBrand = (Brand)this.brandService.find(brandId);
//    Promotion localPromotion = (Promotion)this.promotionService.find(promotionId);
//    List localList = this.tagService.findList(tagIds);
//    HashMap localHashMap = new HashMap();
//    if (localProductCategory != null)
//    {
//      localObject = localProductCategory.getAttributes();
//      Iterator localIterator = ((Set)localObject).iterator();
//      while (localIterator.hasNext())
//      {
//        Attribute localAttribute = (Attribute)localIterator.next();
//        String str = request.getParameter("attribute_" + localAttribute.getId());
//        if (!StringUtils.isNotEmpty(str))
//          continue;
//        localHashMap.put(localAttribute, str);
//      }
//    }
//    Object localObject = new Pageable(pageNumber, pageSize);
//    model.addAttribute("orderTypes", Product.OrderType.values());//布局类型，从前端页面传过来
//    model.addAttribute("productCategory", localProductCategory);
//    model.addAttribute("brand", localBrand);
//    model.addAttribute("promotion", localPromotion);
//    model.addAttribute("tags", localList);
//    model.addAttribute("attributeValue", localHashMap);
//    model.addAttribute("startPrice", startPrice);
//    model.addAttribute("endPrice", endPrice);
//    model.addAttribute("orderType", orderType);
//    model.addAttribute("pageNumber", pageNumber);
//    model.addAttribute("pageSize", pageSize);
//    model.addAttribute("page", this.productService.findPage(localProductCategory, localBrand, localPromotion, localList, localHashMap, startPrice, endPrice, Boolean.valueOf(true), Boolean.valueOf(true), null, Boolean.valueOf(false), null, null, orderType, (Pageable)localObject));
//    return (String)"/shop/product/list";
//  }

	
  @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String list(Long brandId, Long promotionId, Long[] tagIds, BigDecimal startPrice, BigDecimal endPrice, 
		  Product.OrderType orderType, Integer pageNumber, Integer pageSize, HttpServletRequest request, ModelMap model)
  {
//    Brand localBrand = (Brand)this.brandService.find(brandId);
//    Promotion localPromotion = (Promotion)this.promotionService.find(promotionId);
//    List localList = this.tagService.findList(tagIds);
//    Pageable localPageable = new Pageable(pageNumber, pageSize);
//    model.addAttribute("orderTypes", Product.OrderType.values());
//    model.addAttribute("brand", localBrand);
//    model.addAttribute("promotion", localPromotion);
//    model.addAttribute("tags", localList);
//    model.addAttribute("startPrice", startPrice);
//    model.addAttribute("endPrice", endPrice);
//    model.addAttribute("orderType", orderType);
//    model.addAttribute("pageNumber", pageNumber);
//    model.addAttribute("pageSize", pageSize);
//    model.addAttribute("page", this.productService.findPage(null, localBrand, localPromotion, localList, null, startPrice, endPrice, Boolean.valueOf(true), Boolean.valueOf(true), null, Boolean.valueOf(false), null, null, orderType, localPageable));
//    return "/shop/product/list";
	  ProductCategory p=this.productCategoryDao.findById(1L);
	  List<Product> products=this.productDao.findList(p, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
	  for(Product product : products){
		  System.out.println(product.getName()+","+product.getSn());
	  }
	  return "modules/sys/sysLogin";
  }

//  @RequestMapping(value={"/search"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
//  public String search(String keyword, BigDecimal startPrice, BigDecimal endPrice, Product.OrderType orderType, Integer pageNumber, Integer pageSize, ModelMap model)
//  {
//    if (StringUtils.isEmpty(keyword))
//      return "/shop/common/error";
//    Pageable localPageable = new Pageable(pageNumber, pageSize);
//    model.addAttribute("orderTypes", Product.OrderType.values());
//    model.addAttribute("productKeyword", keyword);
//    model.addAttribute("startPrice", startPrice);
//    model.addAttribute("endPrice", endPrice);
//    model.addAttribute("orderType", orderType);
//    model.addAttribute("page", this.searchService.search(keyword, startPrice, endPrice, orderType, localPageable));
//    return "shop/product/search";
//  }
//
//  @RequestMapping(value={"/hits/{id}"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
//  @ResponseBody
//  public Long hits(@PathVariable Long id)
//  {
//    return Long.valueOf(this.productService.viewHits(id));
//  }
}