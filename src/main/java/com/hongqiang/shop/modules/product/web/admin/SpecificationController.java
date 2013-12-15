package com.hongqiang.shop.modules.product.web.admin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Message;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.common.web.BaseController;
import com.hongqiang.shop.modules.entity.Specification;
import com.hongqiang.shop.modules.entity.SpecificationValue;
import com.hongqiang.shop.modules.product.service.SpecificationService;

@Controller("adminSpecificationController")
@RequestMapping({"${adminPath}/specification"})
public class SpecificationController extends BaseController
{

  @Autowired
  private SpecificationService specificationService;

  @RequestMapping(value={"/add"}, method=RequestMethod.GET)
  public String add(ModelMap model)
  {
    model.addAttribute("types", Specification.Type.values());
    return "/admin/specification/add";
  }

  @RequestMapping(value={"/save"}, method=RequestMethod.POST)
  public String save(Specification specification, RedirectAttributes redirectAttributes)
  {
    Iterator<SpecificationValue> localIterator = specification.getSpecificationValues().iterator();
    while (localIterator.hasNext())
    {
      SpecificationValue localSpecificationValue = (SpecificationValue)localIterator.next();
      if ((localSpecificationValue == null) || (localSpecificationValue.getName() == null))
      {
        localIterator.remove();
      }
      else
      {
        if (specification.getType() == Specification.Type.text)
          localSpecificationValue.setImage(null);
        localSpecificationValue.setSpecification(specification);
      }
    }
    if (!beanValidator(redirectAttributes,specification, new Class[0]))
      return ERROR_PAGE;
    specification.setProducts(null);
    this.specificationService.save(specification);
    addMessage(redirectAttributes, ADMIN_SUCCESS);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method=RequestMethod.GET)
  public String edit(Long id, ModelMap model)
  {
    model.addAttribute("types", Specification.Type.values());
    model.addAttribute("specification", this.specificationService.find(id));
    return "/admin/specification/edit";
  }

  @RequestMapping(value={"/update"}, method=RequestMethod.POST)
  public String update(Specification specification, RedirectAttributes redirectAttributes)
  {
    Iterator<SpecificationValue> localIterator = specification.getSpecificationValues().iterator();
    while (localIterator.hasNext())
    {
      SpecificationValue localSpecificationValue = (SpecificationValue)localIterator.next();
      if ((localSpecificationValue == null) || (localSpecificationValue.getName() == null))
      {
        localIterator.remove();
      }
      else
      {
        if (specification.getType() == Specification.Type.text)
          localSpecificationValue.setImage(null);
        localSpecificationValue.setSpecification(specification);
      }
    }
    if (!beanValidator(redirectAttributes,specification, new Class[0]))
      return ERROR_PAGE;
    this.specificationService.update(specification, new String[] { "products" });
    addMessage(redirectAttributes, ADMIN_SUCCESS);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/list"}, method=RequestMethod.GET)
  public String list(Pageable pageable, ModelMap model)
  {
    model.addAttribute("page", this.specificationService.findPage(pageable));
    return "/admin/specification/list";
  }

  @RequestMapping(value={"/delete"}, method=RequestMethod.POST)
  @ResponseBody
  public Message delete(Long[] ids)
  {
    if (ids != null)
    {
      for (Long localLong : ids)
      {
        Specification localSpecification = (Specification)this.specificationService.find(localLong);
        if ((localSpecification != null) && (localSpecification.getProducts() != null) && (!localSpecification.getProducts().isEmpty()))
          return Message.error("admin.specification.deleteExistProductNotAllowed", new Object[] { localSpecification.getName() });
      }
      this.specificationService.delete(ids);
    }
    return ADMIN_SUCCESS;
  }
  
  @RequestMapping(value={"/dospe"},method=RequestMethod.GET)
	public void doit(){
		Specification specification= this.specificationService.find(1L);
		System.out.println(specification.getName()+","+specification.getMemo());
		System.out.println("==============================================");
		
		Pageable pageable = new Pageable(1,40);
		Page<Specification> page=this.specificationService.findPage(pageable);
		for (Specification o : page.getList()) {
		System.out.print(o.getName()+", "+o.getMemo()+"\n");
	}
		System.out.println("==============================================");
		
		Specification specification2 = new Specification();
		specification2.setName("gogogo");
		specification2.setType(Specification.Type.text);
		
		List<SpecificationValue> list = new ArrayList<SpecificationValue>();
		for(int i=0;i<3;i++){
			SpecificationValue specificationValue = new SpecificationValue();
			specificationValue.setName("a"+i);
			specificationValue.setSpecification(specification2);
			list.add(specificationValue);
		}
		specification2.setSpecificationValues(list);
		this.specificationService.save(specification2);
		
		specification.setName("new go");
		this.specificationService.update(specification);
		
		//不可删除
		this.specificationService.delete(6L);
		this.specificationService.delete(specification);
	}
}