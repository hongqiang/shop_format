package com.hongqiang.shop.common.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page<T>
  implements Serializable
{
  private static final long serialVersionUID = -2053800594583879853L;
  private final List<T> content = new ArrayList<T>();
  private final long total;
  private final Pageable pageable;

  public Page()
  {
    this.total = 0L;
    this.pageable = new Pageable();
  }

  public Page(List<T> content, long total, Pageable pageable)
  {
    this.content.addAll(content);
    this.total = total;
    this.pageable = pageable;
  }

  public int getPageNumber()
  {
    return this.pageable.getPageNumber();
  }

  public int getPageSize()
  {
    return this.pageable.getPageSize();
  }

  public String getSearchProperty()
  {
    return this.pageable.getSearchProperty();
  }

  public String getSearchValue()
  {
    return this.pageable.getSearchValue();
  }

  public String getOrderProperty()
  {
    return this.pageable.getOrderProperty();
  }

  public Order.Direction getOrderDirection()
  {
    return this.pageable.getOrderDirection();
  }

  public List<Order> getOrders()
  {
    return this.pageable.getOrders();
  }

  public List<Filter> getFilters()
  {
    return this.pageable.getFilters();
  }

  public int getTotalPages()
  {
    return (int)Math.ceil(getTotal() / getPageSize());
  }

  public List<T> getContent()
  {
    return this.content;
  }

  public long getTotal()
  {
    return this.total;
  }

  public Pageable getPageable()
  {
    return this.pageable;
  }
}