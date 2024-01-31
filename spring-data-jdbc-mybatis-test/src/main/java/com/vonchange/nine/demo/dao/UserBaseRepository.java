package com.vonchange.nine.demo.dao;

import com.vonchange.jdbc.abstractjdbc.handler.AbstractPageWork;
import com.vonchange.jdbc.mybatis.core.query.BatchUpdate;
import com.vonchange.jdbc.mybatis.core.query.ReadDataSource;
import com.vonchange.jdbc.mybatis.core.support.BaseRepository;
import com.vonchange.nine.demo.domain.SearchParam;
import com.vonchange.nine.demo.domain.UserBaseDO;
import com.vonchange.nine.demo.domain.UserBaseVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


//@DataSourceKey("dataSourceRead")
public interface UserBaseRepository extends BaseRepository<UserBaseDO, Long> {
  List<UserBaseDO> findListBase(@Param("userName") String userName);
  UserBaseDO findOne(@Param("userName") String userName);
  @ReadDataSource
  List<UserBaseDO> findList(@Param("userName") String userName,
                            @Param("createTime") LocalDateTime createTime,@Param("isDelete")  Integer isDelete);
  Page<UserBaseDO> findList(Pageable pageable, @Param("userName") String userName,@Param("createTime") Date createTime);
  String findUserName(@Param("userName") String userName);
  List<UserBaseVO> findListVo(@Param("userName") String userName,
                              @Param("createTime") Date createTime);

  List<UserBaseDO> findListByBean(@Param("param") SearchParam searchParam);

  List<UserBaseDO> findListByIds(@Param("userName") String userName,
                           @Param("createTime") Date createTime,@Param("idList")List<Long> idList);

  int updateIsDelete(@Param("isDelete") Integer isDelete,@Param("id") Long id);

  @BatchUpdate
  int batchUpdate(List<UserBaseDO> list);

  @BatchUpdate(size = 5000)
  int batchInsert(List<UserBaseDO> list);

  List<Long> findLongList();

  int updateTest(@Param("list")List<UserBaseDO> list);

  List<UserBaseDO> findByUserName(@Param("test")String test);

  int insertBatchNormal(@Param("list")List<UserBaseDO> list);

  void findBigData(@Param("")AbstractPageWork<UserBaseDO> abstractPageWork,@Param("userName") String userName);

    List<UserBaseDO> findInList(@Param("userNames")List<String> test, @Param("isDeletes")List<Integer> isDeletes);
}