package com.hongqiang.shop.common.persistence;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hongqiang.shop.common.test.SpringTransactionalContextTests;
import com.hongqiang.shop.modules.product.dao.ProductCategoryDao;
import com.hongqiang.shop.modules.entity.ProductCategory;
import com.hongqiang.shop.modules.product.service.ProductCategoryService;

public class ProductCategoryDaoTest extends SpringTransactionalContextTests{
	@Autowired
	private ProductCategoryDao productCategoryDao;

	@Autowired
	private ProductCategoryService productCategoryService;
	
	@Test
	public void find(){
		List<ProductCategory> list = this.productCategoryService.findTree();
		for(ProductCategory p:list){
			System.out.println(p.getName()+","+p.getPath());
		}
		System.out.print("===== 1=====\n");
		Long parentId = 1L;
		ProductCategory productCategory = this.productCategoryService.find(parentId);
		System.out.println(productCategory);
		System.out.print("===== 2 =====\n");
		
		ProductCategory productCategory2=new ProductCategory();
//		productCategory2.setId(79L);
		productCategory2.setCreateDate(new Date());
		productCategory2.setUpdateDate(new Date());
		productCategory2.setParent(productCategory);
		 productCategory2.setTreePath(",1,");
		 productCategory2.setGrade(1);
		 productCategory2.setChildren(null);
		 productCategory2.setName("nihao");
		 this.productCategoryService.save(productCategory2);
		 System.out.print("===== 3 =====\n");
	}
}
