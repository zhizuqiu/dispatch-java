package com.github.zhizuqiu.service;

import com.github.zhizuqiu.App;
import com.github.zhizuqiu.model.Path;
import com.github.zhizuqiu.model.Result;
import com.github.zhizuqiu.nettyrestful.core.annotation.HttpHandler;
import com.github.zhizuqiu.nettyrestful.core.annotation.HttpMap;
import com.github.zhizuqiu.nettyrestful.server.bean.RestMethodKey;
import com.github.zhizuqiu.nettyrestful.server.store.MethodData;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@HttpHandler
public class RestHandler {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(RestHandler.class);

    @HttpMap(path = "/", returnType = HttpMap.ReturnType.TEXT_HTML)
    public String getMain() {
        RestMethodKey methodKey = new RestMethodKey("/index.html", HttpMap.Method.GET, HttpMap.ParamType.URL_DATA);
        return MethodData.getResourceAndTemplate(methodKey);
    }

    @HttpMap(path = "/upload", returnType = HttpMap.ReturnType.TEXT_HTML)
    public String upload() {
        RestMethodKey methodKey = new RestMethodKey("/upload.html", HttpMap.Method.GET, HttpMap.ParamType.URL_DATA);
        return MethodData.getResourceAndTemplate(methodKey);
    }

    @HttpMap(path = "/file/List")
    public List<Path> list(Map<String, String> mapParam, DefaultFullHttpResponse response) {
        if (mapParam == null || mapParam.isEmpty()) {
            return new ArrayList<>();
        }
        String path = mapParam.get("Path");

        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.endsWith("/")) {
            path = path + "/";
        }

        System.out.println("Path=" + path);

        String localPath = App.PATH + "/data" + path;

        File file = new File(localPath);
        if (file.isHidden() || !file.exists()) {
            return new ArrayList<>();
        }

        if (file.isDirectory()) {
            File[] list = file.listFiles();
            Integer index = 0;
            if (list != null) {
                List<Path> pathList = new ArrayList<>();
                for (File f : list) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(f.lastModified());
                    String time = sdf.format(cal.getTime());
                    if (f.isDirectory()) {
                        pathList.add(new Path(index, f.isDirectory(), time, f.length(), f.getName() + "/", path + f.getName() + "/"));

                    } else {
                        pathList.add(new Path(index, f.isDirectory(), time, f.length(), f.getName(), path + f.getName()));
                    }
                    index++;
                }
                Collections.sort(pathList);
                return pathList;
            }
        }
        return new ArrayList<>();
    }

    @HttpMap(path = "/getDanmuServer")
    public Map<String, String> getDanmuServer() {
        Map<String, String> result = new HashMap<>(2);
        result.put("danmuHost", App.DANMUHOST);
        result.put("danmuPort", App.DANMUPORT);
        return result;
    }

    @HttpMap(path = "/file/newDir",
            method = HttpMap.Method.PUT,
            paramType = HttpMap.ParamType.URL_DATA,
            returnType = HttpMap.ReturnType.APPLICATION_JSON
    )
    public Result newDir(Map<String, String> mapParam, DefaultFullHttpResponse response) {
        if (mapParam == null || mapParam.isEmpty()) {
            return new Result(500, "缺少参数");
        }
        String path = mapParam.get("Path");
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        System.out.println(path);

        String localPath = App.PATH + "/data" + path;

        File file = new File(localPath);
        boolean success = true;
        if (!file.exists()) {
            success = file.mkdir();
        }

        if (success) {
            return Result.success();
        }
        return new Result(500, "创建目录失败");
    }

    @HttpMap(path = "/file/Delete",
            method = HttpMap.Method.DELETE,
            paramType = HttpMap.ParamType.URL_DATA,
            returnType = HttpMap.ReturnType.APPLICATION_JSON
    )
    public Result delete(Map<String, String> mapParam, DefaultFullHttpResponse response) {
        if (mapParam == null || mapParam.isEmpty()) {
            return new Result(500, "缺少参数");
        }
        String path = mapParam.get("Path");
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        String isDirStr = mapParam.get("IsDir");
        boolean isDir = "true".equals(isDirStr);

        System.out.println(path);

        String localPath = App.PATH + "/data" + path;

        File file = new File(localPath);
        boolean success = true;
        if (file.exists()) {
            if (isDir) {
                success = deleteDir(file);
            } else {
                success = file.delete();
            }
        }

        if (success) {
            return Result.success();
        }
        return new Result(500, "删除失败");
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < Objects.requireNonNull(children).length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    @HttpMap(path = "/file/UploadFile",
            paramType = HttpMap.ParamType.MULTIPART_FORM_DATA,
            returnType = HttpMap.ReturnType.APPLICATION_JSON,
            method = HttpMap.Method.POST)
    public String upload(Map<String, String> mapParam, FileUpload[] fileUploads, DefaultFullHttpResponse response) {
        if (mapParam == null || mapParam.isEmpty()) {
            return "参数为空";
        }
        String path = mapParam.get("Path");
        if (path == null) {
            response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            return "Path参数为空";
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.endsWith("/")) {
            path = path + "/";
        }

        for (FileUpload fileUpload : fileUploads) {
            String filePath = App.PATH + "/data" + path + fileUpload.getFilename();
            System.out.println("filePath: " + filePath);
            File f = new File(filePath);

            if (f.exists()) {
                System.out.println("delete result: " + f.delete());
            }

            try {
                System.out.println("renameTo result: " + fileUpload.renameTo(f));
            } catch (IOException e) {
                e.printStackTrace();
                response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                return e.getMessage();
            }
        }

        return "上传成功";
    }

}
