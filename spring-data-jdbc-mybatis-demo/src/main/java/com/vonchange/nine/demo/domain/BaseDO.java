package com.vonchange.nine.demo.domain;

import com.vonchange.mybatis.tpl.annotation.UpdateNotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ToString
public class BaseDO {
    @Id
    private Long id;
    @UpdateNotNull
    private Integer isDelete;
    @UpdateNotNull
    private LocalDateTime createTime;
    private Date updateTime;
}
