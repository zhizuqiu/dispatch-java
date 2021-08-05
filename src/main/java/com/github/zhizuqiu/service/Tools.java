package com.github.zhizuqiu.service;


import com.google.gson.*;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Tools {
    private static Pattern doublePattern = Pattern.compile("^[-\\+]?[.\\d]*$");
    private static Pattern intPattern = Pattern.compile("^[-\\+]?[\\d]*$");

    /**
     * 字符串是否为数字
     *
     * @param str 字符串
     * @return 是否
     */
    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String renderMarkdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    /**
     * 字符串是否为bool
     * 如果是'true'返回true，否则返回false
     * 如果str为null，返回null
     *
     * @param str 字符串
     * @return 是否
     */
    public static Boolean isBool(String str) {
        if (str == null) {
            return null;
        }
        return "true".equals(str);
    }

    /**
     * 将json转换为list
     *
     * @param json json串
     * @param t    list元素类型
     * @param <T>  泛型
     * @return list
     * @throws JsonSyntaxException josn异常
     */
    public static <T> List<T> getListFromJson(String json, Class<T> t) throws Exception {
        List<T> list = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();
        for (JsonElement jsonElement : jsonArray) {
            T bean = new Gson().fromJson(jsonElement, t);
            list.add(bean);
        }
        return list;
    }

    /**
     * 获取异常堆栈信息
     */
    public static String getErrorInfoFromException(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            sw.close();
            pw.close();
            return "\r\n" + sw.toString() + "\r\n";
        } catch (Exception e1) {
            return "";
        }
    }


    /**
     * 从文件中获取每一行
     */
    public static List<String> getFileLineList(String path) {
        List<String> list = new ArrayList<>();
        try {
            File file = new File(path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            while ((s = br.readLine()) != null) {
                if (!s.trim().isEmpty()) {
                    list.add(s.trim());
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取环境变量，并做相应的转换
     */
    public static Map<String, Object> getEnvs() {
        Map<String, Object> realEnvs = new HashMap<>();
        Map<String, String> envs = System.getenv();
        for (String key : envs.keySet()) {
            String value = envs.get(key);
            realEnvs.put(key, value);
        }
        return realEnvs;
    }


    public static Integer stringToInt(String str) {
        if (intPattern.matcher(str).matches()) {
            return Integer.valueOf(str);
        } else {
            return null;
        }
    }

    public static Long stringToLong(String str) {
        if (intPattern.matcher(str).matches()) {
            return Long.valueOf(str);
        } else {
            return null;
        }
    }

    /**
     * 字符串是否为正整数
     *
     * @param str 字符串
     * @return 是否
     */
    public static boolean isPositiveInteger(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
