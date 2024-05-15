package com.vonchange.nine.demo.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserExample {
    private String userNameLike;
    private List<String> userCodeIn;

    private Boolean createTimeDesc;
}
