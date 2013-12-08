package com.hongqiang.shop.modules.entity;

import java.math.BigDecimal;
import java.util.HashSet;
//import java.util.Iterator;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
//import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
//import net.shophq.Setting;
//import net.shophq.util.SettingUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="hq_shipping_method")
public class ShippingMethod extends OrderEntity
{
  private static final long serialVersionUID = 5873163245980853245L;
  private String name;
  private Integer firstWeight;
  private Integer continueWeight;
  private BigDecimal firstPrice;
  private BigDecimal continuePrice;
  private String icon;
  private String description;
  private DeliveryCorp defaultDeliveryCorp;
  private Set<PaymentMethod> paymentMethods = new HashSet<PaymentMethod>();
  private Set<Order> orders = new HashSet<Order>();

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

  @NotNull
  @Min(0L)
  @Column(nullable=false)
  public Integer getFirstWeight()
  {
    return this.firstWeight;
  }

  public void setFirstWeight(Integer firstWeight)
  {
    this.firstWeight = firstWeight;
  }

  @NotNull
  @Min(1L)
  @Column(nullable=false)
  public Integer getContinueWeight()
  {
    return this.continueWeight;
  }

  public void setContinueWeight(Integer continueWeight)
  {
    this.continueWeight = continueWeight;
  }

  @NotNull
  @Min(0L)
  @Digits(integer=12, fraction=3)
  @Column(nullable=false, precision=21, scale=6)
  public BigDecimal getFirstPrice()
  {
    return this.firstPrice;
  }

  public void setFirstPrice(BigDecimal firstPrice)
  {
    this.firstPrice = firstPrice;
  }

  @NotNull
  @Min(0L)
  @Digits(integer=12, fraction=3)
  @Column(nullable=false, precision=21, scale=6)
  public BigDecimal getContinuePrice()
  {
    return this.continuePrice;
  }

  public void setContinuePrice(BigDecimal continuePrice)
  {
    this.continuePrice = continuePrice;
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

  @Lob
  public String getDescription()
  {
    return this.description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  @ManyToOne(fetch=FetchType.LAZY)
  public DeliveryCorp getDefaultDeliveryCorp()
  {
    return this.defaultDeliveryCorp;
  }

  public void setDefaultDeliveryCorp(DeliveryCorp defaultDeliveryCorp)
  {
    this.defaultDeliveryCorp = defaultDeliveryCorp;
  }

  @ManyToMany(mappedBy="shippingMethods", fetch=FetchType.LAZY)
  public Set<PaymentMethod> getPaymentMethods()
  {
    return this.paymentMethods;
  }

  public void setPaymentMethods(Set<PaymentMethod> paymentMethods)
  {
    this.paymentMethods = paymentMethods;
  }

  @OneToMany(mappedBy="shippingMethod", fetch=FetchType.LAZY)
  public Set<Order> getOrders()
  {
    return this.orders;
  }

  public void setOrders(Set<Order> orders)
  {
    this.orders = orders;
  }

//  @Transient
//  public BigDecimal calculateFreight(Integer weight)
//  {
//    Setting localSetting = SettingUtils.get();
//    BigDecimal localBigDecimal = new BigDecimal(0);
//    if (weight != null)
//      if ((weight.intValue() <= getFirstWeight().intValue()) || (getContinuePrice().compareTo(new BigDecimal(0)) == 0))
//      {
//        localBigDecimal = getFirstPrice();
//      }
//      else
//      {
//        double d = Math.ceil((weight.intValue() - getFirstWeight().intValue()) / getContinueWeight().intValue());
//        localBigDecimal = getFirstPrice().add(getContinuePrice().multiply(new BigDecimal(d)));
//      }
//    return localSetting.setScale(localBigDecimal);
//  }

  @PreRemove
  public void preRemove()
  {
//    Set localSet = getPaymentMethods();
//    Object localObject2;
//    if (localSet != null)
//    {
//      localObject2 = localSet.iterator();
//      while (((Iterator)localObject2).hasNext())
//      {
//        localObject1 = (PaymentMethod)((Iterator)localObject2).next();
//        ((PaymentMethod)localObject1).getShippingMethods().remove(this);
//      }
//    }
//    Object localObject1 = getOrders();
//    if (localObject1 != null)
//    {
//      Iterator localIterator = ((Set)localObject1).iterator();
//      while (localIterator.hasNext())
//      {
//        localObject2 = (Order)localIterator.next();
//        ((Order)localObject2).setShippingMethod(null);
//      }
//    }
  }
}