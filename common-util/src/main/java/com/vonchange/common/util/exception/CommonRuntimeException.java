
package com.vonchange.common.util.exception;


public class CommonRuntimeException extends RuntimeException {


  private static final long serialVersionUID = 1050921059666314224L;
  private final Object errorCode;

  public CommonRuntimeException(Object errorCode) {
    super(errorCode.toString());
    this.errorCode = errorCode;
  }

  public CommonRuntimeException(Object errorCode, ErrorMsg errorMsg) {
    super(errorCode.toString() + " " + errorMsg.getMessage(), errorMsg.getThrowable());
    this.errorCode = errorCode;
  }


  public Object getErrorCode() {
    return errorCode;
  }
}
