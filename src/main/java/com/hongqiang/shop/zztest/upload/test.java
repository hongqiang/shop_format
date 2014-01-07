package com.hongqiang.shop.zztest.upload;

public class test {
	

	
	public static void main(String[] args){
		for(int i=1;i<301;i++){
			String aString="UPDATE hq_product set image = "+
					"(select image from xx_product where id = "+i+"), introduction ="+
					"(select introduction from xx_product where id = "+i+") WHERE id = "+i+";";
			System.out.println(aString);
		}
	}
}
