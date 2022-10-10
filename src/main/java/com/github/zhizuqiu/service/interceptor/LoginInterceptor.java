package com.github.zhizuqiu.service.interceptor;

import com.github.zhizuqiu.model.User;
import com.github.zhizuqiu.nettyrestful.server.interceptor.AbstractInterceptor;
import com.github.zhizuqiu.nettyrestful.server.interceptor.InterceptorResponse;
import com.github.zhizuqiu.nettyrestful.server.tools.RequestParser;
import com.github.zhizuqiu.service.Base64Util;
import com.github.zhizuqiu.service.Tools;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;

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
                || url.endsWith("/login")
                || url.endsWith("/dispatch")
        ) {
            return true;
        }

        boolean ok = false;
        String auth = req.headers().get("Authorization");
        if ((auth != null) && (auth.length() > 6)) {
            auth = auth.substring(6);
            String decodedAuth = Base64Util.decode(auth);
            String[] auths = decodedAuth.split(":");
            User user = new User();
            if (auths.length > 1) {
                user.setUser(auths[0]);
                user.setPassword(auths[1]);
            }
            ok = Tools.assertPassword(user);
        }

        if (!ok) {
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, UNAUTHORIZED);
            HttpHeaders headers = res.headers();
            headers.set("WWW-authenticate", "Basic test=\"test\"");
            interceptorResponse.setResponse(res);
            return false;
        }
        return true;
    }


    @Override
    public void postHandle(FullHttpRequest fullHttpRequest) {

    }

}
