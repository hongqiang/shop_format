package com.hongqiang.shop.modules.user.web.admin;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hongqiang.shop.common.web.BaseController;
import com.hongqiang.shop.modules.shipping.service.OrderService;

@Controller("adminSalesController")
@RequestMapping({"${adminPath}/sales"})
public class SalesController extends BaseController
{
public enum Type
{
  year, month;
}

  private static final int LINK_LENGTH = 12;

  @Autowired
  private OrderService orderService;

  @RequestMapping(value={"/view"}, method=RequestMethod.GET)
  public String view(Type type, Date beginDate, Date endDate, Model model)
  {
    if (type == null)
      type = Type.month;
    if (beginDate == null)
      beginDate = DateUtils.addMonths(new Date(), -11);
    if (endDate == null)
      endDate = new Date();
    LinkedHashMap<Date, BigDecimal> localLinkedHashMap1 = new LinkedHashMap<Date, BigDecimal>();
    LinkedHashMap<Date, Integer> localLinkedHashMap2 = new LinkedHashMap<Date, Integer>();
    Calendar localCalendar1 = Calendar.getInstance();
    localCalendar1.setTime(beginDate);
    Calendar localCalendar2 = Calendar.getInstance();
    localCalendar1.setTime(endDate);
    int i = localCalendar1.get(1);
    int j = localCalendar2.get(1);
    int k = localCalendar1.get(2);
    int m = localCalendar2.get(2);
    for (int n = i; n <= j; n++)
    {
      if (localLinkedHashMap1.size() >= LINK_LENGTH)
        break;
      Calendar localCalendar3 = Calendar.getInstance();
      localCalendar3.set(1, n);
      Date localDate2;
      if (type == Type.year)
      {
        localCalendar3.set(2, localCalendar3.getActualMinimum(2));
        localCalendar3.set(5, localCalendar3.getActualMinimum(5));
        localCalendar3.set(11, localCalendar3.getActualMinimum(11));
        localCalendar3.set(12, localCalendar3.getActualMinimum(12));
        localCalendar3.set(13, localCalendar3.getActualMinimum(13));
        Date localDate1 = localCalendar3.getTime();
        localCalendar3.set(2, localCalendar3.getActualMaximum(2));
        localCalendar3.set(5, localCalendar3.getActualMaximum(5));
        localCalendar3.set(11, localCalendar3.getActualMaximum(11));
        localCalendar3.set(12, localCalendar3.getActualMaximum(12));
        localCalendar3.set(13, localCalendar3.getActualMaximum(13));
        localDate2 = localCalendar3.getTime();
        BigDecimal salesAmount = this.orderService.getSalesAmount(localDate1, localDate2);
        Integer salesVolume = this.orderService.getSalesVolume(localDate1, localDate2);
        localLinkedHashMap1.put(localDate1, salesAmount != null ? salesAmount : BigDecimal.ZERO);
        localLinkedHashMap2.put(localDate1, Integer.valueOf(salesVolume != null ? salesVolume.intValue() : 0));
      }
      else
      {
        for (int i1 = n == i ? k : localCalendar3.getActualMinimum(2); i1 <= (n == j ? m : localCalendar3.getActualMaximum(2)); i1++)
        {
          if (localLinkedHashMap1.size() >= LINK_LENGTH)
            break;
          localCalendar3.set(2, i1);
          localCalendar3.set(5, localCalendar3.getActualMinimum(5));
          localCalendar3.set(11, localCalendar3.getActualMinimum(11));
          localCalendar3.set(12, localCalendar3.getActualMinimum(12));
          localCalendar3.set(13, localCalendar3.getActualMinimum(13));
          localDate2 = localCalendar3.getTime();
          localCalendar3.set(5, localCalendar3.getActualMaximum(5));
          localCalendar3.set(11, localCalendar3.getActualMaximum(11));
          localCalendar3.set(12, localCalendar3.getActualMaximum(12));
          localCalendar3.set(13, localCalendar3.getActualMaximum(13));
          Date localDate = localCalendar3.getTime();
          BigDecimal salesAmount = this.orderService.getSalesAmount(localDate2, localDate);
          Integer salesVolume = this.orderService.getSalesVolume(localDate2, localDate);
          localLinkedHashMap1.put(localDate2, salesAmount != null ? salesAmount : BigDecimal.ZERO);
          localLinkedHashMap2.put(localDate2, Integer.valueOf(salesVolume != null ? salesVolume.intValue() : 0));
        }
      }
    }
    model.addAttribute("types", Type.values());
    model.addAttribute("type", type);
    model.addAttribute("beginDate", beginDate);
    model.addAttribute("endDate", endDate);
    model.addAttribute("salesAmountMap", localLinkedHashMap1);
    model.addAttribute("salesVolumeMap", localLinkedHashMap2);
    return (String)(String)"/admin/sales/view";
  }
}