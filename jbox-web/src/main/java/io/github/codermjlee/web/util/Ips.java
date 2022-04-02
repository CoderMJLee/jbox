package io.github.codermjlee.web.util;

import javax.servlet.http.HttpServletRequest;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * IP操作
 *
 * @author MJ
 */
public class Ips {
    private static final String[] KEYS = {
        "x-forwarded-for", "Proxy-Client-IP",
        "WL-Proxy-Client-IP", "HTTP_CLIENT_IP",
        "HTTP_X_FORWARDED_FOR"
    };

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址。
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串
     */
    public static String getHttpClientIp() {
        return getHttpClientIp(Contexts.getRequest());
    }

    public static String getHttpClientIp(HttpServletRequest request) {
        if (request == null) return null;
        String ip;
        for (String key : KEYS) {
            ip = request.getHeader(key);
            if (ip != null && !"unknown".equalsIgnoreCase(ip)) return ip;
        }

        ip = request.getRemoteAddr();
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            try {
                // 根据网卡取本机配置的IP
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException ignored) {
            }
        }
        return ip;
    }

    /**
     * 获取本机本地IP
     */
    public static String getLocalIP() {
        try {
//            InetAddress ia = InetAddress.getLocalHost();
//            String localname = ia.getHostName();
//            String localip = ia.getHostAddress();
//            System.out.println("本机名称是：" + localname);
//            System.out.println("本机的ip是 ：" + localip);
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取到所有的在活动的网卡IP 包含虚拟网卡
     */
    public static List<String> getLocalIPList() {
        List<String> ipList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress;
            String ip;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address) { // IPV4
                        System.out.println(inetAddress.getHostName());
                        ip = inetAddress.getHostAddress();
                        System.out.println(ip);
                        ipList.add(ip);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipList;
    }

    /**
     * 获取本地真正的IP地址，即获得有线或者无线WiFi地址（真实物理网卡IP）。
     * 过滤虚拟机、蓝牙等地址
     */
    public static String getRealIP() {
        try {
            String[] names = {"intel", "mediatek", "realtek", "atheros", "broadcom"};
            //获取到所有的网卡
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                // 去除回环接口127.0.0.1，子接口，未运行的接口
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                }
                // 检查网卡名称
                String name = netInterface.getDisplayName().toLowerCase();
                boolean found = false;
                for (String s : names) {
                    if (name.contains(s)) {
                        found = true;
                        break;
                    }
                }
                if (!found) continue;

                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = addresses.nextElement();
                    if (ip != null) {
                        if (ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
                break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getNetworkSegment() {
        String ip = getRealIP();
        if (ip == null) return null;
        int idx = ip.lastIndexOf(".");
        if (idx == -1) return null;
        return ip.substring(0, idx);
    }
}
