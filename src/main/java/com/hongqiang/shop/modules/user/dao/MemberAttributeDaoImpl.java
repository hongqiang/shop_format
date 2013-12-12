package com.hongqiang.shop.modules.user.dao;

import java.util.List;

import javax.persistence.FlushModeType;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.common.persistence.Page;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.entity.MemberAttribute;

@Repository
public class MemberAttributeDaoImpl extends BaseDaoImpl<MemberAttribute>
  implements MemberAttributeDaoCustom
{
@Override
  public Integer findUnusedPropertyIndex()
  {
    for (int i = 0; i < 10; i++)
    {
      String str = "select count(*) from MemberAttribute memberAttribute where memberAttribute.propertyIndex = :propertyIndex";
      Long localLong = (Long)this.getEntityManager().createQuery(str, Long.class).setFlushMode(FlushModeType.COMMIT).setParameter("propertyIndex", Integer.valueOf(i)).getSingleResult();
      if (localLong.longValue() == 0L)
        return Integer.valueOf(i);
    }
    return null;
  }
  
  @Override
	public Page<MemberAttribute>  findPage(Pageable pageable){
		Page<MemberAttribute> memberAttributePage = new Page<MemberAttribute>(pageable.getPageNumber(),pageable.getPageSize());
		String qlString = "select memberAttribute from MemberAttribute memberAttribute";
		return super.find(memberAttributePage, qlString);
	}

  @Override
  public List<MemberAttribute> findList()
  {
    String str = "select memberAttribute from MemberAttribute memberAttribute where memberAttribute.isEnabled = true order by memberAttribute.order asc";
    return this.getEntityManager().createQuery(str, MemberAttribute.class).setFlushMode(FlushModeType.COMMIT).getResultList();
  }

  @Override
  public void remove(MemberAttribute memberAttribute)
  {
    if ((memberAttribute != null) && ((memberAttribute.getType() == MemberAttribute.Type.text) || (memberAttribute.getType() == MemberAttribute.Type.select) || (memberAttribute.getType() == MemberAttribute.Type.checkbox)))
    {
      String str1 = "attributeValue" + memberAttribute.getPropertyIndex();
      String str2 = "update Member members set members." + str1 + " = null";
      this.getEntityManager().createQuery(str2).setFlushMode(FlushModeType.COMMIT).executeUpdate();
      super.remove(memberAttribute);
    }
  }
}