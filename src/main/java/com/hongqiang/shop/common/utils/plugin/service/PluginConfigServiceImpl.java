package com.hongqiang.shop.common.utils.plugin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hongqiang.shop.common.service.BaseService;
import com.hongqiang.shop.common.utils.plugin.dao.PluginConfigDao;
import com.hongqiang.shop.website.entity.PluginConfig;

@Service
public class PluginConfigServiceImpl extends BaseService implements
		PluginConfigService {

	@Autowired
	private PluginConfigDao pluginConfigDao;

	@Transactional(readOnly = true)
	public boolean pluginIdExists(String pluginId) {
		return this.pluginConfigDao.pluginIdExists(pluginId);
	}

	@Transactional(readOnly = true)
	public PluginConfig findByPluginId(String pluginId) {
		return this.pluginConfigDao.findByPluginId(pluginId);
	}
}