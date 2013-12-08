package com.hongqiang.shop.modules.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="hq_tag")
public class Tag extends OrderEntity
{
  private static final long serialVersionUID = -2735037966597250149L;
  private String name;
  private Type type;
  private String icon;
  private String memo;
//  private Set<Article> articles = new HashSet<Article>();
  private Set<Product> products = new HashSet<Product>();

  public enum Type
{
  article, product;
}
  
  @NotEmpty
  @Length(max=200)
  @Column(nullable=false)
  public String getName()
  {
    return this.name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  @NotNull(groups={BaseEntity.Save.class})
  @Column(nullable=false, updatable=false)
  public Type getType()
  {
    return this.type;
  }

  public void setType(Type type)
  {
    this.type = type;
  }

  @Length(max=200)
  public String getIcon()
  {
    return this.icon;
  }

  public void setIcon(String icon)
  {
    this.icon = icon;
  }

  @Length(max=200)
  public String getMemo()
  {
    return this.memo;
  }

  public void setMemo(String memo)
  {
    this.memo = memo;
  }

//  @ManyToMany(mappedBy="tags", fetch=FetchType.LAZY)
//  public Set<Article> getArticles()
//  {
//    return this.articles;
//  }
//
//  public void setArticles(Set<Article> articles)
//  {
//    this.articles = articles;
//  }

  @ManyToMany(mappedBy="tags", fetch=FetchType.LAZY)
  public Set<Product> getProducts()
  {
    return this.products;
  }

  public void setProducts(Set<Product> products)
  {
    this.products = products;
  }

  @PreRemove
  public void preRemove()
  {
//    Set localSet = getArticles();
//    Object localObject2;
//    if (localSet != null)
//    {
//      localObject2 = localSet.iterator();
//      while (((Iterator)localObject2).hasNext())
//      {
//        localObject1 = (Article)((Iterator)localObject2).next();
//        ((Article)localObject1).getTags().remove(this);
//      }
//    }
//    Object localObject1 = getProducts();
//    if (localObject1 != null)
//    {
//      Iterator localIterator = ((Set)localObject1).iterator();
//      while (localIterator.hasNext())
//      {
//        localObject2 = (Product)localIterator.next();
//        ((Product)localObject2).getTags().remove(this);
//      }
//    }
  }
}