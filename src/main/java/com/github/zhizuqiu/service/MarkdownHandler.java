package com.github.zhizuqiu.service;

import com.github.zhizuqiu.nettyrestful.server.handler.CustomStaticFileHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class MarkdownHandler implements CustomStaticFileHandler {

    @Override
    public FullHttpResponse customResponse(String uri, RandomAccessFile raf, FullHttpResponse response) {
        if (!uri.endsWith(".md")) {
            return null;
        }

        StringBuilder contentStrBuilder = new StringBuilder();
        try {
            String con;
            while ((con = raf.readLine()) != null) {
                contentStrBuilder.append(new String(con.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String contentStr = Tools.renderMarkdownToHtml(contentStrBuilder.toString());

        FullHttpResponse res = new DefaultFullHttpResponse(response.protocolVersion(), response.status());
        if (response.status() == OK) {
            ByteBuf content = Unpooled.copiedBuffer(contentStr, CharsetUtil.UTF_8);
            res = new DefaultFullHttpResponse(response.protocolVersion(), response.status(), content);
            res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            HttpUtil.setContentLength(res, content.readableBytes());
        }
        return res;
    }

}
