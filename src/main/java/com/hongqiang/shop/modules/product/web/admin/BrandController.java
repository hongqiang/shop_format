package com.hongqiang.shop.modules.product.web.admin;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Message;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Brand;
import com.hongqiang.shop.modules.entity.Brand.Type;
import com.hongqiang.shop.modules.product.service.BrandService;
import com.hongqiang.shop.common.web.BaseController;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xwpf.usermodel.BreakClear;
import org.hibernate.mapping.Array;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller("adminBrandController")
@RequestMapping({"${adminPath}/brand"})
public class BrandController extends BaseController
{

  @Resource(name="brandServiceImpl")
  private BrandService brandService;

  @RequestMapping(value={"/add"}, method=RequestMethod.GET)
  public String add(ModelMap model)
  {
    model.addAttribute("types", Brand.Type.values());
    return "/admin/brand/add";
  }

  @RequestMapping(value={"/save"}, method=RequestMethod.POST)
  public String save(Brand brand, RedirectAttributes redirectAttributes)
  {
//    if (!IIIllIlI(brand, new Class[0]))
//      return "/admin/common/error";
    if (brand.getType() == Brand.Type.text)
      brand.setLogo(null);
    else if (StringUtils.isEmpty(brand.getLogo()))
      return "/admin/common/error";
    brand.setProducts(null);
    brand.setProductCategories(null);
    brand.setPromotions(null);
    this.brandService.save(brand);
//    IIIllIlI(redirectAttributes, IIIlllII);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method=RequestMethod.GET)
  public String edit(Long id, ModelMap model)
  {
    model.addAttribute("types", Brand.Type.values());
    model.addAttribute("brand", this.brandService.find(id));
    return "/admin/brand/edit";
  }

  @RequestMapping(value={"/update"}, method=RequestMethod.POST)
  public String update(Brand brand, RedirectAttributes redirectAttributes)
  {
//    if (!IIIllIlI(brand, new Class[0]))
//      return "/admin/common/error";
    if (brand.getType() == Brand.Type.text)
      brand.setLogo(null);
    else if (StringUtils.isEmpty(brand.getLogo()))
      return "/admin/common/error";
//    this.brandService.update(brand, new String[] { "products", "productCategories", "promotions" });
//    IIIllIlI(redirectAttributes, IIIlllII);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/list"}, method=RequestMethod.GET)
  public String list(Pageable pageable, ModelMap model)
  {
    model.addAttribute("page", this.brandService.findPage(pageable));
    return "/admin/brand/list";
  }

//  @RequestMapping(value={"/delete"}, method=RequestMethod.POST)
//  @ResponseBody
//  public Message delete(Long[] ids)
//  {
//    this.brandService.delete(ids);
////    return Message("success");
//    return "success";
//  }

  	@RequestMapping(value={"/do"},method=RequestMethod.GET)
  	public void doit(){
//  		Long idLong=1L;
//  		Brand brand=this.brandService.find(idLong);
//  		System.out.println(brand.getName()+","+brand.getIntroduction());
//  		
//  	  	Brand aBrand =new Brand();
//  	  	aBrand.setName("神一样的存在");
//  	  	aBrand.setType(Brand.Type.text);
//  		this.brandService.save(aBrand);
//  		
//  		aBrand.setName("sit"); 	    
//  	    this.brandService.update(aBrand);
//  	  
//  	    this.brandService.delete(aBrand);
//  	  System.out.println("delete success.");
//  	  
//  	    String temp="hello";
//  	  for (int i = 0; i < 10; i++) {
//		Brand aBrand2 = new Brand();
//		temp+=i;
//		aBrand2.setName(temp);
//		aBrand2.setType(Brand.Type.text);
//		this.brandService.save(aBrand2);
//		System.out.println("save success.");
//		this.brandService.delete(aBrand2.getId());
//	}
  		Pageable pageable = new Pageable(1,40);
  		Page<Brand> page=this.brandService.findPage(pageable);
  		for (Brand o : page.getList()) {
			System.out.print(o.getName()+", "+o.getIntroduction()+"\n");
		}

  	}
}