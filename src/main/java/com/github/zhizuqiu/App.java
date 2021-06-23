package com.github.zhizuqiu;

import com.github.zhizuqiu.nettyrestful.server.NettyRestServer;
import com.github.zhizuqiu.service.MarkdownHandler;
import com.github.zhizuqiu.service.Tools;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class App {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(App.class);

    private static int PORT = 80;
    private static int BOSSTHREADCOUNT = 2;
    private static int WORKTHREADCOUNT = 4;
    public static String PATH = "resources";
    private static boolean ENABLEMARKDOWNTOHTML = false;
    public static String DANMUHOST = "localhost";
    public static String DANMUPORT = "9090";


    public static void main(String[] args) {
        String portStr = System.getenv("DP_PORT");
        if (portStr != null && Tools.isNumeric(portStr)) {
            PORT = Integer.parseInt(portStr);
        }

        String bossThreadCountStr = System.getenv("DP_BOSSTHREADCOUNT");
        if (bossThreadCountStr != null && Tools.isNumeric(bossThreadCountStr)) {
            BOSSTHREADCOUNT = Integer.parseInt(bossThreadCountStr);
        }

        String workThreadCountStr = System.getenv("DP_WORKTHREADCOUNT");
        if (workThreadCountStr != null && Tools.isNumeric(workThreadCountStr)) {
            WORKTHREADCOUNT = Integer.parseInt(workThreadCountStr);
        }

        String path = System.getenv("DP_PATH");
        if (path != null) {
            PATH = path;
        }

        String enableMarkdownToHtml = System.getenv("DP_ENABLE_MARKDOWN_TO_HTML");
        if (enableMarkdownToHtml != null) {
            ENABLEMARKDOWNTOHTML = Tools.isBool(enableMarkdownToHtml);
        }

        String danmuHost = System.getenv("DP_DANMUHOST");
        if (danmuHost != null) {
            DANMUHOST = danmuHost;
        }

        String danmuPort = System.getenv("DP_DANMUPORT");
        if (danmuPort != null) {
            DANMUPORT = danmuPort;
        }

        NettyRestServer.NettyRestServerBuilder nettyRestServerBuilder = new NettyRestServer.NettyRestServerBuilder()
                .setSsl(false)
                .setPort(PORT)
                .setStaticFilePath(PATH)
                .setPackages("com.github.zhizuqiu.service")
                .setBossThreadCount(BOSSTHREADCOUNT)
                .setWorkThreadCount(WORKTHREADCOUNT)
                .setEnableUpload(true)
                .setRestCallback((bossGroup, workerGroup) -> LOGGER.info("callback"));

        if (ENABLEMARKDOWNTOHTML) {
            nettyRestServerBuilder.setStaticFileHandler(new MarkdownHandler());
        }

        NettyRestServer nettyRestServer = nettyRestServerBuilder.build();

        try {
            nettyRestServer.run();
        } catch (Exception e) {
            nettyRestServer.stop();
            LOGGER.error("NettyRestServer Exception:" + e.getMessage());
        }
    }

}
