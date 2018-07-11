package com.imooc.dao;

import com.imooc.domain.User;

import java.util.List;

public interface UserDao {
    User getUserByUserName(String username);

    List<String> queryRolesByUsername(String username);

    List<String> queryPermissionsByUsername(String username);
}
