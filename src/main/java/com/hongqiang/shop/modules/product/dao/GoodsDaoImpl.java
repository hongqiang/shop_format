package com.hongqiang.shop.modules.product.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import javax.persistence.FlushModeType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.modules.entity.Goods;
import com.hongqiang.shop.modules.entity.Product;
import com.hongqiang.shop.modules.entity.Sn;
import com.hongqiang.shop.modules.entity.SpecificationValue;

@Repository
class GoodsDaoImpl extends BaseDaoImpl<Goods> implements GoodsDaoCustom {
	class SortSpecificationValue implements Comparator<SpecificationValue> {
		public int compare(SpecificationValue a1, SpecificationValue a2) {
			return new CompareToBuilder().append(a1.getSpecification(),
					a2.getSpecification()).toComparison();
		}
	}

	@Autowired
	private ProductDao productDao;

	 @Autowired
	 private SnDao snDao;

	public void persist(Goods goods) {
		if (goods.getProducts() != null) {
			Iterator<Product> localIterator = goods.getProducts().iterator();
			while (localIterator.hasNext()) {
				Product localProduct = (Product) localIterator.next();
				setProductFullName(localProduct);
			}
		}
		super.persist(goods);
	}

	public Goods merge(Goods goods) {
		if (goods.getProducts() != null) {
			Iterator<Product> localIterator = goods.getProducts().iterator();
			while (localIterator.hasNext()) {
				Product localProduct = (Product) localIterator.next();
				if (localProduct.getId() != null) {
					String str;
					if (!localProduct.getIsGift().booleanValue()) {
						str = "delete from GiftItem giftItem where giftItem.gift = :product";
						this.getEntityManager().createQuery(str)
								.setFlushMode(FlushModeType.COMMIT)
								.setParameter("product", localProduct)
								.executeUpdate();
					}
					if ((!localProduct.getIsMarketable().booleanValue())
							|| (localProduct.getIsGift().booleanValue())) {
						str = "delete from CartItem cartItem where cartItem.product = :product";
						this.getEntityManager().createQuery(str)
								.setFlushMode(FlushModeType.COMMIT)
								.setParameter("product", localProduct)
								.executeUpdate();
					}
				}
				setProductFullName(localProduct);
			}
		}
		return (Goods) super.merge(goods);
	}

	private void setProductFullName(Product paramProduct) {
		if (paramProduct == null)
			return;
		if (StringUtils.isEmpty(paramProduct.getSn())) {
			String localObject ;
			 do{
				  localObject = this.snDao.generate(Sn.Type.product);
			 } while (this.productDao.snExists(localObject));
			 paramProduct.setSn(localObject);
		}
		Object localObject = new StringBuffer(paramProduct.getName());
		if ((paramProduct.getSpecificationValues() != null)
				&& (!paramProduct.getSpecificationValues().isEmpty())) {
			ArrayList<SpecificationValue> localArrayList = new ArrayList<SpecificationValue>(
					paramProduct.getSpecificationValues());
			Collections.sort(localArrayList, new SortSpecificationValue());
			((StringBuffer) localObject).append("[");
			int i = 0;
			Iterator<SpecificationValue> localIterator = localArrayList
					.iterator();
			while (localIterator.hasNext()) {
				if (i != 0)
					((StringBuffer) localObject).append(" ");
				((StringBuffer) localObject)
						.append(((SpecificationValue) localIterator.next())
								.getName());
				i++;
			}
			((StringBuffer) localObject).append("]");
		}
		paramProduct.setFullName(((StringBuffer) localObject).toString());
	}
}