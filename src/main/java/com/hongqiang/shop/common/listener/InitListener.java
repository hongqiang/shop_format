package com.hongqiang.shop.common.listener;

import java.io.File;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import com.hongqiang.shop.modules.util.service.CacheService;
import com.hongqiang.shop.modules.util.service.SearchService;
import com.hongqiang.shop.modules.util.service.StaticService;

@Component("initListener")
public class InitListener
  implements ApplicationListener<ContextRefreshedEvent>, ServletContextAware
{
  private static final String INSTALL = "/install_init.conf";
  private ServletContext servletContext;

  @Resource(name="staticServiceImpl")
  private StaticService staticService;

  @Resource(name="cacheServiceImpl")
  private CacheService cacheService;

  @Resource(name="searchServiceImpl")
  private SearchService searchService;

  public void setServletContext(ServletContext servletContext)
  {
    this.servletContext = servletContext;
  }

  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent)
  {
    if ((this.servletContext != null) && (contextRefreshedEvent.getApplicationContext().getParent() == null))
    {
      File localFile = new File(this.servletContext.getRealPath(INSTALL));
      if (localFile.exists())
      {
        this.cacheService.clear();
        this.staticService.buildAll();
        this.searchService.purge();
        this.searchService.index();
        localFile.delete();
      }
      else
      {
        this.staticService.buildIndex();
        this.staticService.buildOther();
      }
    }
  }
}