package com.vonchange.nine.demo.jpa;

import com.vonchange.mybatis.tpl.annotation.InsertIfNull;
import com.vonchange.mybatis.tpl.annotation.UpdateIfNull;
import com.vonchange.mybatis.tpl.annotation.UpdateNotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
@Entity
@Table(name = "user_base")
public class UserBase {

    //@GeneratedValue
    @Id
    private Long id;
    //@InsertIfNull(function = "REPLACE(UUID(), '-', '')")
    private String code;
    @Column(name="user_name")
    private String userName;
    private String  mobilePhone;
    //@InsertIfNull("0")
    @UpdateNotNull
    private Integer isDelete;
    @InsertIfNull(function = "now()")
    @UpdateNotNull
    private LocalDateTime createTime;
    //@UpdateDuplicateKeyIgnore
    @InsertIfNull(function = "now()")
    @UpdateIfNull(function = "now()")
    private Date updateTime;
    private byte[] headImageData;
    public UserBase(){

    }
    public UserBase(Long id, String userName, String mobilePhone, Integer isDelete, LocalDateTime createTime, Date updateTime){
        this.id=id;
        this.userName=userName;
        this.mobilePhone=mobilePhone;
        this.isDelete=isDelete;
        this.createTime=createTime;
        this.updateTime=updateTime;
    }





    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public byte[] getHeadImageData() {
        return headImageData;
    }

    public void setHeadImageData(byte[] headImageData) {
        this.headImageData = headImageData;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "UserBaseDO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", userName='" + userName + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", isDelete=" + isDelete +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
