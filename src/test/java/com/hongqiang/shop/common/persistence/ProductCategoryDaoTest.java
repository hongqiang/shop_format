package com.hongqiang.shop.common.persistence;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.hongqiang.shop.common.test.SpringTransactionalContextTests;
import com.hongqiang.shop.modules.product.dao.ProductCategoryDao;
import com.hongqiang.shop.modules.entity.ProductCategory;
import com.hongqiang.shop.modules.entity.ProductImage;
import com.hongqiang.shop.modules.product.service.ProductCategoryService;

public class ProductCategoryDaoTest extends SpringTransactionalContextTests{
	@Autowired
	private ProductCategoryDao productCategoryDao;

	@Autowired
	private ProductCategoryService productCategoryService;
	
	public static byte[] getBytes(String filePath){  
        byte[] buffer = null;  
        try {  
            File file = new File(filePath);  
            FileInputStream fis = new FileInputStream(file);  
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
            byte[] b = new byte[1000];  
            int n;  
            while ((n = fis.read(b)) != -1) {  
                bos.write(b, 0, n);  
            }  
            fis.close();  
            bos.close();  
            buffer = bos.toByteArray();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return buffer;  
    }  
	
	@Test
	public void find() throws IOException{
		String name = "D:\\a.png";
		String name2 = "D:\\a2.png";
		FileInputStream fileInputStream = new FileInputStream(name);
		FileOutputStream fileOutputStream = new FileOutputStream(name2);
		int i=fileInputStream.available();
		System.out.println("i="+i);
		File file = new File(name);
		String i1=file.getAbsolutePath();
		System.out.println("i1="+i1);
		
		
		ProductImage productImage = new ProductImage();
		productImage.setFile((MultipartFile) file);
		fileInputStream.close();
//		List<ProductCategory> list = this.productCategoryService.findTree();
//		for(ProductCategory p:list){
//			System.out.println(p.getName()+","+p.getPath());
//		}
//		System.out.print("===== 1=====\n");
//		Long parentId = 1L;
//		ProductCategory productCategory = this.productCategoryService.find(parentId);
//		System.out.println(productCategory);
//		System.out.print("===== 2 =====\n");
//		
//		ProductCategory productCategory2=new ProductCategory();
////		productCategory2.setId(79L);
//		productCategory2.setCreateDate(new Date());
//		productCategory2.setUpdateDate(new Date());
//		productCategory2.setParent(productCategory);
//		 productCategory2.setTreePath(",1,");
//		 productCategory2.setGrade(1);
//		 productCategory2.setChildren(null);
//		 productCategory2.setName("nihao");
//		 this.productCategoryService.save(productCategory2);
//		 System.out.print("===== 3 =====\n");
	}
}
