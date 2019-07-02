package com.dscomm.zookeeper.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * NetworkHelper
 * 
 * @author LiBin
 */
public class NetworkUtil {

	/** 静态变量：系统日志 */
	private static final Log logger = LogFactory.getLog(NetworkUtil.class);
	
	/**
	 * 获取本机地址
	 * @return
	 */
	public static InetAddress getLocalHost() {
		InetAddress ip = null;
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
 
				// 去除回环接口，子接口，未运行和接口
				if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
					continue;
				}
				if (!netInterface.getDisplayName().contains("Intel")
						&& !netInterface.getDisplayName().contains("Realtek")
						&& !netInterface.getDisplayName().contains("ens")) {
					continue;
				}
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = addresses.nextElement();
					if (ip instanceof Inet4Address) {
						return ip;
					}
				}
				break;
			}
		} catch (SocketException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Failed to obtain native IP: " + e.getMessage(), e);
			}
		}
		return ip;
	}
	
	
	public static String getAllLocalHost() {
		String ips = "";
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
 
				// 去除回环接口，子接口，未运行和接口
				if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
					continue;
				}
				if (!netInterface.getDisplayName().contains("Intel")
						&& !netInterface.getDisplayName().contains("Realtek")
						&& !netInterface.getDisplayName().contains("ens")) {
					continue;
				}
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress ip=addresses.nextElement();
					if(ip!=null)
					ips =ips+"@"+ip.getHostAddress();
					
				}
				break;
			}
		} catch (SocketException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Failed to obtain native IP: " + e.getMessage(), e);
			}
		}
		return ips;
	}
	
	public static String getInterface() {
		String interfac = null;
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
 
				// 去除回环接口，子接口，未运行和接口
				if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
					continue;
				}
				if (!netInterface.getDisplayName().contains("Intel")
						&& !netInterface.getDisplayName().contains("Realtek")
						&& !netInterface.getDisplayName().contains("ens")) {
					continue;
				}
				interfac=netInterface.getName();
				logger.info(interfac);
				break;
			}
		} catch (SocketException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Failed to obtain native IP: " + e.getMessage(), e);
			}
		}
		return interfac;
	}
	
	/**
	 * 获取本机IP地址（字符串）
	 * @return
	 */
	public static String getLocalHostIP() {
		InetAddress my = getLocalHost();
		if (my != null) {
			return my.getHostAddress();
		}
		
		return "";
	}
	
	/**
	 * 获取本机IP地址（整数值）
	 * @return
	 */
	public static long getLocalHostAddress() {
		InetAddress my = getLocalHost();
		if (my != null) {
			byte[] addr = my.getAddress();
			if (addr != null && addr.length == 4) {
				long rawip1 = (addr[0] < 0 ? 256 + addr[0] : addr[0]) * 1000000000L;//(long)Math.pow(256, 3);
				long rawip2 = (addr[1] < 0 ? 256 + addr[1] : addr[1]) * 1000000L;//(long)Math.pow(256, 2);
				long rawip3 = (addr[2] < 0 ? 256 + addr[2] : addr[2]) * 1000L;//256;
				long rawip4 = addr[3] < 0 ? 256 + addr[3] : addr[3];
				
				return rawip1 + rawip2 + rawip3 + rawip4;
			}
		}
		
		return 0L;
	}
	
	
	public static void main(String[] args) {
		System.out.println(getLocalHostIP());
	}
	
}
