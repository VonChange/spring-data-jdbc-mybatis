package com.vonchange.nine.demo.domain;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Table(name = "user_info")
@ToString
public class UserInfoDO extends BaseDO{
    @Id
    private Long id;
    private String userCode;
    @Column(name="user_name")
    private String userName;
    private String  mobileNo;
   // private Integer status;

    private byte[] headImageData;
    public UserInfoDO(){
    }

}
