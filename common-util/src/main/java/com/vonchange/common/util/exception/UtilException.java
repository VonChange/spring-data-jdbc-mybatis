
package com.vonchange.common.util.exception;


public class UtilException extends CommonRuntimeException {
  private static final long serialVersionUID = -8919479339352899374L;

  public UtilException(EnumUtilErrorCode code) {
    super(code);
  }
  public UtilException(EnumUtilErrorCode code,ErrorMsg message) {
    super(code,message);
  }
  public EnumUtilErrorCode getErrorCode() {
    return (EnumUtilErrorCode) super.getErrorCode();
  }
}
