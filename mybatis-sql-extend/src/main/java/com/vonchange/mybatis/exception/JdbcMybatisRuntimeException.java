
package com.vonchange.mybatis.exception;


import com.vonchange.common.util.UtilAll;

public class JdbcMybatisRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  protected EnumErrorCode code = EnumErrorCode.Error;

  public JdbcMybatisRuntimeException() {
    super();
  }

  public JdbcMybatisRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
  public JdbcMybatisRuntimeException(EnumErrorCode code, String message, Object... parameters) {
    super(UtilAll.UString.format(message, parameters));
    this.code = code;
  }
  public JdbcMybatisRuntimeException(String message, Object... parameters) {
    super(UtilAll.UString.format(message, parameters));
  }
  public JdbcMybatisRuntimeException(String message) {
    super(message);
  }

  public JdbcMybatisRuntimeException(EnumErrorCode code, String message) {
    super(message);
    this.code = code;
  }
  public JdbcMybatisRuntimeException(EnumErrorCode code) {
    super(code.name());
    this.code = code;
  }

  public JdbcMybatisRuntimeException(Throwable cause) {
    super(cause);
  }


  public void setCode(EnumErrorCode code) {
    this.code = code;
  }


  public EnumErrorCode getCode() {
    return code;
  }

}
