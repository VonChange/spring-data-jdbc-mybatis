package com.vonchange.common.util.exception;

import com.vonchange.common.util.UtilAll;

public class ErrorMsg{
     private String message;
     private  Throwable throwable;


    public static ErrorMsg builder(){
         return new ErrorMsg();
    }
    public  ErrorMsg message(String message) {
      this.message =message;
      return this;
    }
    public  ErrorMsg message(String message, Object... parameters) {
         this.message = UtilAll.UString.format(message,parameters);
      return this;
    }
    public  ErrorMsg throwable( Throwable throwable) {
      this.throwable =throwable;
      return this;
    }


    public String getMessage() {
      return message;
    }


    public Throwable getThrowable() {
      return throwable;
    }
  }