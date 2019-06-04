package com.zyy.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 14:58 2019/5/31
 */
public final class IPUtil {
    /**
     * 获取目标主机的ip
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.contains(",")) {
            return ip.split(",")[0];
        } else {
            return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
        }
    }


}
