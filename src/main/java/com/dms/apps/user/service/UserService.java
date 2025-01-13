package com.dms.apps.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dms.apps.user.domain.UserVO;
import com.dms.apps.user.mapper.UserMapper;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public List<UserVO> getUserList() {
        return userMapper.findUsers();
    }

    public UserVO getUser(String id) {
        return userMapper.findUserById(id);
    }
}

