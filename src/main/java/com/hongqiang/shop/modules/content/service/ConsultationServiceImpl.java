package com.hongqiang.shop.modules.content.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.service.BaseService;
import com.hongqiang.shop.common.utils.Filter;
import com.hongqiang.shop.common.utils.Order;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.content.dao.ConsultationDao;
import com.hongqiang.shop.modules.entity.Consultation;
import com.hongqiang.shop.modules.entity.Member;
import com.hongqiang.shop.modules.entity.Product;
import com.hongqiang.shop.modules.util.service.StaticService;

@Service
public class ConsultationServiceImpl extends BaseService
  implements ConsultationService
{

  @Autowired
  private ConsultationDao consultationDao;

  @Autowired
  private StaticService staticService;

  @Transactional(readOnly=true)
  public List<Consultation> findList(Member member, Product product, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders)
  {
    return this.consultationDao.findList(member, product, isShow, count, filters, orders);
  }

  @Transactional(readOnly=true)
  @Cacheable({"consultation"})
  public List<Consultation> findList(Member member, Product product, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion)
  {
    return this.consultationDao.findList(member, product, isShow, count, filters, orders);
  }
  
  @Transactional(readOnly=true)
  @Cacheable({"consultation"})
  public List<Consultation> findList(Integer first, Integer count, List<Filter> filters, List<Order> orders){
	return this.consultationDao.findList(first, count, filters, orders);
  }
  
  @Transactional(readOnly=true)
  @Cacheable({"consultation"})
  public Page<Consultation> findPage(Pageable pageable){
	return this.consultationDao.findPage( pageable);
  }
  
  @Transactional(readOnly=true)
  @Cacheable({"consultation"})
  public Consultation find(Long id){
	return this.consultationDao.find(id);
  }
  

  @Transactional(readOnly=true)
  public Page<Consultation> findPage(Member member, Product product, Boolean isShow, Pageable pageable)
  {
    return this.consultationDao.findPage(member, product, isShow, pageable);
  }

  @Transactional(readOnly=true)
  public Long count(Member member, Product product, Boolean isShow)
  {
    return this.consultationDao.count(member, product, isShow);
  }

  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public void reply(Consultation consultation, Consultation replyConsultation)
  {
    if ((consultation == null) || (replyConsultation == null))
      return;
    consultation.setIsShow(Boolean.valueOf(true));
    this.consultationDao.merge(consultation);
    replyConsultation.setIsShow(Boolean.valueOf(true));
    replyConsultation.setProduct(consultation.getProduct());
    replyConsultation.setForConsultation(consultation);
    this.consultationDao.persist(replyConsultation);
    Product localProduct = consultation.getProduct();
    if (localProduct != null)
    {
      this.consultationDao.flush();
      this.staticService.build(localProduct);
    }
  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public void save(Consultation consultation)
  {
    this.consultationDao.persist(consultation);
    Product localProduct = consultation.getProduct();
    if (localProduct != null)
    {
      this.consultationDao.flush();
      this.staticService.build(localProduct);
    }
  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public Consultation update(Consultation consultation)
  {
    Consultation localConsultation = (Consultation)this.consultationDao.merge(consultation);
    Product localProduct = localConsultation.getProduct();
    if (localProduct != null)
    {
      this.consultationDao.flush();
      this.staticService.build(localProduct);
    }
    return localConsultation;
  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public Consultation update(Consultation consultation, String[] ignoreProperties)
  {
    return (Consultation)this.consultationDao.update(consultation, ignoreProperties);
  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public void delete(Long id)
  {
    this.consultationDao.delete(id);
  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public void delete(Long[] ids)
  {
    if (ids != null)
		for (Long localSerializable : ids)
			this.consultationDao.delete(localSerializable);
  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public void delete(Consultation consultation)
  {
    if (consultation != null)
    {
      this.consultationDao.delete(consultation);
      Product localProduct = consultation.getProduct();
      if (localProduct != null)
      {
        this.consultationDao.flush();
        this.staticService.build(localProduct);
      }
    }
  }
}