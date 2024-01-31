package com.vonchange.nine.demo.dao;

import com.vonchange.jdbc.abstractjdbc.handler.AbstractPageWork;
import com.vonchange.jdbc.mybatis.core.query.BatchUpdate;
import com.vonchange.jdbc.mybatis.core.query.ReadDataSource;
import com.vonchange.jdbc.mybatis.core.support.CrudRepository;
import com.vonchange.nine.demo.domain.SearchParam;
import com.vonchange.nine.demo.domain.UserInfoDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


public interface UserInfoRepository extends CrudRepository<UserInfoDO, Long> {
  List<UserInfoDO> findListByUserCode(@Param("userCode") String userCode);
  UserInfoDO findOneByUserCode(@Param("userCode") String userCode);
  String findUserNameByCode(@Param("userCode") String userCode);
  @ReadDataSource
  List<UserInfoDO> findUserList(@Param("userName") String userName,
                            @Param("createTime") LocalDateTime createTime,@Param("isDelete")  Integer isDelete);
  Page<UserInfoDO> findUserList(Pageable pageable, @Param("userName") String userName,@Param("createTime") LocalDateTime createTime,@Param("isDelete")  Integer isDelete);

  List<UserInfoDO> findUserBySearchParam(@Param("param") SearchParam searchParam);

  List<UserInfoDO> findListByIds(@Param("userName") String userName,
                           @Param("createTime") Date createTime,@Param("idList")List<Long> idList);

  int updateIsDelete(@Param("isDelete") Integer isDelete,@Param("id") Long id);

  @BatchUpdate
  int batchUpdate(List<UserInfoDO> list);

  @BatchUpdate(size = 5000)
  int batchInsert(List<UserInfoDO> list);

  List<Long> findLongList();

  int updateTest(@Param("list")List<UserInfoDO> list);

  List<UserInfoDO> findByUserName(@Param("test")String test);

  int insertBatchNormal(@Param("list")List<UserInfoDO> list);

  void findBigData(@Param("")AbstractPageWork<UserInfoDO> abstractPageWork,@Param("userName") String userName);

    List<UserInfoDO> findByUserCodes(@Param("userCodes")List<String> userCodes);
}