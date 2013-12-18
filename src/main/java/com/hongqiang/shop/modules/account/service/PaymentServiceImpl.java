package com.hongqiang.shop.modules.account.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.service.BaseService;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.account.dao.PaymentDao;
import com.hongqiang.shop.modules.entity.Payment;

@Service
public class PaymentServiceImpl extends BaseService implements PaymentService {

	@Autowired
	private PaymentDao paymentDao;

	@Transactional(readOnly = true)
	public Payment findBySn(String sn) {
		return this.paymentDao.findBySn(sn);
	}

	@Transactional
	public Payment find(Long id) {
		return this.paymentDao.find(id);
	}

	@Transactional
	public Page<Payment> findPage(Pageable pageable) {
		return this.paymentDao.findPage(pageable);
	}

	@Transactional
	public void save(Payment paymentDao) {
		this.paymentDao.persist(paymentDao);
	}

	@Transactional
	public Payment update(Payment paymentDao) {
		return (Payment) this.paymentDao.merge(paymentDao);
	}

	@Transactional
	public Payment update(Payment paymentDao, String[] ignoreProperties) {
		return (Payment) this.paymentDao.update(paymentDao, ignoreProperties);
	}

	@Transactional
	public void delete(Long id) {

		this.paymentDao.delete(id);
	}

	@Transactional
	public void delete(Long[] ids) {
		if (ids != null)
			for (Long localSerializable : ids)
				this.paymentDao.delete(localSerializable);
	}

	@Transactional
	public void delete(Payment paymentDao) {
		this.paymentDao.delete(paymentDao);
	}
}