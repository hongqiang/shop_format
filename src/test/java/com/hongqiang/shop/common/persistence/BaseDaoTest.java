package com.hongqiang.shop.common.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hongqiang.shop.common.test.SpringTransactionalContextTests;
import com.hongqiang.shop.common.utils.Filter;
import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.modules.sys.dao.UserDao;
import com.hongqiang.shop.modules.sys.entity.User;

/**
 * BaseDaoTest
 * @author ThinkGem
 * @version 2013-05-15
 */
public class BaseDaoTest extends SpringTransactionalContextTests {
	
	@Autowired
	private UserDao userDao;

	@Test
	public void find(){
		System.out.println("hehe");
	}
	
}