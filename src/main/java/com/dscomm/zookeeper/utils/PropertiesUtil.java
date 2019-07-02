package com.dscomm.zookeeper.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertiesUtil {

	private static final Log logger = LogFactory.getLog(PropertiesUtil.class);
	
	public static Properties getProperties(String filePath) {
		Properties p = null;
		try {
//			InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(filePath);
			InputStream in = new FileInputStream(filePath);
			p = new Properties();
			p.load(in);
		} catch (Exception e) {
			p = null;
			if (logger.isErrorEnabled()) {
				logger.error("读取配置文件失败，文件路径：" + filePath, e);
			}
		}
		return p;
	}
	
}
