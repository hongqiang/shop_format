package com.hongqiang.shop.common.template.method;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModel;

import java.util.List;
//import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Component("abbreviateMethod")
public class AbbreviateMethod
  implements TemplateMethodModel
{
  private static final Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5\\ufe30-\\uffa0]+$");

  public Object exec(@SuppressWarnings("rawtypes") List arguments)
  {
    if ((arguments != null) && (!arguments.isEmpty()) && (arguments.get(0) != null) && (StringUtils.isNotEmpty(arguments.get(0).toString())))
    {
      Integer localInteger = null;
      String str = null;
      if (arguments.size() == 2)
      {
        if (arguments.get(1) != null)
          localInteger = Integer.valueOf(arguments.get(1).toString());
      }
      else if (arguments.size() > 2)
      {
        if (arguments.get(1) != null)
          localInteger = Integer.valueOf(arguments.get(1).toString());
        if (arguments.get(2) != null)
          str = arguments.get(2).toString();
      }
      return new SimpleScalar(compose(arguments.get(0).toString(), localInteger, str));
    }
    return null;
  }

  private String compose(String paramString1, Integer paramInteger, String paramString2)
  {
    if (paramInteger != null)
    {
      int i = 0;
      int j = 0;
      while (i < paramString1.length())
      {
        j = pattern.matcher(String.valueOf(paramString1.charAt(i))).find() ? j + 2 : j + 1;
        if (j >= paramInteger.intValue())
          break;
        i++;
      }
      if (i < paramString1.length())
      {
        if (paramString2 != null)
          return paramString1.substring(0, i + 1) + paramString2;
        return paramString1.substring(0, i + 1);
      }
      return paramString1;
    }
    if (paramString2 != null)
      return paramString1 + paramString2;
    return paramString1;
  }
}