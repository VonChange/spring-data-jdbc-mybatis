package com.vonchange.nine.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnumStatus {
    on("还在世"),off("已驾崩");
    private String desc;

}
