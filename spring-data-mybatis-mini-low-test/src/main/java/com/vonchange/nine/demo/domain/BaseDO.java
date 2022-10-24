package com.vonchange.nine.demo.domain;

import com.vonchange.mybatis.tpl.annotation.InsertIfNull;
import com.vonchange.mybatis.tpl.annotation.UpdateDuplicateKeyIgnore;
import com.vonchange.mybatis.tpl.annotation.UpdateIfNull;
import com.vonchange.mybatis.tpl.annotation.UpdateNotNull;

import javax.persistence.Id;
import java.util.Date;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "user_base")
public class BaseDO {
    @Id
    private Long id;
    @UpdateNotNull
    private Integer isDelete;
    @InsertIfNull(function = "now()")
    @UpdateNotNull
    private Date createTime;
    @UpdateDuplicateKeyIgnore
    @InsertIfNull(function = "now()")
    @UpdateIfNull(function = "now()")
    private Date updateTime;

    public BaseDO(){

    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
