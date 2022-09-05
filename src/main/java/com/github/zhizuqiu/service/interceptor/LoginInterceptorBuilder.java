package com.github.zhizuqiu.service.interceptor;

import com.github.zhizuqiu.nettyrestful.server.interceptor.AbstractInterceptor;
import com.github.zhizuqiu.nettyrestful.server.interceptor.InterceptorBuilder;

import java.util.ArrayList;
import java.util.List;

public class LoginInterceptorBuilder implements InterceptorBuilder {
    @Override
    public List<AbstractInterceptor> build() {

        List<AbstractInterceptor> list = new ArrayList<>();

        list.add(new LoginInterceptor());

        return list;
    }
}
