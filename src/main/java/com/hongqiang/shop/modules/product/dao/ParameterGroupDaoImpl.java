package com.hongqiang.shop.modules.product.dao;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.FlushModeType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Parameter;
import com.hongqiang.shop.modules.entity.ParameterGroup;
import com.hongqiang.shop.modules.entity.Product;

@Repository
public class ParameterGroupDaoImpl extends BaseDaoImpl<ParameterGroup>
		implements ParameterGroupDaoCustom {
	@Autowired
	private ParameterDao parameterDao;

	public Page<ParameterGroup> findPage(Pageable pageable){
		Page<ParameterGroup> parameterGroupPage = new Page<ParameterGroup>(pageable.getPageNumber(),pageable.getPageSize());
		String qlString = "select parameterGroup from ParameterGroup parameterGroup";
		return super.find(parameterGroupPage, qlString);
	}
	
	public ParameterGroup merge(ParameterGroup parameterGroup) {

		HashSet<Parameter> localHashSet = new HashSet<Parameter>();
		List<Parameter> parameterGroups = parameterGroup.getParameters();
		for (Parameter tempParameter : parameterGroups) {
			if ((tempParameter != null) && (tempParameter.getId() != null)) {
				localHashSet.add(tempParameter);
			}
		}
		List<Parameter> localList1 = this.parameterDao.findList(parameterGroup,localHashSet);
		for (int i = 0; i < localList1.size(); i++) {
			Parameter localParameter = (Parameter) localList1.get(i);
			String str = "select product from Product product join product.parameterValue parameterValue where index(parameterValue) = :parameter";
			List<Product> localList2 = this.getEntityManager()
					.createQuery(str, Product.class)
					.setFlushMode(FlushModeType.COMMIT)
					.setParameter("parameter", localParameter).getResultList();
			Iterator<Product> localIterator = localList2.iterator();
			while (localIterator.hasNext()) {
				Product localProduct = (Product) localIterator.next();
				localProduct.getParameterValue().remove(localParameter);
				if (i % 20 != 0)
					continue;
				super.flush();
				super.clear();
			}
		}
		return (ParameterGroup) super.merge(parameterGroup);
	}

	public void remove(ParameterGroup parameterGroup) {
		if (parameterGroup != null) {
			for (int i = 0; i < parameterGroup.getParameters().size(); i++) {
				Parameter localParameter = (Parameter) parameterGroup
						.getParameters().get(i);
				String str = "select product from Product product join product.parameterValue parameterValue where index(parameterValue) = :parameter";
				List<Product> localList = this.getEntityManager()
						.createQuery(str, Product.class)
						.setFlushMode(FlushModeType.COMMIT)
						.setParameter("parameter", localParameter)
						.getResultList();
				Iterator<Product> localIterator = localList.iterator();
				while (localIterator.hasNext()) {
					Product localProduct = (Product) localIterator.next();
					localProduct.getParameterValue().remove(localParameter);
					if (i % 20 != 0)
						continue;
					super.flush();
					super.clear();
				}
			}
			super.remove((ParameterGroup) super.merge(parameterGroup));
		}
	}
}