package com.hongqiang.shop.modules.shipping.dao;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.modules.entity.OrderItem;

@Repository
public class OrderItemDaoImpl extends BaseDaoImpl<OrderItem>
  implements OrderItemDaoCustom
{
}