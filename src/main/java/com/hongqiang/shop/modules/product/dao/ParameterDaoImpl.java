package com.hongqiang.shop.modules.product.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.hongqiang.shop.common.persistence.BaseDaoImpl;
import com.hongqiang.shop.modules.entity.Parameter;
import com.hongqiang.shop.modules.entity.ParameterGroup;

@Repository
public class ParameterDaoImpl extends BaseDaoImpl<Parameter>
  implements ParameterDaoCustom
{
  public List<Parameter> findList(ParameterGroup parameterGroup, Set<Parameter> excludes)
  {
	  String sqlString = "select DISTINCT parameter from Parameter parameter where 1=1 ";
	  List<Object> params = new ArrayList<Object>();
	  if (parameterGroup != null){
		  System.out.print("wo zai zheli "+parameterGroup.getName()+", "+parameterGroup.getProductCategory()+"\n");
		  sqlString += " and parameter.parameterGroup = ? ";
		  params.add(parameterGroup);
	  }
	  if ((excludes != null) && (!excludes.isEmpty())){
		  System.out.print("wo zai zheli ");
			for (Parameter op : excludes) {
				System.out.print(op.getName()+", "+op.getParameterGroup()+"\n");
			}
			sqlString += " and parameter not in (";
			for (Parameter parameter : excludes) {
					sqlString += " ?, ";
					params.add(parameter);
			}
			sqlString = sqlString.substring(0,sqlString.length()-2);
			sqlString +=")";
//		  sqlString += " and parameter not in (?)";
//		  params.add(excludes);
	  }
	  System.out.print("sql="+sqlString+"\n");
	  return super.find(sqlString,params.toArray());
  }
}