package com.dms.apps.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dms.apps.user.domain.UserVO;

@Mapper
public interface UserMapper {

    List<UserVO> findUsers();
    UserVO findUserById(String id);

}
