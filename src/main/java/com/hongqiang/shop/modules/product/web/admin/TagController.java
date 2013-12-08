package com.hongqiang.shop.modules.product.web.admin;

import java.util.List;

import javax.annotation.Resource;

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
import com.hongqiang.shop.modules.entity.BaseEntity;
import com.hongqiang.shop.modules.entity.Tag;
import com.hongqiang.shop.modules.product.service.TagService;

@Controller("adminTagController")
@RequestMapping({"${adminPath}/tag"})
public class TagController extends BaseController
{

  @Resource(name="tagServiceImpl")
  private TagService tagService;

  @RequestMapping(value={"/add"}, method=RequestMethod.GET)
  public String add(ModelMap model)
  {
    model.addAttribute("types", Tag.Type.values());
    return "/admin/tag/add";
  }

  @RequestMapping(value={"/save"}, method=RequestMethod.POST)
  public String save(Tag tag, RedirectAttributes redirectAttributes)
  {
//    if (!IIIllIlI(tag, new Class[] { BaseEntity.Save.class }))
//      return "/admin/common/error";
//    tag.setArticles(null);
    tag.setProducts(null);
    this.tagService.save(tag);
//    IIIllIlI(redirectAttributes, IIIlllII);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method=RequestMethod.GET)
  public String edit(Long id, ModelMap model)
  {
    model.addAttribute("types", Tag.Type.values());
    model.addAttribute("tag", this.tagService.find(id));
    return "/admin/tag/edit";
  }

  @RequestMapping(value={"/update"}, method=RequestMethod.POST)
  public String update(Tag tag, RedirectAttributes redirectAttributes)
  {
//    if (!IIIllIlI(tag, new Class[0]))
//      return "/admin/common/error";
//    this.tagService.update(tag, new String[] { "type", "articles", "products" });
//    IIIllIlI(redirectAttributes, IIIlllII);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/list"}, method=RequestMethod.GET)
  public String list(Pageable pageable, ModelMap model)
  {
    model.addAttribute("page", this.tagService.findPage(pageable));
    return "/admin/tag/list";
  }

  @RequestMapping(value={"/delete"}, method=RequestMethod.POST)
  @ResponseBody
  public Message delete(Long[] ids)
  {
    this.tagService.delete(ids);
    return Message.success("admin.message.success",null);
  }
  
	@RequestMapping(value={"/dotag"},method=RequestMethod.GET)
	public void doit(){
//		Tag tag = this.tagService.find(1L);
//		System.out.println(tag.getName()+","+tag.getType());
//		System.out.println("==============================================");
//		
//		Pageable pageable = new Pageable(1,40);
//  		Page<Tag> page=this.tagService.findPage(pageable);
//  		for (Tag o : page.getList()) {
//			System.out.print(o.getName()+", "+o.getType()+"\n");
//		}
//  		System.out.println("==============================================");
//  		
//  		List<Tag> tags=this.tagService.findList(Tag.Type.product);
//  		for (Tag o : tags) {
//			System.out.print(o.getName()+", "+o.getType()+"\n");
//		}
//  		System.out.println("==============================================");
		
  		Tag aTag = new Tag();
  		aTag.setName("nb");
  		aTag.setType(Tag.Type.product);
  		this.tagService.save(aTag);
  		
  		this.tagService.delete(aTag);
  		this.tagService.delete(5L);
	}
}