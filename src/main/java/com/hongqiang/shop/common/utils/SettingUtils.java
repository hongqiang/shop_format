package com.hongqiang.shop.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.io.IOUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.core.io.ClassPathResource;

import com.hongqiang.shop.common.utils.model.CommonAttributes;

public final class SettingUtils
{
  private static final CacheManager cacheManager = CacheManager.create();
  private static final BeanUtilsBean beanUtilsBean;

  static
  {
	  ShopConvertUtils local1 = new ShopConvertUtils();
    DateConverter localDateConverter = new DateConverter();
    localDateConverter.setPatterns(CommonAttributes.DATE_PATTERNS);
    local1.register(localDateConverter, Date.class);
    beanUtilsBean = new BeanUtilsBean(local1);
  }

  public static Setting get()
  {
    Ehcache localEhcache = cacheManager.getEhcache("setting");
    net.sf.ehcache.Element localElement = localEhcache.get(Setting.CACHE_KEY);
    Setting localSetting;
    if (localElement != null)
    {
      localSetting = (Setting)localElement.getObjectValue();
    }
    else
    {
      localSetting = new Setting();
      try
      {
        File localFile = new ClassPathResource("/shophq.xml").getFile();
        Document localDocument = new SAXReader().read(localFile);
        @SuppressWarnings("unchecked")
		List<org.dom4j.Element> localList = localDocument.selectNodes("/shophq/setting");
        Iterator<org.dom4j.Element> localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          org.dom4j.Element localElement1 = (org.dom4j.Element)localIterator.next();
          String str1 = localElement1.attributeValue("name");
          String str2 = localElement1.attributeValue("value");
          try
          {
        	  System.out.println("key= "+str1+" , value= "+str2);
            beanUtilsBean.setProperty(localSetting, str1, str2);
          }
          catch (IllegalAccessException localIllegalAccessException)
          {
            localIllegalAccessException.printStackTrace();
          }
          catch (InvocationTargetException localInvocationTargetException)
          {
            localInvocationTargetException.printStackTrace();
          }
        }
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
      localEhcache.put(new net.sf.ehcache.Element(Setting.CACHE_KEY, localSetting));
    }
    return localSetting;
  }

  public static void set(Setting setting)
  {
    try
    {
      File localFile = new ClassPathResource("/shopxx.xml").getFile();
      Document localDocument = new SAXReader().read(localFile);
      List<org.dom4j.Element> localList = localDocument.selectNodes("/shopxx/setting");
      Object localObject2 = localList.iterator();
      while (((Iterator<org.dom4j.Element>)localObject2).hasNext())
      {
    	org.dom4j.Element localObject1 = (org.dom4j.Element)((Iterator)localObject2).next();
        try
        {
          String str1 = ((org.dom4j.Element)localObject1).attributeValue("name");
          String str2 = beanUtilsBean.getProperty(setting, str1);
          Attribute localAttribute = ((org.dom4j.Element)localObject1).attribute("value");
          localAttribute.setValue(str2);
        }
        catch (IllegalAccessException localIllegalAccessException1)
        {
          localIllegalAccessException1.printStackTrace();
        }
        catch (InvocationTargetException localInvocationTargetException1)
        {
          localInvocationTargetException1.printStackTrace();
        }
        catch (NoSuchMethodException localNoSuchMethodException1)
        {
          localNoSuchMethodException1.printStackTrace();
        }
      }
      Object localObject1 = null;
      localObject2 = null;
      try
      {
        OutputFormat localOutputFormat = OutputFormat.createPrettyPrint();
        localOutputFormat.setEncoding("UTF-8");
        localOutputFormat.setIndent(true);
        localOutputFormat.setIndent("\t");
        localOutputFormat.setNewlines(true);
        localObject1 = new FileOutputStream(localFile);
        localObject2 = new XMLWriter((OutputStream)localObject1, localOutputFormat);
        ((XMLWriter)localObject2).write(localDocument);
      }
      catch (Exception localException3)
      {
        localException3.printStackTrace();
      }
      finally
      {
        if (localObject2 != null)
          try
          {
            ((XMLWriter)localObject2).close();
          }
          catch (IOException localIOException4)
          {
          }
        IOUtils.closeQuietly((OutputStream)localObject1);
      }
      Ehcache localEhcache = cacheManager.getEhcache("setting");
      localEhcache.put(new net.sf.ehcache.Element(Setting.CACHE_KEY, setting));
    }
    catch (Exception localException2)
    {
      localException2.printStackTrace();
    }
  }
}
