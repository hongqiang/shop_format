package com.hongqiang.shop.modules.user.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.Member;
import com.hongqiang.shop.modules.entity.Order;

@Repository
public class MemberDaoImpl extends BaseDaoImpl<Member>
  implements MemberDaoCustom
{
 
 @Override
  public Member findByUsername(String username)
  {
    if (username == null)
      return null;
    try
    {
      String str = "select members from Member members where lower(members.username) = lower(:username)";
      return (Member)this.getEntityManager().createQuery(str, Member.class).setFlushMode(FlushModeType.COMMIT).setParameter("username", username).getSingleResult();
    }
    catch (NoResultException localNoResultException1)
    {
    }
    return null;
  }

  @Override
  public List<Member> findListByEmail(String email)
  {
    if (email == null)
      return Collections.emptyList();
    String str = "select members from Member members where lower(members.email) = lower(:email)";
    return this.getEntityManager().createQuery(str, Member.class).setFlushMode(FlushModeType.COMMIT).setParameter("email", email).getResultList();
  }

  @Override
  public Page<Member> findPurchasePage(Date beginDate, Date endDate, Pageable pageable){
  
	String sqlString = "select DISTINCT member, sum(order.amountPaid) from Member member, Order order where 1=1 ";
	List<Object> params = new ArrayList<Object>();
    if (beginDate != null){
		sqlString += " and order.createDate >= ?";
		params.add(beginDate);
	}
    if (endDate != null){
		sqlString += " and order.createDate <= ?";
		params.add(endDate);
	}
	sqlString += " and order.orderStatus = ?";
	params.add(Order.OrderStatus.completed);
	
	sqlString += " and order.paymentStatus = ?";
	params.add(Order.PaymentStatus.paid);
	
	sqlString += " group by member.id";
	
	sqlString += " order by sum(order.amountPaid) DESC";
	//结果总数除以pagesize得到每页显示的条数，如100条除以10页，则每页显示10条，设置pagenumber为10.
	//这里先使用jeesite自带的函数find(page)，遇到问题再修改。
	Page<Member> memberPage = new Page<Member>(pageable.getPageNumber(),pageable.getPageSize());
	return this.find(memberPage,sqlString,params.toArray());  
  }
  
  @Override
  public Page<Member> findPage(Pageable pageable){
	Page<Member> adminPage = new Page<Member>(pageable.getPageNumber(),pageable.getPageSize());
		String sqlString = "select members from Member members";
		return super.find(adminPage, sqlString);
  }
  
  @Override
  public boolean usernameExists(String username)
  {
    if (username == null)
      return false;
    String str = "select count(*) from Member members where lower(members.username) = lower(:username)";
    Long localLong = (Long)this.getEntityManager().createQuery(str, Long.class).setFlushMode(FlushModeType.COMMIT).setParameter("username", username).getSingleResult();
    return localLong.longValue() > 0L;
  }

  @Override
  public boolean emailExists(String email)
  {
    if (email == null)
      return false;
    String str = "select count(*) from Member members where lower(members.email) = lower(:email)";
    Long localLong = (Long)this.getEntityManager().createQuery(str, Long.class).setFlushMode(FlushModeType.COMMIT).setParameter("email", email).getSingleResult();
    return localLong.longValue() > 0L;
  }
}