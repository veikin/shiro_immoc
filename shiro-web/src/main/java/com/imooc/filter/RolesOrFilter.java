package com.imooc.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;

public class RolesOrFilter extends AuthorizationFilter {

    protected boolean isAccessAllowed(ServletRequest servletRequest, javax.servlet.ServletResponse servletResponse, Object o) throws Exception {
        Subject subject = getSubject(servletRequest, servletResponse);
        String[] roles = (String[]) o;

        if (roles == null || roles.length == 0) {
            return true;
        }
        for (String role : roles) {
            // 只要有一个满足，就返回成功
            if (subject.hasRole(role)) {
                return true;
            }
        }

        return false;
    }
}
