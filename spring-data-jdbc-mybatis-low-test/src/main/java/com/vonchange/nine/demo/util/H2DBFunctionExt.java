package com.vonchange.nine.demo.util;

import java.util.UUID;

public class H2DBFunctionExt {
	/**
	 * 用法：SELECT uuid(); H2数据库注册uuid函数：CREATE ALIAS uuid FOR
	 * "h2db.function.ext.H2DBFunctionExt.uuid";
	 *
	 * @return
	 * @Method: uuid
	 * @Description: 实现MySQL数据库的uuid函数，用于生成UUID
	 */
	public static String uuid() {
		return UUID.randomUUID().toString();
	}
}
