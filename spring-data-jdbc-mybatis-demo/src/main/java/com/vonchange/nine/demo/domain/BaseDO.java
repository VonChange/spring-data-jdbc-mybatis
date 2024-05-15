package com.vonchange.nine.demo.domain;


import com.vonchange.jdbc.annotation.InsertOnlyProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Version;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ToString
public class BaseDO {
    @Id
    private Long id;
    @InsertOnlyProperty
    private Integer isDelete;
    @InsertOnlyProperty
    private LocalDateTime createTime;
    @Version
    private Integer version;
    private Date updateTime;
}
