package com.vonchange.nine.demo.domain;

import com.vonchange.mybatis.tpl.annotation.UpdateNotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class BaseDO {
    @Id
    private Long id;
    @UpdateNotNull
    private Integer isDelete;
    @UpdateNotNull
    private Date createTime;
    private Date updateTime;
}
