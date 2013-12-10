package com.hongqiang.shop.modules.product.web.shop;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongqiang.shop.common.utils.Message;
import com.hongqiang.shop.common.web.BaseController;
import com.hongqiang.shop.modules.entity.Member;
import com.hongqiang.shop.modules.entity.Product;
import com.hongqiang.shop.modules.entity.ProductNotify;
import com.hongqiang.shop.modules.product.service.ProductNotifyService;
import com.hongqiang.shop.modules.product.service.ProductService;

@Controller("shopProductNotifyController")
@RequestMapping({"/product_notify"})
public class ProductNotifyController extends BaseController
{

  @Autowired
  private ProductNotifyService productNotifyService;

//  @Autowired
//  private MemberService memberService;

  @Autowired
  private ProductService productService;

  @RequestMapping(value={"/email"}, method=RequestMethod.GET)
  @ResponseBody
  public Map<String, String> email()
  {
//    Member localMember = this.memberService.getCurrent();
//    Object localObject = localMember != null ? localMember.getEmail() : null;
    HashMap localHashMap = new HashMap();
//    localHashMap.put("email", localObject);
    return localHashMap;
  }

  @RequestMapping(value={"/save"}, method=RequestMethod.POST)
  @ResponseBody
  public Map<String, Object> save(String email, Long productId)
  {
    HashMap localHashMap = new HashMap();
//    if (!IIIllIlI(ProductNotify.class, "email", email, new Class[0]))
//    {
//      localHashMap.put("message", IIIllIll);
//      return localHashMap;
//    }
    Product localProduct = (Product)this.productService.find(productId);
    if (localProduct == null)
    {
      localHashMap.put("message", Message.warn("shop.productNotify.productNotExist", new Object[0]));
      return localHashMap;
    }
    if (!localProduct.getIsMarketable().booleanValue())
    {
      localHashMap.put("message", Message.warn("shop.productNotify.productNotMarketable", new Object[0]));
      return localHashMap;
    }
    if (!localProduct.getIsOutOfStock().booleanValue())
      localHashMap.put("message", Message.warn("shop.productNotify.productInStock", new Object[0]));
    if (this.productNotifyService.exists(localProduct, email))
    {
      localHashMap.put("message", Message.warn("shop.productNotify.exist", new Object[0]));
    }
    else
    {
      ProductNotify localProductNotify = new ProductNotify();
      localProductNotify.setEmail(email);
      localProductNotify.setHasSent(Boolean.valueOf(false));
//      localProductNotify.setMember(this.memberService.getCurrent());
      localProductNotify.setProduct(localProduct);
      this.productNotifyService.save(localProductNotify);
//      localHashMap.put("message", IIIlllII);
    }
    return localHashMap;
  }
}
