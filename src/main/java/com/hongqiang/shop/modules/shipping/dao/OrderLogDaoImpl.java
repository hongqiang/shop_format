package com.hongqiang.shop.modules.shipping.dao;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.modules.entity.OrderLog;

@Repository
public class OrderLogDaoImpl extends BaseDaoImpl<OrderLog>
  implements OrderLogDaoCustom
{
}
