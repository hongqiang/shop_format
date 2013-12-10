package com.hongqiang.shop.modules.product.web.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

//import net.shopxx.FileInfo.FileType;
//import net.shopxx.Message;
//import net.shopxx.Pageable;
//import net.shopxx.Setting;
//import net.shopxx.entity.Attribute;
//import net.shopxx.entity.Brand;
//import net.shopxx.entity.Goods;
//import net.shopxx.entity.MemberRank;
//import net.shopxx.entity.Parameter;
//import net.shopxx.entity.ParameterGroup;
//import net.shopxx.entity.Product;
//import net.shopxx.entity.Product.OrderType;
//import net.shopxx.entity.ProductCategory;
//import net.shopxx.entity.ProductImage;
//import net.shopxx.entity.Promotion;
//import net.shopxx.entity.Specification;
//import net.shopxx.entity.SpecificationValue;
//import net.shopxx.entity.Tag.Type;
//import net.shopxx.service.BrandService;
//import net.shopxx.service.FileService;
//import net.shopxx.service.GoodsService;
//import net.shopxx.service.MemberRankService;
//import net.shopxx.service.ProductCategoryService;
//import net.shopxx.service.ProductImageService;
//import net.shopxx.service.ProductService;
//import net.shopxx.service.PromotionService;
//import net.shopxx.service.SpecificationService;
//import net.shopxx.service.SpecificationValueService;
//import net.shopxx.service.TagService;
//import net.shopxx.util.SettingUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hongqiang.shop.common.utils.Message;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.common.utils.Setting;
import com.hongqiang.shop.common.utils.SettingUtils;
import com.hongqiang.shop.common.web.BaseController;
import com.hongqiang.shop.modules.entity.Attribute;
import com.hongqiang.shop.modules.entity.Brand;
import com.hongqiang.shop.modules.entity.Goods;
import com.hongqiang.shop.modules.entity.MemberRank;
import com.hongqiang.shop.modules.entity.Parameter;
import com.hongqiang.shop.modules.entity.ParameterGroup;
import com.hongqiang.shop.modules.entity.Product;
import com.hongqiang.shop.modules.entity.ProductCategory;
import com.hongqiang.shop.modules.entity.ProductImage;
import com.hongqiang.shop.modules.entity.Promotion;
import com.hongqiang.shop.modules.entity.Specification;
import com.hongqiang.shop.modules.entity.SpecificationValue;
import com.hongqiang.shop.modules.entity.Tag;
import com.hongqiang.shop.modules.product.service.BrandService;
import com.hongqiang.shop.modules.product.service.GoodsService;
import com.hongqiang.shop.modules.product.service.ProductCategoryService;
import com.hongqiang.shop.modules.product.service.ProductService;
import com.hongqiang.shop.modules.product.service.SpecificationService;
import com.hongqiang.shop.modules.product.service.SpecificationValueService;
import com.hongqiang.shop.modules.product.service.TagService;

@Controller("adminProductController")
@RequestMapping({"/admin/product"})
public class ProductController extends BaseController
{

  @Resource(name="productServiceImpl")
  private ProductService productService;

  @Resource(name="productCategoryServiceImpl")
  private ProductCategoryService productCategoryService;

  @Resource(name="goodsServiceImpl")
  private GoodsService goodsService;

  @Resource(name="brandServiceImpl")
  private BrandService brandService;

//  @Resource(name="promotionServiceImpl")
//  private PromotionService promotionService;

  @Resource(name="tagServiceImpl")
  private TagService tagService;
//
//  @Resource(name="memberRankServiceImpl")
//  private MemberRankService memberRankService;
//
//  @Resource(name="productImageServiceImpl")
//  private ProductImageService productImageService;

  @Resource(name="specificationServiceImpl")
  private SpecificationService specificationService;

  @Resource(name="specificationValueServiceImpl")
  private SpecificationValueService specificationValueService;

//  @Resource(name="fileServiceImpl")
//  private FileService fileService;

  @RequestMapping(value={"/check_sn"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public boolean checkSn(String previousSn, String sn)
  {
    if (StringUtils.isEmpty(sn))
      return false;
    return this.productService.snUnique(previousSn, sn);
  }

  @RequestMapping(value={"/parameter_groups"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public Set<ParameterGroup> parameterGroups(Long id)
  {
    ProductCategory localProductCategory = (ProductCategory)this.productCategoryService.find(id);
    return localProductCategory.getParameterGroups();
  }

  @RequestMapping(value={"/attributes"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public Set<Attribute> attributes(Long id)
  {
    ProductCategory localProductCategory = (ProductCategory)this.productCategoryService.find(id);
    return localProductCategory.getAttributes();
  }

//  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
//  public String add(ModelMap model)
//  {
//    model.addAttribute("productCategoryTree", this.productCategoryService.findTree());
//    model.addAttribute("brands", this.brandService.findAll());
//    model.addAttribute("tags", this.tagService.findList(Tag.Type.product));
//    model.addAttribute("memberRanks", this.memberRankService.findAll());
//    model.addAttribute("specifications", this.specificationService.findAll());
//    return "/admin/product/add";
//  }
//
//  @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
//  public String save(Product product, Long productCategoryId, Long brandId, Long[] tagIds, Long[] specificationIds, HttpServletRequest request, RedirectAttributes redirectAttributes)
//  {
//    Object localObject1 = product.getProductImages().iterator();
//    while (((Iterator)localObject1).hasNext())
//    {
//      localObject3 = (ProductImage)((Iterator)localObject1).next();
//      if ((localObject3 == null) || (((ProductImage)localObject3).isEmpty()))
//      {
//        ((Iterator)localObject1).remove();
//      }
//      else
//      {
//        if ((((ProductImage)localObject3).getFile() == null) || (((ProductImage)localObject3).getFile().isEmpty()) || (this.fileService.isValid(FileInfo.FileType.image, ((ProductImage)localObject3).getFile())))
//          continue;
//        IIIllIlI(redirectAttributes, Message.error("admin.upload.invalid", new Object[0]));
//        return "redirect:add.jhtml";
//      }
//    }
//    product.setProductCategory((ProductCategory)this.productCategoryService.find(productCategoryId));
//    product.setBrand((Brand)this.brandService.find(brandId));
//    product.setTags(new HashSet(this.tagService.findList(tagIds)));
//    if (!IIIllIlI(product, new Class[0]))
//      return ERROR_PAGE;
//    if ((StringUtils.isNotEmpty(product.getSn())) && (this.productService.snExists(product.getSn())))
//      return ERROR_PAGE;
//    if (product.getMarketPrice() == null)
//    {
//      localObject1 = IIIllIlI(product.getPrice());
//      product.setMarketPrice((BigDecimal)localObject1);
//    }
//    if (product.getPoint() == null)
//    {
//      long l = IIIllIll(product.getPrice());
//      product.setPoint(Long.valueOf(l));
//    }
//    product.setFullName(null);
//    product.setAllocatedStock(Integer.valueOf(0));
//    product.setScore(Float.valueOf(0.0F));
//    product.setTotalScore(Long.valueOf(0L));
//    product.setScoreCount(Long.valueOf(0L));
//    product.setHits(Long.valueOf(0L));
//    product.setWeekHits(Long.valueOf(0L));
//    product.setMonthHits(Long.valueOf(0L));
//    product.setSales(Long.valueOf(0L));
//    product.setWeekSales(Long.valueOf(0L));
//    product.setMonthSales(Long.valueOf(0L));
//    product.setWeekHitsDate(new Date());
//    product.setMonthHitsDate(new Date());
//    product.setWeekSalesDate(new Date());
//    product.setMonthSalesDate(new Date());
//    product.setReviews(null);
//    product.setConsultations(null);
//    product.setFavoriteMembers(null);
//    product.setPromotions(null);
//    product.setCartItems(null);
//    product.setOrderItems(null);
//    product.setGiftItems(null);
//    product.setProductNotifies(null);
//    Object localObject3 = this.memberRankService.findAll().iterator();
//    Object localObject4;
//    while (((Iterator)localObject3).hasNext())
//    {
//      localObject2 = (MemberRank)((Iterator)localObject3).next();
//      localObject4 = request.getParameter("memberPrice_" + ((MemberRank)localObject2).getId());
//      if ((StringUtils.isNotEmpty((String)localObject4)) && (new BigDecimal((String)localObject4).compareTo(new BigDecimal(0)) >= 0))
//        product.getMemberPrice().put(localObject2, new BigDecimal((String)localObject4));
//      else
//        product.getMemberPrice().remove(localObject2);
//    }
//    localObject3 = product.getProductImages().iterator();
//    while (((Iterator)localObject3).hasNext())
//    {
//      localObject2 = (ProductImage)((Iterator)localObject3).next();
//      this.productImageService.build((ProductImage)localObject2);
//    }
//    Collections.sort(product.getProductImages());
//    if ((product.getImage() == null) && (product.getThumbnail() != null))
//      product.setImage(product.getThumbnail());
//    localObject3 = product.getProductCategory().getParameterGroups().iterator();
//    Object localObject5;
//    Object localObject6;
//    while (((Iterator)localObject3).hasNext())
//    {
//      localObject2 = (ParameterGroup)((Iterator)localObject3).next();
//      localObject5 = ((ParameterGroup)localObject2).getParameters().iterator();
//      while (((Iterator)localObject5).hasNext())
//      {
//        localObject4 = (Parameter)((Iterator)localObject5).next();
//        localObject6 = request.getParameter("parameter_" + ((Parameter)localObject4).getId());
//        if (StringUtils.isNotEmpty((String)localObject6))
//          product.getParameterValue().put(localObject4, localObject6);
//        else
//          product.getParameterValue().remove(localObject4);
//      }
//    }
//    localObject3 = product.getProductCategory().getAttributes().iterator();
//    while (((Iterator)localObject3).hasNext())
//    {
//      localObject2 = (Attribute)((Iterator)localObject3).next();
//      localObject4 = request.getParameter("attribute_" + ((Attribute)localObject2).getId());
//      if (StringUtils.isNotEmpty((String)localObject4))
//        product.setAttributeValue((Attribute)localObject2, (String)localObject4);
//      else
//        product.setAttributeValue((Attribute)localObject2, null);
//    }
//    Object localObject2 = new Goods();
//    localObject3 = new ArrayList();
//    if ((specificationIds != null) && (specificationIds.length > 0))
//    {
//      for (int i = 0; i < specificationIds.length; i++)
//      {
//        localObject5 = (Specification)this.specificationService.find(specificationIds[i]);
//        localObject6 = request.getParameterValues("specification_" + ((Specification)localObject5).getId());
//        if ((localObject6 == null) || (localObject6.length <= 0))
//          continue;
//        for (int j = 0; j < localObject6.length; j++)
//        {
//          if (i == 0)
//            if (j == 0)
//            {
//              product.setGoods((Goods)localObject2);
//              product.setSpecifications(new HashSet());
//              product.setSpecificationValues(new HashSet());
//              ((List)localObject3).add(product);
//            }
//            else
//            {
//              localProduct = new Product();
//              BeanUtils.copyProperties(product, localProduct);
//              localProduct.setId(null);
//              localProduct.setCreateDate(null);
//              localProduct.setModifyDate(null);
//              localProduct.setSn(null);
//              localProduct.setFullName(null);
//              localProduct.setAllocatedStock(Integer.valueOf(0));
//              localProduct.setIsList(Boolean.valueOf(false));
//              localProduct.setScore(Float.valueOf(0.0F));
//              localProduct.setTotalScore(Long.valueOf(0L));
//              localProduct.setScoreCount(Long.valueOf(0L));
//              localProduct.setHits(Long.valueOf(0L));
//              localProduct.setWeekHits(Long.valueOf(0L));
//              localProduct.setMonthHits(Long.valueOf(0L));
//              localProduct.setSales(Long.valueOf(0L));
//              localProduct.setWeekSales(Long.valueOf(0L));
//              localProduct.setMonthSales(Long.valueOf(0L));
//              localProduct.setWeekHitsDate(new Date());
//              localProduct.setMonthHitsDate(new Date());
//              localProduct.setWeekSalesDate(new Date());
//              localProduct.setMonthSalesDate(new Date());
//              localProduct.setGoods((Goods)localObject2);
//              localProduct.setReviews(null);
//              localProduct.setConsultations(null);
//              localProduct.setFavoriteMembers(null);
//              localProduct.setSpecifications(new HashSet());
//              localProduct.setSpecificationValues(new HashSet());
//              localProduct.setPromotions(null);
//              localProduct.setCartItems(null);
//              localProduct.setOrderItems(null);
//              localProduct.setGiftItems(null);
//              localProduct.setProductNotifies(null);
//              ((List)localObject3).add(localProduct);
//            }
//          Product localProduct = (Product)((List)localObject3).get(j);
//          SpecificationValue localSpecificationValue = (SpecificationValue)this.specificationValueService.find(Long.valueOf(localObject6[j]));
//          localProduct.getSpecifications().add(localObject5);
//          localProduct.getSpecificationValues().add(localSpecificationValue);
//        }
//      }
//    }
//    else
//    {
//      product.setGoods((Goods)localObject2);
//      product.setSpecifications(null);
//      product.setSpecificationValues(null);
//      ((List)localObject3).add(product);
//    }
//    ((Goods)localObject2).getProducts().clear();
//    ((Goods)localObject2).getProducts().addAll((Collection)localObject3);
//    this.goodsService.save(localObject2);
//    IIIllIlI(redirectAttributes, IIIlllII);
//    return (String)(String)(String)(String)(String)(String)"redirect:list.jhtml";
//  }
//
//  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
//  public String edit(Long id, ModelMap model)
//  {
//    model.addAttribute("productCategoryTree", this.productCategoryService.findTree());
//    model.addAttribute("brands", this.brandService.findAll());
//    model.addAttribute("tags", this.tagService.findList(Tag.Type.product));
//    model.addAttribute("memberRanks", this.memberRankService.findAll());
//    model.addAttribute("specifications", this.specificationService.findAll());
//    model.addAttribute("product", this.productService.find(id));
//    return "/admin/product/edit";
//  }
//
//  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
//  public String update(Product product, Long productCategoryId, Long brandId, Long[] tagIds, Long[] specificationIds, Long[] specificationProductIds, HttpServletRequest request, RedirectAttributes redirectAttributes)
//  {
//    Object localObject1 = product.getProductImages().iterator();
//    Object localObject2;
//    while (((Iterator)localObject1).hasNext())
//    {
//      localObject2 = (ProductImage)((Iterator)localObject1).next();
//      if ((localObject2 == null) || (((ProductImage)localObject2).isEmpty()))
//      {
//        ((Iterator)localObject1).remove();
//      }
//      else
//      {
//        if ((((ProductImage)localObject2).getFile() == null) || (((ProductImage)localObject2).getFile().isEmpty()) || (this.fileService.isValid(FileInfo.FileType.image, ((ProductImage)localObject2).getFile())))
//          continue;
//        IIIllIlI(redirectAttributes, Message.error("admin.upload.invalid", new Object[0]));
//        return "redirect:edit.jhtml?id=" + product.getId();
//      }
//    }
//    product.setProductCategory((ProductCategory)this.productCategoryService.find(productCategoryId));
//    product.setBrand((Brand)this.brandService.find(brandId));
//    product.setTags(new HashSet(this.tagService.findList(tagIds)));
//    if (!IIIllIlI(product, new Class[0]))
//      return ERROR_PAGE;
//    localObject1 = (Product)this.productService.find(product.getId());
//    if (localObject1 == null)
//      return ERROR_PAGE;
//    if ((StringUtils.isNotEmpty(product.getSn())) && (!this.productService.snUnique(((Product)localObject1).getSn(), product.getSn())))
//      return ERROR_PAGE;
//    if (product.getMarketPrice() == null)
//    {
//      localObject2 = IIIllIlI(product.getPrice());
//      product.setMarketPrice((BigDecimal)localObject2);
//    }
//    if (product.getPoint() == null)
//    {
//      long l = IIIllIll(product.getPrice());
//      product.setPoint(Long.valueOf(l));
//    }
//    Object localObject4 = this.memberRankService.findAll().iterator();
//    Object localObject5;
//    while (((Iterator)localObject4).hasNext())
//    {
//      localObject3 = (MemberRank)((Iterator)localObject4).next();
//      localObject5 = request.getParameter("memberPrice_" + ((MemberRank)localObject3).getId());
//      if ((StringUtils.isNotEmpty((String)localObject5)) && (new BigDecimal((String)localObject5).compareTo(new BigDecimal(0)) >= 0))
//        product.getMemberPrice().put(localObject3, new BigDecimal((String)localObject5));
//      else
//        product.getMemberPrice().remove(localObject3);
//    }
//    localObject4 = product.getProductImages().iterator();
//    while (((Iterator)localObject4).hasNext())
//    {
//      localObject3 = (ProductImage)((Iterator)localObject4).next();
//      this.productImageService.build((ProductImage)localObject3);
//    }
//    Collections.sort(product.getProductImages());
//    if ((product.getImage() == null) && (product.getThumbnail() != null))
//      product.setImage(product.getThumbnail());
//    localObject4 = product.getProductCategory().getParameterGroups().iterator();
//    Object localObject6;
//    Object localObject7;
//    while (((Iterator)localObject4).hasNext())
//    {
//      localObject3 = (ParameterGroup)((Iterator)localObject4).next();
//      localObject6 = ((ParameterGroup)localObject3).getParameters().iterator();
//      while (((Iterator)localObject6).hasNext())
//      {
//        localObject5 = (Parameter)((Iterator)localObject6).next();
//        localObject7 = request.getParameter("parameter_" + ((Parameter)localObject5).getId());
//        if (StringUtils.isNotEmpty((String)localObject7))
//          product.getParameterValue().put(localObject5, localObject7);
//        else
//          product.getParameterValue().remove(localObject5);
//      }
//    }
//    localObject4 = product.getProductCategory().getAttributes().iterator();
//    while (((Iterator)localObject4).hasNext())
//    {
//      localObject3 = (Attribute)((Iterator)localObject4).next();
//      localObject5 = request.getParameter("attribute_" + ((Attribute)localObject3).getId());
//      if (StringUtils.isNotEmpty((String)localObject5))
//        product.setAttributeValue((Attribute)localObject3, (String)localObject5);
//      else
//        product.setAttributeValue((Attribute)localObject3, null);
//    }
//    Object localObject3 = ((Product)localObject1).getGoods();
//    localObject4 = new ArrayList();
//    if ((specificationIds != null) && (specificationIds.length > 0))
//    {
//      for (int i = 0; i < specificationIds.length; i++)
//      {
//        localObject6 = (Specification)this.specificationService.find(specificationIds[i]);
//        localObject7 = request.getParameterValues("specification_" + ((Specification)localObject6).getId());
//        if ((localObject7 == null) || (localObject7.length <= 0))
//          continue;
//        for (int j = 0; j < localObject7.length; j++)
//        {
//          if (i == 0)
//            if (j == 0)
//            {
//              BeanUtils.copyProperties(product, localObject1, new String[] { "id", "createDate", "modifyDate", "fullName", "allocatedStock", "score", "totalScore", "scoreCount", "hits", "weekHits", "monthHits", "sales", "weekSales", "monthSales", "weekHitsDate", "monthHitsDate", "weekSalesDate", "monthSalesDate", "goods", "reviews", "consultations", "favoriteMembers", "specifications", "specificationValues", "promotions", "cartItems", "orderItems", "giftItems", "productNotifies" });
//              ((Product)localObject1).setSpecifications(new HashSet());
//              ((Product)localObject1).setSpecificationValues(new HashSet());
//              ((List)localObject4).add(localObject1);
//            }
//            else if ((specificationProductIds != null) && (j < specificationProductIds.length))
//            {
//              localProduct = (Product)this.productService.find(specificationProductIds[j]);
//              if (localProduct.getGoods() != localObject3)
//                return ERROR_PAGE;
//              localProduct.setSpecifications(new HashSet());
//              localProduct.setSpecificationValues(new HashSet());
//              ((List)localObject4).add(localProduct);
//            }
//            else
//            {
//              localProduct = new Product();
//              BeanUtils.copyProperties(product, localProduct);
//              localProduct.setId(null);
//              localProduct.setCreateDate(null);
//              localProduct.setModifyDate(null);
//              localProduct.setSn(null);
//              localProduct.setFullName(null);
//              localProduct.setAllocatedStock(Integer.valueOf(0));
//              localProduct.setIsList(Boolean.valueOf(false));
//              localProduct.setScore(Float.valueOf(0.0F));
//              localProduct.setTotalScore(Long.valueOf(0L));
//              localProduct.setScoreCount(Long.valueOf(0L));
//              localProduct.setHits(Long.valueOf(0L));
//              localProduct.setWeekHits(Long.valueOf(0L));
//              localProduct.setMonthHits(Long.valueOf(0L));
//              localProduct.setSales(Long.valueOf(0L));
//              localProduct.setWeekSales(Long.valueOf(0L));
//              localProduct.setMonthSales(Long.valueOf(0L));
//              localProduct.setWeekHitsDate(new Date());
//              localProduct.setMonthHitsDate(new Date());
//              localProduct.setWeekSalesDate(new Date());
//              localProduct.setMonthSalesDate(new Date());
//              localProduct.setGoods((Goods)localObject3);
//              localProduct.setReviews(null);
//              localProduct.setConsultations(null);
//              localProduct.setFavoriteMembers(null);
//              localProduct.setSpecifications(new HashSet());
//              localProduct.setSpecificationValues(new HashSet());
//              localProduct.setPromotions(null);
//              localProduct.setCartItems(null);
//              localProduct.setOrderItems(null);
//              localProduct.setGiftItems(null);
//              localProduct.setProductNotifies(null);
//              ((List)localObject4).add(localProduct);
//            }
//          Product localProduct = (Product)((List)localObject4).get(j);
//          SpecificationValue localSpecificationValue = (SpecificationValue)this.specificationValueService.find(Long.valueOf(localObject7[j]));
//          localProduct.getSpecifications().add(localObject6);
//          localProduct.getSpecificationValues().add(localSpecificationValue);
//        }
//      }
//    }
//    else
//    {
//      product.setSpecifications(null);
//      product.setSpecificationValues(null);
//      BeanUtils.copyProperties(product, localObject1, new String[] { "id", "createDate", "modifyDate", "fullName", "allocatedStock", "score", "totalScore", "scoreCount", "hits", "weekHits", "monthHits", "sales", "weekSales", "monthSales", "weekHitsDate", "monthHitsDate", "weekSalesDate", "monthSalesDate", "goods", "reviews", "consultations", "favoriteMembers", "promotions", "cartItems", "orderItems", "giftItems", "productNotifies" });
//      ((List)localObject4).add(localObject1);
//    }
//    ((Goods)localObject3).getProducts().clear();
//    ((Goods)localObject3).getProducts().addAll((Collection)localObject4);
//    this.goodsService.update(localObject3);
//    IIIllIlI(redirectAttributes, IIIlllII);
//    return (String)(String)(String)(String)(String)(String)(String)"redirect:list.jhtml";
//  }
//
//  @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
//  public String list(Long productCategoryId, Long brandId, Long promotionId, Long tagId, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Pageable pageable, ModelMap model)
//  {
//    ProductCategory localProductCategory = (ProductCategory)this.productCategoryService.find(productCategoryId);
//    Brand localBrand = (Brand)this.brandService.find(brandId);
//    Promotion localPromotion = (Promotion)this.promotionService.find(promotionId);
//    List localList = this.tagService.findList(new Long[] { tagId });
//    model.addAttribute("productCategoryTree", this.productCategoryService.findTree());
//    model.addAttribute("brands", this.brandService.findAll());
//    model.addAttribute("promotions", this.promotionService.findAll());
//    model.addAttribute("tags", this.tagService.findList(Tag.Type.product));
//    model.addAttribute("productCategoryId", productCategoryId);
//    model.addAttribute("brandId", brandId);
//    model.addAttribute("promotionId", promotionId);
//    model.addAttribute("tagId", tagId);
//    model.addAttribute("isMarketable", isMarketable);
//    model.addAttribute("isList", isList);
//    model.addAttribute("isTop", isTop);
//    model.addAttribute("isGift", isGift);
//    model.addAttribute("isOutOfStock", isOutOfStock);
//    model.addAttribute("isStockAlert", isStockAlert);
//    model.addAttribute("page", this.productService.findPage(localProductCategory, localBrand, localPromotion, localList, null, null, null, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, Product.OrderType.dateDesc, pageable));
//    return "/admin/product/list";
//  }
//
//  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
//  @ResponseBody
//  public Message delete(Long[] ids)
//  {
//    this.productService.delete(ids);
//    return IIIlllII;
//  }
//
//  private BigDecimal IIIllIlI(BigDecimal paramBigDecimal)
//  {
//    Setting localSetting = SettingUtils.get();
//    Double localDouble = localSetting.getDefaultMarketPriceScale();
//    return localSetting.setScale(paramBigDecimal.multiply(new BigDecimal(localDouble.toString())));
//  }
//
//  private long IIIllIll(BigDecimal paramBigDecimal)
//  {
//    Setting localSetting = SettingUtils.get();
//    Double localDouble = localSetting.getDefaultPointScale();
//    return paramBigDecimal.multiply(new BigDecimal(localDouble.toString())).longValue();
//  }
}