package com.dscomm.zookeeper2;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.dscomm.zookeeper.utils.NetworkUtil;
import com.dscomm.zookeeper.utils.PropertiesUtil;

public class ZookeeperRegister {
	
	private static Log logger = LogFactory.getLog(ZookeeperRegister.class);
	
	// 开发环境时，使用绝对路径
//	private static final String FILE_PATH = "G:/workspace/ds-zookeeper/ds-zookeeper/src/main/resources/zookeeper.properties";
	// 打包部署时，使用相对路径
	private static final String FILE_PATH = "./conf/zookeeper.properties";
	
	private String connectString;
	private int sessionTimeout = 2000;
	private String clientData;
	
	private ZooKeeper zk = null;
	// 父节点
	private String parentNodePath="/client";
	// 父节点下的子节点
	private String childNodePath;
	// 父节点下的子节点名称
	private String childNodeName = "client";
	
	private BusinessExecuter businessExecuter;
	
	public ZookeeperRegister(BusinessExecuter businessExecuter) {
		this.businessExecuter = businessExecuter;
		init();
	}
	
	/**
	 * 初始化
	 */
	private void init() {
		Properties properties = PropertiesUtil.getProperties(FILE_PATH);
		if (properties == null) {
			System.exit(-1);
		}
		
		String zookeeperIps = properties.getProperty("zookeeperIps");
		if (zookeeperIps == null || zookeeperIps.isEmpty()) {
			logger.error("zookeeperIps为空。");
			System.exit(-1);
		} else {
			this.connectString = zookeeperIps.trim();
		}
		
		String tempSessionTimeout = properties.getProperty("zookeeperSessionTimeout");
		if (tempSessionTimeout == null || tempSessionTimeout.isEmpty()) {
			logger.info("zookeeperSessionTimeout设置默认值为2000。");
		} else {
			this.sessionTimeout = Integer.parseInt(tempSessionTimeout.trim());
		}
		
		String tempCclientData = properties.getProperty("clientData");
		if (tempCclientData == null || tempCclientData.isEmpty()) {
			tempCclientData = NetworkUtil.getLocalHostIP();
			this.clientData = tempCclientData;
			
			logger.info("clientData设置默认值为本机IP-" + this.clientData);
		} else {
			this.clientData = tempCclientData.trim();
			logger.info("clientData设置为" + this.clientData);
		}
		
		String tempParentNodePath = properties.getProperty("parentNodePath");
		if (tempParentNodePath == null || tempParentNodePath.isEmpty()) {
			logger.info("parentNodePath设置默认值为" + this.parentNodePath);
		} else {
			this.parentNodePath = tempParentNodePath.trim();
			logger.info("parentNodePath设置为" + this.parentNodePath);
		}
		
		this.childNodePath = this.parentNodePath + "/" + this.childNodeName;
		logger.info("childNodePath设置为" + this.childNodePath);
	}

	/**
	 * 创建到zk的客户端连接
	 * @throws IOException
	 */
	public void connect() throws IOException {
		zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				// 监听父节点下子节点的变化
				if (KeeperState.SyncConnected == event.getState() && EventType.NodeChildrenChanged == event.getType()) {
					try {
						// 判断子节点是上线还是下线
						Stat stat = zk.exists(childNodePath, false);
						if (stat == null) { // 节点下线
							logger.info(childNodePath + "节点下线");
							
							createChildNode(); // 创建子节点
							
							startBusiness(); // 执行业务
						} else { // 节点上线
							logger.info(childNodePath + "节点上线");
							
							startBusiness(); // 执行业务
						}
					} catch (Exception e) {
						logger.error(String.format("判断%s节点是否存在失败", event.getPath()), e);
					}
					
					// 再次启动监听
					try {
						listenNode();
					} catch (Exception e) {
						logger.error("监听节点失败", e);
					}
				}
			}
		});
	}

	/**
	 * 监听节点
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void listenNode() throws KeeperException, InterruptedException {
		// 判断父节点是否存在
		Stat parentNodeStat = zk.exists(parentNodePath, false);
		if (parentNodeStat == null) {
			this.createParentNode();
		}
		
		// 监听父节点下子节点的变化
		zk.getChildren(parentNodePath, true);
		
		// 判断子节点是否存在
		Stat childNodeStat = zk.exists(childNodePath, false);
		if (childNodeStat == null) {
			createChildNode();
		}
	}
	
	/**
	 * 执行业务
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private void startBusiness() throws KeeperException, InterruptedException {
		byte[] data = zk.getData(childNodePath, false, null);
		if (clientData.equals(new String(data))) {
			// 是自己，开始执行业务
			new Thread() {
				@Override
				public void run() {
					if (businessExecuter == null) {
						logger.error("业务执行器为null，执行线程结束。");
						return;
					}
					
					logger.error("业务执行器开始执行...");
					businessExecuter.execute();
				}
			}.start();;
		}
	}
	
	/**
	 * 创建父节点（节点为持久节点）
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private void createParentNode() throws KeeperException, InterruptedException {
		String[] nodeNames = parentNodePath.substring(1).split("/");
		StringBuilder nameBuilder = new StringBuilder();
		for (String nodeName : nodeNames) {
			String path = nameBuilder.append("/").append(nodeName).toString();
			Stat nodeStat = zk.exists(path, false);
			if (nodeStat == null) {
				zk.create(path, nodeName.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				logger.info("持久节点[" + path + "]不存在，现在创建该节点");
			}
		}
	}
	
	/**
	 * 创建子节点（子节点为临时节点）
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private void createChildNode() throws KeeperException, InterruptedException {
		// 创建子节点
		zk.create(childNodePath, clientData.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		logger.info("临时节点[" + childNodePath + "]不存在，现在创建该节点");
	}

}
