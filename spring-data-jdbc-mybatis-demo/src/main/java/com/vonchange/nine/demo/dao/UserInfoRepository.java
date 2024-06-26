package com.vonchange.nine.demo.dao;

import com.vonchange.jdbc.mybatis.core.query.BatchUpdate;
import com.vonchange.jdbc.mybatis.core.support.CrudExtendRepository;
import com.vonchange.nine.demo.domain.SearchParam;
import com.vonchange.nine.demo.domain.UserInfoDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserInfoRepository extends CrudExtendRepository<UserInfoDO, Long> {

  List<UserInfoDO> findByUserCodes(@Param("userCodes") List<String> userCodes);
  // 根据用户代码查找用户信息
  UserInfoDO findByUserCode(@Param("userCode") String userCode);

  String findUserNameByCode(@Param("userCode") String userCode);

  List<UserInfoDO> findUserList(@Param("userCodes") List<String> userCodes,
                                @Param("userName")String userName,
                            @Param("createTime") LocalDateTime createTime);
  Page<UserInfoDO> findUserList(Pageable pageable,
                                    @Param("userCodes") List<String> userCodes,
                                    @Param("userName")String userName,
          @Param("createTime") LocalDateTime createTime);

  List<UserInfoDO> findUserBySearchParam(@Param("param") SearchParam searchParam);


  int updateIsDelete(@Param("isDelete") Integer isDelete,@Param("id") Long id);

  @BatchUpdate
  int batchUpdate(List<UserInfoDO> list);

}