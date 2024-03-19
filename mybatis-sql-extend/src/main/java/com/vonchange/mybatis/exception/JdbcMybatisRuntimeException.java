
package com.vonchange.mybatis.exception;


import com.vonchange.common.util.exception.CommonRuntimeException;
import com.vonchange.common.util.exception.ErrorMsg;

public class JdbcMybatisRuntimeException extends CommonRuntimeException {

  private static final long serialVersionUID = 4638320203258180664L;
  public JdbcMybatisRuntimeException(EnumJdbcErrorCode errorCode) {
    super(errorCode);
  }
  public JdbcMybatisRuntimeException(EnumJdbcErrorCode errorCode, ErrorMsg message) {
    super(errorCode,message);
  }
  public EnumJdbcErrorCode getErrorCode() {
    return (EnumJdbcErrorCode) super.getErrorCode();
  }
}
