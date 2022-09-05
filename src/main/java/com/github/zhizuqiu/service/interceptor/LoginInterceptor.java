package com.github.zhizuqiu.service.interceptor;

import com.github.zhizuqiu.nettyrestful.server.interceptor.AbstractInterceptor;
import com.github.zhizuqiu.nettyrestful.server.interceptor.InterceptorResponse;
import com.github.zhizuqiu.nettyrestful.server.tools.RequestParser;
import com.github.zhizuqiu.service.Tools;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;

import java.util.Iterator;
import java.util.Set;

import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class LoginInterceptor extends AbstractInterceptor {


    @Override
    public boolean preHandle(FullHttpRequest req, InterceptorResponse interceptorResponse) {
        String url = RequestParser.getUrl(req.uri());

        // 如果是静态资源，或者是temp3.html使用的接口，以及登录接口不进行过滤，不过滤
        if (url.endsWith(".css")
                || url.endsWith(".js")
                || url.endsWith(".woff2")
                || url.endsWith(".map")
                || url.endsWith(".ico")
                || url.endsWith(".json")
                || url.endsWith("/login.html")
                || url.endsWith("/login")) {
            return true;
        }

        // 校验cookie
        Set<Cookie> cookies = RequestParser.getCookies(req);
        // 遍历
        Iterator<Cookie> i = cookies.iterator();
        boolean has = false;
        while (i.hasNext()) {
            Cookie cookie = i.next();
            if (Tools.LOGIN_COOKIE.equals(cookie.name())) {
                has = true;
                break;
            }
        }
        // 如果没有设置cookie，重定向到登录页
        if (!has) {
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
            HttpHeaders headers = res.headers();
            headers.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "x-requested-with,content-type");
            headers.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "POST,GET");
            headers.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            headers.set(HttpHeaderNames.CONTENT_LENGTH, res.content().readableBytes());
            headers.set(HttpHeaderNames.LOCATION, "/login.html");
            return false;
        }

        return true;
    }


    @Override
    public void postHandle(FullHttpRequest fullHttpRequest) {

    }

}
