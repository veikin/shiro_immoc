package com.imooc.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;

import javax.servlet.ServletRequest;
import java.io.Serializable;

/**
 * 自定义SessionManager.Shiro自带的SessionManager每次都对redis读取，效率较低.
 * 本实现则在第一次获取到session后存入request对象，后面通过request对象读取.
 *
 */
public class CustomSessionManager extends DefaultWebSessionManager {

    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {

        Serializable sessionId = getSessionId(sessionKey);
        ServletRequest request = null;

        if (sessionKey instanceof WebSessionKey) {
            request = ((WebSessionKey) sessionKey).getServletRequest();
        }
        if (request != null && sessionId != null) {
            Session session = (Session) request.getAttribute(sessionId.toString());
            if (session != null)
                return session;
        }

        Session session = super.retrieveSession(sessionKey);
        if (request != null && session!= null) {
            request.setAttribute(sessionId.toString(), session);
        }

        return session;
    }
}
