package com.example.demo.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author linjian
 * @date 2018/11/01
 */
@Slf4j
public class HttpUtils {

    private static final int HTTP_CONNECTION_TIMEOUT = 60 * 1000;

    private static final int HTTP_READ_TIMEOUT = 60 * 1000 * 15;

    public static String sendGet(String url, String param) {
        return sendGet(url, param, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
    }

    public static String sendGet(String url, String param, int timeOut) {
        return sendGet(url, param, timeOut, timeOut);
    }

    public static String sendPost(String url, String param) {
        return sendPost(url, param, "application/json", "application/json");
    }

    public static String sendPost(String url, String param, String accept, String contentType) {
        return sendPost(url, param, accept, contentType, HTTP_CONNECTION_TIMEOUT,
                HTTP_READ_TIMEOUT);
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param, int connectionTimeOut, int readTimeOut) {
        log.debug("get url=" + url + ", param=" + param);
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        String urlNameString = null;
        try {
            urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setConnectTimeout(connectionTimeOut);
            connection.setReadTimeout(readTimeOut);
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            log.error("发送GET请求出现异常！url: " + urlNameString, e);
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                log.error("close exception", e2);
            }
        }
        return result.toString();
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param, String accept, String contentType, int connectionTimeOut, int readTimeOut) {
        log.debug("post url=" + url + ", param=" + param);
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", accept);
            conn.setRequestProperty("content-type", contentType);
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setConnectTimeout(connectionTimeOut);
            conn.setReadTimeout(readTimeOut);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            log.error("发送 POST 请求出现异常！url: " + url + ", " + e.getMessage());
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                log.error("IO exception", ex);
            }
        }
        log.debug("result:" + result);
        return result.toString();
    }
}