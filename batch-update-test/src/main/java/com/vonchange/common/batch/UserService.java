package com.vonchange.common.batch;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserService extends IService<UserBaseDO> {
    int saveBatchCustom(List<UserBaseDO> list);
}
