package com.vonchange.nine.demo.domain;




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

    //@GeneratedValue
    @Id
    private Long id;
    //@InsertIfNull(function = "REPLACE(UUID(), '-', '')")
    private String code;
    @Column(name="user_name")
    private String userName;
    private String  mobilePhone;
    //@InsertIfNull("0")
    //@UpdateNotNull
    //private Integer isDelete;
    @UpdateNotNull
    private Integer isDelete;

    @UpdateNotNull
    private LocalDateTime createTime;
    //@UpdateDuplicateKeyIgnore
    private Date updateTime;


    private EnumStatus status;

    private byte[] headImageData;
    public UserBaseDO(){

    }
    public UserBaseDO(Long id,String userName,String mobilePhone,Integer isDelete,LocalDateTime createTime,Date updateTime){
        this.id=id;
        this.userName=userName;
        this.mobilePhone=mobilePhone;
        this.createTime=createTime;
        this.updateTime=updateTime;
        this.isDelete=isDelete;
    }




}
