package com.hongqiang.shop.modules.account.service;

import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hongqiang.shop.common.service.BaseService;
import com.hongqiang.shop.common.utils.CookieUtils;
import com.hongqiang.shop.common.utils.Principal;
import com.hongqiang.shop.modules.account.dao.CartDao;
import com.hongqiang.shop.modules.account.dao.CartItemDao;
import com.hongqiang.shop.modules.entity.Cart;
import com.hongqiang.shop.modules.entity.CartItem;
import com.hongqiang.shop.modules.entity.Member;
import com.hongqiang.shop.modules.entity.Product;
import com.hongqiang.shop.modules.user.dao.MemberDao;

@Service
public class CartServiceImpl extends BaseService implements CartService {

	@Autowired
	private CartDao cartDao;

	@Autowired
	private CartItemDao cartItemDao;

	@Autowired
	private MemberDao memberDao;

	public Cart getCurrent() {
		RequestAttributes localRequestAttributes = RequestContextHolder
				.currentRequestAttributes();
		if (localRequestAttributes != null) {
			HttpServletRequest localHttpServletRequest = ((ServletRequestAttributes) localRequestAttributes)
					.getRequest();
			Principal localPrincipal = (Principal) localHttpServletRequest
					.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
			Member localObject1 = localPrincipal != null ? (Member) this.memberDao
					.find(localPrincipal.getId()) : null;
			if (localObject1 != null) {
				Cart localObject2 = localObject1.getCart();
				if (localObject2 != null) {
					if (!((Cart) localObject2).hasExpired()) {
						if (!DateUtils.isSameDay(
								((Cart) localObject2).getUpdateDate(),
								new Date())) {
							((Cart) localObject2).setUpdateDate(new Date());
							this.cartDao.merge(localObject2);
						}
						return localObject2;
					}
					this.cartDao.remove(localObject2);
				}
			} else {
				String localObject2 = CookieUtils.getCookie(
						localHttpServletRequest, "cartId");
				String str = CookieUtils.getCookie(localHttpServletRequest,
						"cartKey");
				if ((StringUtils.isNotEmpty((String) localObject2))
						&& (StringUtils.isNumeric((String) localObject2))
						&& (StringUtils.isNotEmpty(str))) {
					Cart localCart = (Cart) this.cartDao.find(Long
							.valueOf((String) localObject2));
					if ((localCart != null) && (localCart.getMember() == null)
							&& (StringUtils.equals(localCart.getKey(), str))) {
						if (!localCart.hasExpired()) {
							if (!DateUtils.isSameDay(localCart.getUpdateDate(),
									new Date())) {
								localCart.setUpdateDate(new Date());
								this.cartDao.merge(localCart);
							}
							return localCart;
						}
						this.cartDao.remove(localCart);
					}
				}
			}
		}
		return (Cart) null;
	}

	public void merge(Member member, Cart cart) {
		if ((member != null) && (cart != null) && (cart.getMember() == null)) {
			Cart localCart = member.getCart();
			if (localCart != null) {
				Iterator<CartItem> localIterator = cart.getCartItems()
						.iterator();
				while (localIterator.hasNext()) {
					CartItem localCartItem1 = (CartItem) localIterator.next();
					Product localProduct = localCartItem1.getProduct();
					if (localCart.contains(localProduct)) {
						if ((Cart.MAX_PRODUCT_COUNT != null)
								&& (localCart.getCartItems().size() > Cart.MAX_PRODUCT_COUNT
										.intValue()))
							continue;
						CartItem localCartItem2 = localCart
								.getCartItem(localProduct);
						localCartItem2.add(localCartItem1.getQuantity()
								.intValue());
						this.cartItemDao.merge(localCartItem2);
					} else {
						if ((Cart.MAX_PRODUCT_COUNT != null)
								&& (localCart.getCartItems().size() >= Cart.MAX_PRODUCT_COUNT
										.intValue()))
							continue;
						localIterator.remove();
						localCartItem1.setCart(localCart);
						localCart.getCartItems().add(localCartItem1);
						this.cartItemDao.merge(localCartItem1);
					}
				}
				this.cartDao.remove(cart);
			} else {
				member.setCart(cart);
				cart.setMember(member);
				this.cartDao.merge(cart);
			}
		}
	}

	public void evictExpired() {
		this.cartDao.evictExpired();
	}

	@Transactional
	public void save(Cart cart) {
		this.cartDao.persist(cart);
	}

	@Transactional
	public Cart update(Cart cart) {
		return (Cart) this.cartDao.merge(cart);
	}

	@Transactional
	public Cart update(Cart cart, String[] ignoreProperties) {
		return (Cart) this.cartDao.update(cart, ignoreProperties);
	}

	@Transactional
	public void delete(Long id) {

		this.cartDao.delete(id);
	}

	@Transactional
	public void delete(Long[] ids) {
		if (ids != null)
			for (Long id : ids)
				this.cartDao.delete(id);
	}

	@Transactional
	public void delete(Cart cart) {
		this.cartDao.delete(cart);
	}
}