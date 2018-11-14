package com.xxl.rpc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * get ip
 *
 * @author xuxueli 2016-5-22 11:38:05
 */
public class IpUtil {
    private static final Logger logger = LoggerFactory.getLogger(IpUtil.class);

    private static final String ANYHOST = "0.0.0.0";
    private static final String LOCALHOST = "127.0.0.1";
    public static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    private static volatile String LOCAL_ADDRESS = null;

    /**
     * valid address
     * @param address
     * @return boolean
     */
    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress() || address.isLinkLocalAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        return (name != null
                && ! ANYHOST.equals(name)
                && ! LOCALHOST.equals(name)
                && IP_PATTERN.matcher(name).matches());
    }

    /**
     * get first valid address
     *
     * @return InetAddress
     */
    private static InetAddress getFirstValidAddress() {
        // NetworkInterface address
        Enumeration<NetworkInterface> interfaces;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            logger.error("Failed to retriving ip address, " + e.getMessage(), e);
            return null;
        }

        InetAddress candidateAddress = null;
        while (interfaces.hasMoreElements()) {
            NetworkInterface network = interfaces.nextElement();
            Enumeration<InetAddress> addresses = network.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (isValidAddress(address)) {
                    // return site-local address immediately.
                    if (address.isSiteLocalAddress()) {
                        return address;
                    }
                    // get first candidate address.
                    if (candidateAddress == null) {
                        candidateAddress = address;
                    }
                }
            }
        }
        logger.error("Could not get local host ip address, will use 127.0.0.1 instead.");
        return candidateAddress;
    }


    /**
     * get address
     *
     * @return String
     */
    private static String getAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        InetAddress localAddress = getFirstValidAddress();
        LOCAL_ADDRESS = localAddress != null ? localAddress.getHostAddress() : null;
        return LOCAL_ADDRESS;
    }

    /**
     * get ip
     *
     * @return String
     */
    public static String getIp(){
        return getAddress();
    }

    /**
     * get ip:port
     *
     * @param port
     * @return String
     */
    public static String getIpPort(int port){
        String ip = getIp();
        return getIpPort(ip, port);
    }

    public static String getIpPort(String ip, int port){
        if (ip==null) {
            return null;
        }
        return ip.concat(":").concat(String.valueOf(port));
    }

    public static Object[] parseIpPort(String address){
        String[] array = address.split(":");

        String host = array[0];
        int port = Integer.parseInt(array[1]);

        return new Object[]{host, port};
    }


}
