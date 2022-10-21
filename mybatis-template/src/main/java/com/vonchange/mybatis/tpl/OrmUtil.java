package com.vonchange.mybatis.tpl;


import com.vonchange.mybatis.common.util.StringUtils;

/**
 * Orm field
 *
 */
public class OrmUtil {
	/**
	 * _ to up
	 *
	 */
	public static   String toFiled(String colName) {
		return  toUp(colName);
	}
	private static   String toUp(String colName){
		if(null==colName||!colName.contains("_")){
			return colName;
		}
		StringBuilder sb = new StringBuilder();
		boolean flag = false;
		for (int i = 0; i < colName.length(); i++) {
			char cur = colName.charAt(i);
			if (cur == '_') {
				flag = true;

			} else {
				if (flag) {
					sb.append(Character.toUpperCase(cur));
					flag = false;
				} else {
					sb.append(Character.toLowerCase(cur));
				}
			}
		}
		return sb.toString();
	}


	/**
	 * Hql to sql
	 *
	 */
	public static String toSql(String myHql) {
			StringBuilder sb = new StringBuilder();
			boolean flag = false;
			boolean isLetter = false;
			for (int i = 0; i < myHql.length(); i++) {
				char cur = myHql.charAt(i);
				if (cur == '_') {
					throw new RuntimeException("not allow  _ !");
				}
				if (cur == ':') {
					flag = true;
				}
				if (cur != ':' && !Character.isLetter(cur)) {
					flag = false;
				}
				if (flag) {
					sb.append(cur);
					continue;
				}
				if (Character.isUpperCase(cur) && isLetter) {
					sb.append("_");
					sb.append(Character.toLowerCase(cur));
				} else {
					sb.append(Character.toLowerCase(cur));
				}
				if (!Character.isLetter(cur)) {
					isLetter = false;
				} else {
					isLetter = true;
				}
			}
			return sb.toString();
	}

	/**
	 * toEntity
	 */
	public static String toEntity(String tableName) {
		return StringUtils.capitalize(toUp(tableName));
	}




}
