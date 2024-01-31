
package com.vonchange.mybatis.exception;


import com.vonchange.common.util.UtilAll;

public class MybatisMinRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  protected EnumErrorCode code = EnumErrorCode.Error;

  public MybatisMinRuntimeException() {
    super();
  }

  public MybatisMinRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
  public MybatisMinRuntimeException(EnumErrorCode code,String message, Object... parameters) {
    super(UtilAll.UString.format(message, parameters));
    this.code = code;
  }
  public MybatisMinRuntimeException(String message, Object... parameters) {
    super(UtilAll.UString.format(message, parameters));
  }
  public MybatisMinRuntimeException(String message) {
    super(message);
  }

  public MybatisMinRuntimeException(EnumErrorCode code,String message) {
    super(message);
    this.code = code;
  }
  public MybatisMinRuntimeException( EnumErrorCode code) {
    super(code.name());
    this.code = code;
  }

  public MybatisMinRuntimeException(Throwable cause) {
    super(cause);
  }


  public void setCode(EnumErrorCode code) {
    this.code = code;
  }


  public EnumErrorCode getCode() {
    return code;
  }

}
