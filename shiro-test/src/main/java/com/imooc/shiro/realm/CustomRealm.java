package com.imooc.shiro.realm;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomRealm extends AuthorizingRealm {

    Map<String, String> userMap = new HashMap<String, String>();

    {
        userMap.put("Mark", "283538989cef48f3d7d8a1c1bdf2008f");
    }

    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        String username = (String) principalCollection.getPrimaryPrincipal();

        // 从数据库或缓存中获取角色数据
        Set<String> roles = getRolesByUsername(username);

        Set<String> permissions = getPermissionsByRoles();

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

    private Set<String> getPermissionsByRoles() {
        Set<String> permissions = new HashSet<String>();
        permissions.add("user:add");
        permissions.add("user:delete");
        return permissions;
    }

    private Set<String> getRolesByUsername(String username) {
        Set<String> roles = new HashSet<String>();
        roles.add("admin");
        roles.add("user");
        return roles;
    }

    /**
     * 模拟数据库
     * @param username
     * @return
     */
    private String getPasswordByUsername(String username) {
        return userMap.get(username);
    }

    public static void main(String[] args) {
        Md5Hash md5Hash = new Md5Hash("123456", "Mark");
        System.out.println(md5Hash);
    }
}
