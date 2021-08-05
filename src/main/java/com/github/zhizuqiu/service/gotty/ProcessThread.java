package com.github.zhizuqiu.service.gotty;

import org.apache.log4j.Logger;

public class ProcessThread extends Thread {
    private static Logger logger = Logger.getLogger(ProcessThread.class);

    private String command;

    public ProcessThread(String cmd) {
        super();
        this.command = cmd;
    }

    @Override
    public void run() {
        try {
            logger.info("start exec command:" + this.command);
            ProcessUtil.execCommand(this.command);
        } catch (Exception e) {
            logger.error("exec command:" + this.command + "error!");
            logger.error("Exception: " + e.getMessage());
        }
    }
}
