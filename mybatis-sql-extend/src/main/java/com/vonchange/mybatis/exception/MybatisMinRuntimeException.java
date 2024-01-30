
package com.vonchange.mybatis.exception;


public class MybatisMinRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  protected EnumErrorCode code = EnumErrorCode.Error;

  public MybatisMinRuntimeException() {
    super();
  }

  public MybatisMinRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public MybatisMinRuntimeException(String message) {
    super(message);
  }

  public MybatisMinRuntimeException(String message, EnumErrorCode code) {
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
