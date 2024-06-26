package com.vonchange.common.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("user_info")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserInfoDO {
    @Id
    private Long id;
    private String userCode;
    //@Column("user_name")
    private String userName;
    private String  mobileNo;
    private String address;
    private String order;
   // private boolean isValid;
   // private Integer status;
   // private byte[] headImageData;


}
