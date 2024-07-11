package com.vonchange.nine.demo.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserInfoDTO {
    private Long id;
    private String userCode;
    private String aUserName;
    private String  mobileNo;
    private String address;
    private Boolean isValid;
   // private Integer status;
    private byte[] headImageData;
    private String order;


}
