package com.vonchange.nine.demo.domain;

import com.vonchange.mybatis.tpl.annotation.UpdateNotNull;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
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
    @UpdateNotNull
    private Integer isDelete;
    @UpdateNotNull
    private LocalDateTime createTime;
    private Date updateTime;
    private EnumDelete enumDelete;
    private byte[] headImageData;
    public UserBaseDO(){

    }
    public UserBaseDO(Long id,String userName,String mobilePhone,Integer isDelete,LocalDateTime createTime,Date updateTime){
        this.id=id;
        this.userName=userName;
        this.mobilePhone=mobilePhone;
        this.isDelete=isDelete;
        this.createTime=createTime;
        this.updateTime=updateTime;
    }

    public EnumDelete getEnumDelete() {
        return enumDelete;
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
        this.enumDelete=EnumDelete.getValue(isDelete);
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

    public void setEnumDelete(EnumDelete enumDelete) {
        this.enumDelete = enumDelete;
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
                ", enumDelete=" + enumDelete +
                '}';
    }
}
