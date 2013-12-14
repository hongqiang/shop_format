package com.hongqiang.shop.modules.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hongqiang.shop.common.service.BaseService;
import com.hongqiang.shop.modules.entity.SpecificationValue;
import com.hongqiang.shop.modules.product.dao.SpecificationValueDao;

@Service
public class SpecificationValueServiceImpl extends BaseService implements
		SpecificationValueService {
	@Autowired
	private SpecificationValueDao specificationValueDao;

	@Transactional
	public SpecificationValue find(Long id) {
		return this.specificationValueDao.findById(id);
	}

	@Transactional
	@CacheEvict(value={"specificationValue"}, allEntries=true)
	public void save(SpecificationValue specificationValue) {
		this.specificationValueDao.persist(specificationValue);
	}

	@Transactional
	@CacheEvict(value={"specificationValue"}, allEntries=true)
	public SpecificationValue update(SpecificationValue specificationValue) {
		return (SpecificationValue) this.specificationValueDao
				.merge(specificationValue);
	}

	 @Transactional
	 @CacheEvict(value={"specificationValue"}, allEntries=true)
	 public SpecificationValue update(SpecificationValue specificationValue,
	 String[] ignoreProperties)
	 {
		 return (SpecificationValue)this.specificationValueDao.update(specificationValue,ignoreProperties);
	 }

	@Transactional
	public void delete(Long id) {
		this.specificationValueDao.delete(id);
	}

	@Transactional
	public void delete(Long[] ids) {
		if (ids != null)
			for (Long localSerializable : ids)
				this.specificationValueDao.delete(localSerializable);
	}

	@Transactional
	public void delete(SpecificationValue specificationValue) {
		this.specificationValueDao.delete(specificationValue);
	}

}