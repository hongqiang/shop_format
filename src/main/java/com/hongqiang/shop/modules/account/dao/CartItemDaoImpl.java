package com.hongqiang.shop.modules.account.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.CartItem;

@Repository
public class CartItemDaoImpl extends BaseDaoImpl<CartItem>
  implements CartItemDaoCustom{
	
	@Override
	public Page<CartItem>  findPage(Pageable pageable){
		Page<CartItem> cartItemPage = new Page<CartItem>(pageable.getPageNumber(),pageable.getPageSize());
		String qlString = "select cartItem from CartItem cartItem where 1=1 ";
		List<Object> parameter = new ArrayList<Object>();
		return super.findPage(cartItemPage,  qlString,  parameter, pageable);
	}
}