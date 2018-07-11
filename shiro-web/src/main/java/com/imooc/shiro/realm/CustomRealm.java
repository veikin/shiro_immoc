package com.imooc.shiro.realm;

import com.imooc.dao.UserDao;
import com.imooc.domain.User;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.*;

public class CustomRealm extends AuthorizingRealm {

    @Autowired
    private UserDao userDao;

    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        String username = (String) principalCollection.getPrimaryPrincipal();

        // 从数据库或缓存中获取角色数据
        Set<String> roles = getRolesByUsername(username);

        Set<String> permissions = getPermissionsByUsername(username);

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setRoles(roles);
        simpleAuthorizationInfo.setStringPermissions(permissions);

        return simpleAuthorizationInfo;
    }

    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        // 1. 从主体传过来的认证信息中获得用户名
        String username = (String) authenticationToken.getPrincipal();
        
        // 2. 通过用户名到数据库中获取凭证
        String password = getPasswordByUsername(username);

        if (password == null) {
            return null;
        }

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                username, password, "customRealm");
        authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes("Mark"));
        return authenticationInfo;
    }

    /**
     * 模拟数据库
     * @param username
     * @return
     */
    private String getPasswordByUsername(String username) {
        User user = userDao.getUserByUserName(username);
        if (user != null) {
            return user.getPassword();
        }

        return null;
    }

    private Set<String> getRolesByUsername(String username) {
        List<String> list = userDao.queryRolesByUsername(username);
        return new HashSet<String>(list);
    }

    private Set<String> getPermissionsByUsername(String username) {
        List<String> list = userDao.queryPermissionsByUsername(username);
        return new HashSet<String>(list);
    }

    public static void main(String[] args) {
        Md5Hash md5Hash = new Md5Hash("123456", "Mark");
        System.out.println(md5Hash);
    }
}
