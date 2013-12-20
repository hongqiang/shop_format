package com.hongqiang.shop.website.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.hongqiang.shop.common.persistence.BaseDao;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Filter;
import com.hongqiang.shop.common.utils.Order;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.website.entity.FriendLink;

public interface FriendLinkDao extends FriendLinkDaoCustom, CrudRepository<FriendLink, Long> {

}

/**
 * DAO自定义接口
 * 
 * @author Jack
 * 
 */
interface FriendLinkDaoCustom extends BaseDao<FriendLink> {
  public List<FriendLink> findList(FriendLink.Type paramType);
  
  public  List<FriendLink> findList(Integer first, Integer count, List<Filter> filters, List<Order> orders);
  
  public Page<FriendLink>  findPage(Pageable pageable);
}