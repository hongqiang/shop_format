/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.hongqiang.shop.common.persistence;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.util.Version;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.filter.impl.CachingWrapperFilter;
import org.hibernate.search.query.DatabaseRetrievalMethod;
import org.hibernate.search.query.ObjectLookupMethod;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.beans.BeanUtils;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.hongqiang.shop.common.utils.Filter;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.common.utils.Reflections;
import com.hongqiang.shop.common.utils.StringUtils;
import com.hongqiang.shop.modules.entity.OrderEntity;

/**
 * DAO支持类实现
 * 
 * @author ThinkGem
 * @version 2013-05-15
 * @param <T>
 */
public class BaseDaoImpl<T> implements BaseDao<T> {

	// 忽略的属性集合。更新实体类时，不需要更新的属性集合
	private static final String[] ignoreBaseProperties = { "id", "createDate",
			"updateDate" };

	/**
	 * 获取实体工厂管理对象
	 */
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * 实体类类型(由构造方法自动赋值)
	 */
	private Class<T> entityClass;

	/**
	 * 构造方法，根据实例类自动获取实体类类型
	 */
	public BaseDaoImpl() {
		entityClass = Reflections.getClassGenricType(getClass());
	}

	/**
	 * 获取实体工厂管理对象
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * 获取 Session
	 */
	public Session getSession() {
		return (Session) getEntityManager().getDelegate();
	}

	/**
	 * 强制与数据库同步
	 */
	public void flush() {
		getSession().flush();
	}

	/**
	 * 清除缓存数据
	 */
	public void clear() {
		getSession().clear();
	}

	/**
	 * 保存实体类 Make an instance managed and persistent.
	 */
	public void persist(T entity) {
		if (entity != null)
			this.entityManager.persist(entity);
	}

	/**
	 * 更新实体类 Merge the state of the given entity into the current persistence
	 * context.
	 */
	public T merge(T entity) {
		if (entity != null)
			return this.entityManager.merge(entity);
		return null;
	}

	/**
	 * 根据实体类得到其id
	 * 
	 * @param entity
	 * @return
	 */
	public Long getIdentifier(T entity) {
		Object id = this.entityManager.getEntityManagerFactory()
				.getPersistenceUnitUtil().getIdentifier(entity);
		return (Long) id;
	}

	/**
	 * 根据id得到实体类
	 * 
	 * @param id
	 * @return
	 */
	public T find(Long id) {
		if (id != null)
			return this.entityManager.find(this.entityClass, id);// Find by id
		return null;
	}

	/**
	 * 更新实体类
	 * 
	 * @param entity
	 * @param ignoreProperties
	 *            忽略的实体类属性
	 * @return
	 */
	public T update(T entity, String[] ignoreProperties) {
		// Check if the instance is a managed entity instance belonging to the
		// current persistence context.
		if (isManaged(entity))
			throw new IllegalArgumentException("Entity must not be managed");
		T localObject = find(getIdentifier(entity));
		if (localObject != null) {
			BeanUtils.copyProperties(entity, localObject, (String[]) ArrayUtils
					.addAll(ignoreProperties, ignoreBaseProperties));
			return merge(localObject);
		}
		return merge(entity);
	}

	/**
	 * 从数据库删除实体类 Remove the entity instance.
	 */
	public void remove(T entity) {
		if (entity != null)
			this.entityManager.remove(entity);
	}

	/**
	 * 为数据库刷新实体类 Refresh the state of the instance from the database,
	 * overwriting changes made to the entity, if any.
	 */
	public void refresh(T entity) {
		if (entity != null)
			this.entityManager.refresh(entity);
	}

	/**
	 * 判断是否包含实体类
	 */
	public boolean isManaged(T entity) {
		return this.entityManager.contains(entity);
	}

	/**
	 * Remove the given entity from the persistence context, causing a managed
	 * entity to become detached.
	 */
	public void detach(T entity) {
		this.entityManager.detach(entity);
	}

	/**
	 * Lock an entity instance that is contained in the persistence context with
	 * the specified lock mode type.
	 */
	public void lock(T entity, LockModeType lockModeType) {
		if ((entity != null) && (lockModeType != null))
			this.entityManager.lock(entity, lockModeType);
	}
	
	protected void addFilter(StringBuilder qlString, List<Filter> filters,List<Object> params ) {
		if (filters!=null && filters.size() >0) {
			Iterator<Filter> localIterator = filters.iterator();
		 while (localIterator.hasNext()){
	    	  Filter localFilter = (Filter)localIterator.next();
	          if ((localFilter == null) || (StringUtils.isEmpty(localFilter.getProperty())))
	            continue;
	          if ((localFilter.getOperator() == Filter.Operator.eq) && (localFilter.getValue() != null)){
	        	  if ((localFilter.getIgnoreCase() != null) && (localFilter.getIgnoreCase().booleanValue()) 
	        			  && ((localFilter.getValue() instanceof String))){
	        		  qlString.append(" and "+localFilter.getProperty()+" = ? ");
	        		  params.add(((String)localFilter.getValue()).toLowerCase());
	        	  }
	                else{
	                	qlString.append(" and "+localFilter.getProperty()+" = ? ");
		        		  params.add(localFilter.getValue());
	                }
	          }
	          else if ((localFilter.getOperator() == Filter.Operator.ne) && (localFilter.getValue() != null)){
	            if ((localFilter.getIgnoreCase() != null) 
	            		&& (localFilter.getIgnoreCase().booleanValue())
	            		&& ((localFilter.getValue() instanceof String))){
	            	qlString.append(" and "+localFilter.getProperty()+" <> ? ");
	        		  params.add(((String)localFilter.getValue()).toLowerCase());
	            }
	            else{
	            	  qlString.append(" and "+localFilter.getProperty()+" <> ? ");
	        		  params.add(localFilter.getValue());
	            }
	          }
	          else if ((localFilter.getOperator() == Filter.Operator.gt) && (localFilter.getValue() != null)){
	        	  qlString.append(" and "+localFilter.getProperty()+" > ? ");
       		  params.add((Number)localFilter.getValue());
	          }
	          else if ((localFilter.getOperator() == Filter.Operator.lt) && (localFilter.getValue() != null)) {
	        	  qlString.append(" and "+localFilter.getProperty()+" < ? ");
       		  params.add((Number)localFilter.getValue());
	          }
	          else if ((localFilter.getOperator() == Filter.Operator.ge) && (localFilter.getValue() != null)){
	        	  qlString.append(" and "+localFilter.getProperty()+" >= ? ");
       		  params.add((Number)localFilter.getValue());
	          }
	          else if ((localFilter.getOperator() == Filter.Operator.le) && (localFilter.getValue() != null)){
	        	  qlString.append(" and "+localFilter.getProperty()+" <= ? ");
       		  params.add((Number)localFilter.getValue());
	          }
	          else if ((localFilter.getOperator() == Filter.Operator.like) && (localFilter.getValue() != null)
	        		  && ((localFilter.getValue() instanceof String))){
	        	  qlString.append(" and "+localFilter.getProperty()+" like ? ");
       		  params.add((String)localFilter.getValue());
	          }
	          else if ((localFilter.getOperator() == Filter.Operator.in) && (localFilter.getValue() != null)) {
	        	  qlString.append(" and "+localFilter.getProperty()+" in (?) ");
       		  params.add(new Object[] { localFilter.getValue() });
	          }
	          else if (localFilter.getOperator() == Filter.Operator.isNull) {
	        	  qlString.append(" and "+localFilter.getProperty()+" is null ");
	          }
	          else {
	            if (localFilter.getOperator() != Filter.Operator.isNotNull)
	              continue;
	            qlString.append(" and "+localFilter.getProperty()+" is not null ");
	          }
	      }
		}
	}

	protected void addFilter(StringBuilder qlString, 
			Pageable pageable, List<Object> params) {
		if ((StringUtils.isNotEmpty(pageable.getSearchProperty())) 
				&& (StringUtils.isNotEmpty(pageable.getSearchValue()))){
			qlString.append(" and "+pageable.getSearchProperty()+" like ?");
			params.add("%" + pageable.getSearchValue() + "%");
		}
		if (pageable.getFilters() != null)
	    {
	     addFilter(qlString, pageable.getFilters(), params);
	    }
	}
		      
	protected void addOrders(StringBuilder qlString,
			List<com.hongqiang.shop.common.utils.Order> orderList,
			List<Object> params) {
		if (orderList!=null && orderList.size() > 0) {
			qlString.append("order by ");
			Iterator<com.hongqiang.shop.common.utils.Order> localIterator = orderList
					.iterator();
			while (localIterator.hasNext()) {
				com.hongqiang.shop.common.utils.Order localOrder = (com.hongqiang.shop.common.utils.Order) localIterator
						.next();
				if (localOrder.getDirection() == com.hongqiang.shop.common.utils.Order.Direction.asc) {
					qlString.append(" ? ASC,");
					params.add(localOrder.getProperty());
				} else {
					if (localOrder.getDirection() != com.hongqiang.shop.common.utils.Order.Direction.desc)
						continue;
					qlString.append(" ? DESC,");
					params.add(localOrder.getProperty());
				}
			}
			if (qlString.charAt(qlString.length() - 1) == ',') {
				qlString.deleteCharAt(qlString.length() - 1);
			}
		}
	}
	
	protected void addOrders(StringBuilder qlString, 
			Pageable pageable, List<Object> params) {
		int tag = 0;
		if ((StringUtils.isNotEmpty(pageable.getOrderProperty()))
				&& (pageable.getOrderDirection() != null)) {
			tag = 1;
			qlString.append("order by ");
			if (pageable.getOrderDirection() ==
					com.hongqiang.shop.common.utils.Order.Direction.asc) {
				qlString.append(" ? ASC");
				params.add(pageable.getOrderProperty());
			} else if (pageable.getOrderDirection() == 
					com.hongqiang.shop.common.utils.Order.Direction.asc)
				qlString.append(" ? DESC");
			params.add(pageable.getOrderProperty());
		}
		if (pageable.getOrders() != null && pageable.getOrders().size() > 0) {
			if (tag == 0) {
				qlString.append("order by ");
			} else {
				qlString.append(" , ");
			}
			Iterator<com.hongqiang.shop.common.utils.Order> localIterator = pageable
					.getOrders().iterator();
			while (localIterator.hasNext()) {
				com.hongqiang.shop.common.utils.Order localOrder =
						(com.hongqiang.shop.common.utils.Order) localIterator.next();
				if (localOrder.getDirection() == 
						com.hongqiang.shop.common.utils.Order.Direction.asc) {
					qlString.append(" ? ASC,");
					params.add(localOrder.getProperty());
				} else {
					if (localOrder.getDirection() != 
							com.hongqiang.shop.common.utils.Order.Direction.desc)
						continue;
					qlString.append(" ? DESC,");
					params.add(localOrder.getProperty());
				}
			}
			if (qlString.charAt(qlString.length() - 1) == ',') {
				qlString.deleteCharAt(qlString.length() - 1);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Long count(StringBuilder qlString, List<Filter> filters, List<Object> params) {
		addFilter(qlString, filters, params);
		Query query = createQuery(qlString.toString(), params.toArray());
		List<Object> list = query.list();
		return new Long((long)list.size());
	}
	// -------------- QL Query --------------

	/**
	 * QL 分页查询
	 * 
	 * @param page
	 * @param qlString
	 * @param parameter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E> Page<E> find(Page<E> page, String qlString, Object... parameter) {
		// get count
		if (!page.isDisabled() && !page.isNotCount()) {
			String countQlString = "select count(*) "
					+ removeSelect(removeOrders(qlString));
			// page.setCount(Long.valueOf(createQuery(countQlString,
			// parameter).uniqueResult().toString()));
			Query query = createQuery(countQlString, parameter);
			List<Object> list = query.list();
			if (list.size() > 0) {
				page.setCount(Long.valueOf(list.get(0).toString()));
			} else {
				page.setCount(list.size());
			}
			if (page.getCount() < 1) {
				return page;
			}
		}
		// order by
		String ql = qlString;
		if (StringUtils.isNotBlank(page.getOrderBy())) {
			ql += " order by " + page.getOrderBy();
		}
		Query query = createQuery(ql, parameter);
		// set page
		if (!page.isDisabled()) {
			query.setFirstResult(page.getFirstResult());
			query.setMaxResults(page.getMaxResults());
		}
		page.setList(query.list());
		return page;
	}

	/**
	 * QL 分页查询
	 * 
	 * @param page
	 * @param qlString
	 * @param parameter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E> Page<E> findPage(Page<E> page, String qlString,
			Object[] parameter) {
		// get count
		if (!page.isDisabled() && !page.isNotCount()) {
			String countQlString = "select count(*) "
					+ removeSelect(removeOrders(qlString));
			// page.setCount(Long.valueOf(createQuery(countQlString,
			// parameter).uniqueResult().toString()));
			Query query = createQueryByList(countQlString, parameter);
			List<Object> list = query.list();
			if (list.size() > 0) {
				page.setCount(Long.valueOf(list.get(0).toString()));
			} else {
				page.setCount(list.size());
			}
			if (page.getCount() < 1) {
				return page;
			}
		}
		// order by
		String ql = qlString;
		if (StringUtils.isNotBlank(page.getOrderBy())) {
			ql += " order by " + page.getOrderBy();
		}
		Query query = createQuery(ql, parameter);
		// set page
		if (!page.isDisabled()) {
			query.setFirstResult(page.getFirstResult());
			query.setMaxResults(page.getMaxResults());
		}
		page.setList(query.list());
		return page;
	}

	/**
	 * QL 查询
	 * 
	 * @param qlString
	 * @param parameter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E> List<E> find(String qlString, Object... parameter) {
		Query query = createQuery(qlString, parameter);
		return query.list();
	}

	/**
	 * QL 查询
	 * 
	 * @param qlString
	 *            --shop
	 * @param parameter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E> List<E> findList(String qlString, List<Object> parameter,
			Integer firstResults, Integer MaxResults, List<Filter> filters,
			List<com.hongqiang.shop.common.utils.Order> orderList) {
		StringBuilder stringBuilder = new StringBuilder(qlString);
		
		addFilter(stringBuilder, filters, parameter);
		if (qlString.indexOf("order by")==-1) {
			addOrders(stringBuilder, orderList, parameter);
		}
		
		qlString = stringBuilder.toString();
//		System.out.println("qlstirng= "+qlString);
		if (qlString.indexOf("order by")==-1) {
			if (OrderEntity.class.isAssignableFrom(this.entityClass)){
				qlString += "order by order ASC";
			}else {
				qlString += "order by createDate DESC";
			}
		}
		System.out.println("productQuery = "+qlString);
		System.out.println(parameter.size());
		for (Object object : parameter) {
			System.out.println("object="+object);
		}
		Query query = createQueryByList(qlString, parameter.toArray());
		if (firstResults != null) {
			query.setFirstResult(firstResults);
		}
		if (MaxResults != null) {
			query.setMaxResults(MaxResults);
		}
		return query.list();
	}

	public <E> Page<E> findPage(Page<E> page, String qlString, List<Object> parameter,Pageable pageable) {
		 if (pageable == null)
			 pageable = new Pageable();
		 StringBuilder stringBuilder = new StringBuilder(qlString);
		 addFilter(stringBuilder, pageable, parameter);
		 if (qlString.indexOf("order by")==-1) {
			 addOrders(stringBuilder, pageable, parameter);
		 }
		 qlString = stringBuilder.toString();
		 if (qlString.indexOf("order by")==-1) {
				if (OrderEntity.class.isAssignableFrom(this.entityClass)){
					qlString += "order by order ASC";
				}else {
					qlString += "order by createDate DESC";
				}
			}
		 long count = count(stringBuilder, null, parameter);
		 int i=(int)Math.ceil(count/pageable.getPageSize());
		 if (i<pageable.getPageNumber()) {
			pageable.setPageNumber(i);
		}
		 page.setPageNo(pageable.getPageNumber());
		 page.setPageSize(pageable.getPageSize());
		 return find(page,qlString,parameter.toArray());
	}
	
	/**
	 * QL 更新
	 * 
	 * @param qlString
	 * @param parameter
	 * @return
	 */
	public int update(String qlString, Object... parameter) {
		return createQuery(qlString, parameter).executeUpdate();
	}

	/**
	 * 创建 QL 查询对象
	 * 
	 * @param qlString
	 * @param parameter
	 * @return
	 */
	public Query createQuery(String qlString, Object... parameter) {
		Query query = getSession().createQuery(qlString);
		setParameter(query, parameter);
		return query;
	}

	/**
	 * 创建 QL 查询对象--shop
	 * 
	 * @param qlString
	 * @param parameter
	 * @return
	 */
	public Query createQueryByList(String qlString, Object[] parameter) {
		Query query = getSession().createQuery(qlString);
		setParameterByList(query, parameter);
		return query;
	}

	// -------------- SQL Query --------------

	/**
	 * SQL 分页查询
	 * 
	 * @param page
	 * @param sqlString
	 * @param parameter
	 * @return
	 */
	public <E> Page<E> findBySql(Page<E> page, String sqlString,
			Object... parameter) {
		return findBySql(page, sqlString, null, parameter);
	}

	/**
	 * SQL 分页查询
	 * 
	 * @param page
	 * @param sqlString
	 * @param resultClass
	 * @param parameter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E> Page<E> findBySql(Page<E> page, String sqlString,
			Class<?> resultClass, Object... parameter) {
		// get count
		if (!page.isDisabled() && !page.isNotCount()) {
			String countSqlString = "select count(*) "
					+ removeSelect(removeOrders(sqlString));
			// page.setCount(Long.valueOf(createSqlQuery(countSqlString,
			// parameter).uniqueResult().toString()));
			Query query = createSqlQuery(countSqlString, parameter);
			List<Object> list = query.list();
			if (list.size() > 0) {
				page.setCount(Long.valueOf(list.get(0).toString()));
			} else {
				page.setCount(list.size());
			}
			if (page.getCount() < 1) {
				return page;
			}
		}
		// order by
		String sql = sqlString;
		if (StringUtils.isNotBlank(page.getOrderBy())) {
			sql += " order by " + page.getOrderBy();
		}
		SQLQuery query = createSqlQuery(sql, parameter);
		// set page
		if (!page.isDisabled()) {
			query.setFirstResult(page.getFirstResult());
			query.setMaxResults(page.getMaxResults());
		}
		setResultTransformer(query, resultClass);
		page.setList(query.list());
		return page;
	}

	/**
	 * SQL 查询
	 * 
	 * @param sqlString
	 * @param parameter
	 * @return
	 */
	public <E> List<E> findBySql(String sqlString, Object... parameter) {
		return findBySql(sqlString, null, parameter);
	}

	/**
	 * SQL 查询
	 * 
	 * @param sqlString
	 * @param resultClass
	 * @param parameter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E> List<E> findBySql(String sqlString, Class<?> resultClass,
			Object... parameter) {
		SQLQuery query = createSqlQuery(sqlString, parameter);
		setResultTransformer(query, resultClass);
		return query.list();
	}

	/**
	 * SQL 更新
	 * 
	 * @param sqlString
	 * @param parameter
	 * @return
	 */
	public int updateBySql(String sqlString, Object... parameter) {
		return createSqlQuery(sqlString, parameter).executeUpdate();
	}

	/**
	 * 创建 SQL 查询对象
	 * 
	 * @param sqlString
	 * @param parameter
	 * @return
	 */
	public SQLQuery createSqlQuery(String sqlString, Object... parameter) {
		SQLQuery query = getSession().createSQLQuery(sqlString);
		setParameter(query, parameter);
		return query;
	}

	// -------------- Query Tools --------------

	/**
	 * 设置查询结果类型
	 * 
	 * @param query
	 * @param resultClass
	 */
	private void setResultTransformer(SQLQuery query, Class<?> resultClass) {
		if (resultClass != null) {
			if (resultClass == Map.class) {
				query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			} else if (resultClass == List.class) {
				query.setResultTransformer(Transformers.TO_LIST);
			} else {
				query.addEntity(resultClass);
			}
		}
	}

	/**
	 * 设置查询参数
	 * 
	 * @param query
	 * @param parameter
	 */
	private void setParameter(Query query, Object... parameter) {
		if (parameter != null) {
			for (int i = 0; i < parameter.length; i++) {
				query.setParameter(i, parameter[i]);
			}
		}
	}

	/**
	 * 设置查询参数--shop
	 * 
	 * @param query
	 * @param parameter
	 */
	private void setParameterByList(Query query, Object[] parameter) {
		if (parameter != null) {
			for (int i = 0; i < parameter.length; i++) {
				query.setParameter(i, parameter[i]);
			}
		}
	}

	/**
	 * 去除qlString的select子句。
	 * 
	 * @param hql
	 * @return
	 */
	private String removeSelect(String qlString) {
		int beginPos = qlString.toLowerCase().indexOf("from");
		return qlString.substring(beginPos);
	}

	/**
	 * 去除hql的orderBy子句。
	 * 
	 * @param hql
	 * @return
	 */
	private String removeOrders(String qlString) {
		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*",
				Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(qlString);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	// -------------- Criteria --------------

	/**
	 * 分页查询
	 * 
	 * @param page
	 * @return
	 */
	public Page<T> find(Page<T> page) {
		return find(page, createDetachedCriteria());
	}

	/**
	 * 使用检索标准对象分页查询
	 * 
	 * @param page
	 * @param detachedCriteria
	 * @param resultTransformer
	 * @return
	 */
	public Page<T> find(Page<T> page, DetachedCriteria detachedCriteria) {
		return find(page, detachedCriteria, Criteria.DISTINCT_ROOT_ENTITY);
	}

	/**
	 * 使用检索标准对象分页查询
	 * 
	 * @param page
	 * @param detachedCriteria
	 * @param resultTransformer
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Page<T> find(Page<T> page, DetachedCriteria detachedCriteria,
			ResultTransformer resultTransformer) {
		// get count
		if (!page.isDisabled() && !page.isNotCount()) {
			page.setCount(count(detachedCriteria));
			if (page.getCount() < 1) {
				return page;
			}
		}
		Criteria criteria = detachedCriteria
				.getExecutableCriteria(getSession());
		criteria.setResultTransformer(resultTransformer);
		// set page
		if (!page.isDisabled()) {
			criteria.setFirstResult(page.getFirstResult());
			criteria.setMaxResults(page.getMaxResults());
		}
		// order by
		if (StringUtils.isNotBlank(page.getOrderBy())) {
			for (String order : StringUtils.split(page.getOrderBy(), ",")) {
				String[] o = StringUtils.split(order, " ");
				if (o.length == 1) {
					criteria.addOrder(Order.asc(o[0]));
				} else if (o.length == 2) {
					if ("DESC".equals(o[1].toUpperCase())) {
						criteria.addOrder(Order.desc(o[0]));
					} else {
						criteria.addOrder(Order.asc(o[0]));
					}
				}
			}
		}
		page.setList(criteria.list());
		return page;
	}

	/**
	 * 使用检索标准对象查询
	 * 
	 * @param detachedCriteria
	 * @return
	 */
	public List<T> find(DetachedCriteria detachedCriteria) {
		return find(detachedCriteria, Criteria.DISTINCT_ROOT_ENTITY);
	}

	/**
	 * 使用检索标准对象查询
	 * 
	 * @param detachedCriteria
	 * @param resultTransformer
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> find(DetachedCriteria detachedCriteria,
			ResultTransformer resultTransformer) {
		Criteria criteria = detachedCriteria
				.getExecutableCriteria(getSession());
		criteria.setResultTransformer(resultTransformer);
		return criteria.list();
	}

	/**
	 * 使用检索标准对象查询记录数
	 * 
	 * @param detachedCriteria
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public long count(DetachedCriteria detachedCriteria) {
		Criteria criteria = detachedCriteria
				.getExecutableCriteria(getSession());
		long totalCount = 0;
		try {
			// Get orders
			Field field = CriteriaImpl.class.getDeclaredField("orderEntries");
			field.setAccessible(true);
			List orderEntrys = (List) field.get(criteria);
			// Remove orders
			field.set(criteria, new ArrayList());
			// Get count
			criteria.setProjection(Projections.rowCount());
			totalCount = Long.valueOf(criteria.uniqueResult().toString());
			// Clean count
			criteria.setProjection(null);
			// Restore orders
			field.set(criteria, orderEntrys);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return totalCount;
	}

	/**
	 * 创建与会话无关的检索标准对象
	 * 
	 * @param criterions
	 *            Restrictions.eq("name", value);
	 * @return
	 */
	public DetachedCriteria createDetachedCriteria(Criterion... criterions) {
		DetachedCriteria dc = DetachedCriteria.forClass(entityClass);
		for (Criterion c : criterions) {
			dc.add(c);
		}
		return dc;
	}

	// -------------- Hibernate search --------------

	/**
	 * 获取全文Session
	 */
	public FullTextSession getFullTextSession() {
		return Search.getFullTextSession(getSession());
	}

	/**
	 * 建立索引
	 */
	public void createIndex() {
		try {
			getFullTextSession().createIndexer(entityClass).startAndWait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 全文检索
	 * 
	 * @param page
	 *            分页对象
	 * @param query
	 *            关键字查询对象
	 * @param queryFilter
	 *            查询过滤对象
	 * @param sort
	 *            排序对象
	 * @return 分页对象
	 */
	@SuppressWarnings("unchecked")
	public Page<T> search(Page<T> page, BooleanQuery query,
			BooleanQuery queryFilter, Sort sort) {

		// 按关键字查询
		FullTextQuery fullTextQuery = getFullTextSession().createFullTextQuery(
				query, entityClass);

		// 过滤无效的内容
		if (queryFilter != null) {
			fullTextQuery.setFilter(new CachingWrapperFilter(
					new QueryWrapperFilter(queryFilter)));
		}

		// 设置排序
		if (sort != null) {
			fullTextQuery.setSort(sort);
		}

		// 定义分页
		page.setCount(fullTextQuery.getResultSize());
		fullTextQuery.setFirstResult(page.getFirstResult());
		fullTextQuery.setMaxResults(page.getMaxResults());

		// 先从持久化上下文中查找对象，如果没有再从二级缓存中查找
		fullTextQuery.initializeObjectsWith(
				ObjectLookupMethod.SECOND_LEVEL_CACHE,
				DatabaseRetrievalMethod.QUERY);

		// 返回结果
		page.setList(fullTextQuery.list());

		return page;
	}

	/**
	 * 获取全文查询对象
	 */
	public BooleanQuery getFullTextQuery(BooleanClause... booleanClauses) {
		BooleanQuery booleanQuery = new BooleanQuery();
		for (BooleanClause booleanClause : booleanClauses) {
			booleanQuery.add(booleanClause);
		}
		return booleanQuery;
	}

	/**
	 * 获取全文查询对象
	 * 
	 * @param q
	 *            查询关键字
	 * @param fields
	 *            查询字段
	 * @return 全文查询对象
	 */
	public BooleanQuery getFullTextQuery(String q, String... fields) {
		Analyzer analyzer = new IKAnalyzer();
		BooleanQuery query = new BooleanQuery();
		try {
			if (StringUtils.isNotBlank(q)) {
				for (String field : fields) {
					QueryParser parser = new QueryParser(Version.LUCENE_36,
							field, analyzer);
					query.add(parser.parse(q), Occur.SHOULD);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return query;
	}

	/**
	 * 设置关键字高亮
	 * 
	 * @param query
	 *            查询对象
	 * @param list
	 *            设置高亮的内容列表
	 * @param subLength
	 *            截取长度
	 * @param fields
	 *            字段名
	 */
	public List<T> keywordsHighlight(BooleanQuery query, List<T> list,
			int subLength, String... fields) {
		Analyzer analyzer = new IKAnalyzer();
		Formatter formatter = new SimpleHTMLFormatter(
				"<span class=\"highlight\">", "</span>");
		Highlighter highlighter = new Highlighter(formatter, new QueryScorer(
				query));
		highlighter.setTextFragmenter(new SimpleFragmenter(subLength));
		for (T entity : list) {
			try {
				for (String field : fields) {
					String text = StringUtils.replaceHtml((String) Reflections
							.invokeGetter(entity, field));
					String description = highlighter.getBestFragment(analyzer,
							field, text);
					if (description != null) {
						Reflections
								.invokeSetter(entity, fields[0], description);
						break;
					}
					Reflections.invokeSetter(entity, fields[0],
							StringUtils.abbr(text, subLength * 2));
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidTokenOffsetsException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
}