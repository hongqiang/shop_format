package com.hongqiang.shop.modules.product.web.admin;

import java.util.Iterator;

import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hongqiang.shop.common.utils.Message;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.common.web.BaseController;
import com.hongqiang.shop.modules.entity.Attribute;
import com.hongqiang.shop.modules.entity.BaseEntity;
import com.hongqiang.shop.modules.entity.ProductCategory;
import com.hongqiang.shop.modules.product.service.AttributeService;
import com.hongqiang.shop.modules.product.service.ProductCategoryService;

@Controller("adminAttributeController")
@RequestMapping({"/admin/attribute"})
public class AttributeController extends BaseController
{

  @Resource(name="attributeServiceImpl")
  private AttributeService attributeService;

  @Resource(name="productCategoryServiceImpl")
  private ProductCategoryService productCategoryService;

  @RequestMapping(value={"/add"}, method=RequestMethod.GET)
  public String add(ModelMap model)
  {
    model.addAttribute("productCategoryTree", this.productCategoryService.findTree());
    model.addAttribute("attributeValuePropertyCount", Integer.valueOf(20));
    return "/admin/attribute/add";
  }

  @RequestMapping(value={"/save"}, method=RequestMethod.POST)
  public String save(Attribute attribute, Long productCategoryId, RedirectAttributes redirectAttributes)
  {
    Iterator<String> localIterator = attribute.getOptions().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (!StringUtils.isEmpty(str))
        continue;
      localIterator.remove();
    }
    attribute.setProductCategory((ProductCategory)this.productCategoryService.find(productCategoryId));
    if (!beanValidator(redirectAttributes,attribute, new Class[] { BaseEntity.Save.class }))
      return ERROR_PAGE;
    if (attribute.getProductCategory().getAttributes().size() >= 20)
    {
    	addMessage(redirectAttributes, Message.error("admin.attribute.addCountNotAllowed", new Object[] { Integer.valueOf(20) }));
    }
    else
    {
      attribute.setPropertyIndex(null);
      this.attributeService.save(attribute);
      addMessage(redirectAttributes, ADMIN_SUCCESS);
    }
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method=RequestMethod.GET)
  public String edit(Long id, ModelMap model)
  {
    model.addAttribute("productCategoryTree", this.productCategoryService.findTree());
    model.addAttribute("attributeValuePropertyCount", Integer.valueOf(20));
    model.addAttribute("attribute", this.attributeService.find(id));
    return "/admin/attribute/edit";
  }

  @RequestMapping(value={"/update"}, method=RequestMethod.POST)
  public String update(Attribute attribute, RedirectAttributes redirectAttributes)
  {
    Iterator<String> localIterator = attribute.getOptions().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (!StringUtils.isEmpty(str))
        continue;
      localIterator.remove();
    }
    if (!beanValidator(redirectAttributes,attribute, new Class[0]))
      return ERROR_PAGE;
//    this.attributeService.update(attribute, new String[] { "propertyIndex", "productCategory" });
    addMessage(redirectAttributes, ADMIN_SUCCESS);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/list"}, method=RequestMethod.GET)
  public String list(Pageable pageable, ModelMap model)
  {
    model.addAttribute("page", this.attributeService.findPage(pageable));
    return "/admin/attribute/list";
  }

  @RequestMapping(value={"/delete"}, method=RequestMethod.POST)
  @ResponseBody
  public Message delete(Long[] ids)
  {
    this.attributeService.delete(ids);
    return ADMIN_SUCCESS;
  }
  
	@RequestMapping(value={"/attribute"},method=RequestMethod.GET)
  	public void doit(){
		Attribute a = this.attributeService.find(1L);
		Attribute newa = this.attributeService.find(70L);
		System.out.println("a.name="+a.getName()+", a.property_index="+a.getPropertyIndex()+", a.product_category"+a.getProductCategory());
		System.out.println("===============================================");
		
//		Pageable pageable = new Pageable();
//		pageable.setPageNumber(1);
//		pageable.setPageSize(40);
//		Page<Attribute> page = this.attributeService.findPage( pageable);
//		for (Attribute ab : page.getList()) {
//			System.out.println("ab.name="+ab.getName()+",ab.property_index="+ab.getPropertyIndex()+", ab.product_category"+ab.getProductCategory());
//	}
//		
//		System.out.println("===============================================");
//		
//		Attribute newAttribute = new Attribute();
//		newAttribute.setName("Hello");
//		List<String> options = new ArrayList<String>();
//		options.add("a");
//		options.add("b");
//		newAttribute.setOptions(options);
//		ProductCategory productCategory = new ProductCategory();
//		productCategory.setId(1L);
//		newAttribute.setProductCategory(productCategory);
//		newAttribute.setPropertyIndex(1);
//		this.attributeService.save(newAttribute);
//		System.out.println("newAttribute.name="+newAttribute.getName()+", newAttribute.property_index="+newAttribute.getPropertyIndex()+", newAttribute.product_category"+newAttribute.getProductCategory());
//		System.out.println("===============================================");
//		
//		a.setName("modify");
//		this.attributeService.update(a);
//		System.out.println("a.name="+a.getName()+", a.property_index="+a.getPropertyIndex()+", a.product_category"+a.getProductCategory());
//		System.out.println("===============================================");
		
		this.attributeService.delete(a);
		this.attributeService.delete(newa);
	}
}