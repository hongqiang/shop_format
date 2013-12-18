package com.hongqiang.shop.common.utils.plugin.service;

import com.hongqiang.shop.website.entity.PluginConfig;

public interface PluginConfigService {
	public boolean pluginIdExists(String pluginId);

	public PluginConfig findByPluginId(String pluginId);
}