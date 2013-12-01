package com.hongqiang.shop.modules.sys.utils.workflow;

/**
 * 工作流枚举
 * 
 * @author liuj
 *
 */
public enum ProcessDefinitionKey {
	/**
	 * 请假申请
	 */
	Leave("leave");

	private String key;

	private ProcessDefinitionKey(String key) {
		this.key = key;
	}

	/**
	 * 获取值
	 * 
	 * @return String
	 */
	public String getKey() {
		return this.key;
	}
}
