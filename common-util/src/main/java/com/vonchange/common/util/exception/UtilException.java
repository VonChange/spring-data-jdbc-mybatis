
package com.vonchange.common.util.exception;


public class UtilException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  protected EnumUtilErrorCode code = EnumUtilErrorCode.Error;

  public UtilException() {
    super();
  }

  public UtilException(String message, Throwable cause) {
    super(message, cause);
  }

  public UtilException(String message) {
    super(message);
  }

  public UtilException(EnumUtilErrorCode code,String message) {
    super(message);
    this.code = code;
  }
  public UtilException(EnumUtilErrorCode code) {
    super(code.name());
    this.code = code;
  }

  public UtilException(Throwable cause) {
    super(cause);
  }


  public void setCode(EnumUtilErrorCode code) {
    this.code = code;
  }


  public EnumUtilErrorCode getCode() {
    return code;
  }

}
