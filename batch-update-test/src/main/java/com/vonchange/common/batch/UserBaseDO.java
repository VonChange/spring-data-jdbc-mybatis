package com.vonchange.common.batch;


import com.vonchange.mybatis.tpl.annotation.UpdateNotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@Table(name = "user_base")
public class UserBaseDO {


    @Id
    private Long id;
    private String code;
    @Column(name="user_name")
    private String userName;
    private String  mobilePhone;
    private String descInfo;
    //@InsertIfNull("0")
    //@UpdateNotNull
    //private Integer isDelete;
    @UpdateNotNull
    private Integer isDelete;
    @UpdateNotNull
    private LocalDateTime createTime;

    private Date updateTime;


    private byte[] headImageData;
    public UserBaseDO(){

    }
    public UserBaseDO(Long id, String userName, String mobilePhone, Integer isDelete, LocalDateTime createTime, Date updateTime){
        this.id=id;
        this.userName=userName;
        this.mobilePhone=mobilePhone;
        this.createTime=createTime;
        this.updateTime=updateTime;
        this.isDelete=isDelete;
    }




}
