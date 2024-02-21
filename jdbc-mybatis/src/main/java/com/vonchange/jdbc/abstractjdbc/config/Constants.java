package com.vonchange.jdbc.abstractjdbc.config;


/**
 * 数据字典
 * @author song.chen
 *
 */
public class Constants {
	private Constants() { throw new IllegalStateException("Utility class");}
	/**
	 * mardown 配置里的信息
	 */
	public static  class  Markdown{
		public static final String TABLES = "tables";
		public static final String IDPREF = "--";
	}
	public  enum EnumRWType {
		read(0,"读"),write(1,"写");
		private Integer value;
		private String desc;
		EnumRWType(Integer value, String desc){
			this.value=value;
			this.desc=desc;
		}
		public static EnumRWType getValue(Integer value) {
			for (EnumRWType c : EnumRWType.values()) {
				if (c.getValue().equals(value)) {
					return c;
				}
			}
			return null;
		}
		public Integer getValue() {
			return value;
		}

		public EnumRWType setValue(int value) {
			this.value = value;
			return this;

		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
	}

}
