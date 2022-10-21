package com.vonchange.mybatis.tpl.exception;



/**
 *自定义RuntimeException
 * @author von_change@163.com
 */
public class MybatisMinRuntimeException extends NestedRuntimeException {

	public MybatisMinRuntimeException(String msg) {
		super(msg);
	}
	public static MybatisMinRuntimeException instance(Object... msg) {
		StringBuilder sb = new StringBuilder();
		for (Object object : msg) {
			sb.append(String.valueOf(object));
		}
	    String msgStr=sb.toString();
		return new MybatisMinRuntimeException(msgStr);
	}

	private static final long serialVersionUID = 1L;

}
