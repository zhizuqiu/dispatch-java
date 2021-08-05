package com.github.zhizuqiu.service.gotty;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProcessUtil {

    private static Logger logger = Logger.getLogger(ProcessUtil.class);

    /**
     * 执行linux主机命令
     */
    public static String execCommand(String cmd) throws InterruptedException {
        logger.info("start process linux command:" + cmd);
        String[] cmds = new String[]{"/bin/sh", "-c", cmd};
        Process pro = null;
        StringBuilder result = new StringBuilder();
        try {
            pro = Runtime.getRuntime().exec(cmds);
            InputStream error = pro.getErrorStream();
            BufferedReader errorRead = new BufferedReader(new InputStreamReader(error));
            String errorMsg;
            while ((errorMsg = errorRead.readLine()) != null) {
                logger.info("command errorStream :" + errorMsg);
            }
            InputStream input = pro.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = read.readLine()) != null) {
                result.append(line);
                logger.info("command inputStream :" + line);
            }
            pro.waitFor();
            errorRead.close();
            read.close();
        } catch (IOException e) {
            logger.error("exec linux command error!commond :" + cmd + " errorInfo:" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (pro != null) {
                pro.destroy();
            }
            logger.info("destroy process runtime ...");
        }
        logger.info("exec linux command:" + cmd + " success");
        return result.toString();
    }
}
