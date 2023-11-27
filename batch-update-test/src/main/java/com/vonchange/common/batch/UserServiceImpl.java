package com.vonchange.common.batch;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,UserBaseDO> implements UserService {

    @Resource
    private UserBaseDao userBaseDao;


    @Override
    public int saveBatchCustom(List<UserBaseDO> list) {

        return  userBaseDao.insertBatch(list,10000);
    }
}
