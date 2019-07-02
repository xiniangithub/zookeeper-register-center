package com.dscomm.zookeeper1;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperJavaAPI {

	private ZooKeeper zk;
	private final CountDownLatch connectedSignal = new CountDownLatch(1);

	/**
	 * 获取zookeeper连接
	 * @param host
	 * @return
	 * @throws Exception
	 */
	public ZooKeeper connect() throws Exception {
		zk = new ZooKeeper("192.168.6.251:2181", 5000, new Watcher() {
			public void process(WatchedEvent we) {
				if (we.getState() == KeeperState.SyncConnected) {
					connectedSignal.countDown();
				}
			}
		});

		connectedSignal.await();
		return zk;
	}

	/**
	 * 关闭zookeeper连接
	 * @throws InterruptedException
	 */
	public void close() throws InterruptedException {
		zk.close();
	}

	/**
	 * 创建节点
	 * @param path
	 * @param data
	 * @throws Exception
	 */
	public void create(String path, byte[] data) throws Exception {
		zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	/**
	 * 判断节点是否存在
	 * @param path
	 * @throws Exception
	 */
	public Stat exists(String path) throws Exception {
		return zk.exists(path, true);
	}
	
	/**
	 * 获取节点的子节点
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public List<String> getChildren(String path) throws Exception {
		return zk.getChildren(path, false);
	}

	/**
	 * 获取节点数据
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public byte[] getData(String path) throws Exception {
		return zk.getData(path, null, null);
	}
	
	/**
	 * 修改节点数据
	 * @param path
	 * @param data
	 * @throws Exception 
	 */
	public void update(String path, byte[] data) throws Exception {
		zk.setData(path, data, this.exists(path).getVersion());
	}
	
	/**
	 * 删除节点
	 * 注意：delete()不支持删除包含子节点的节点，当节点包含字节点时，需要遍历子节点一层层删除节点。
	 * @param path
	 * @throws Exception
	 */
	public void delete(String path) throws Exception {
		zk.delete(path, zk.exists(path, true).getVersion());
	}
	
	/**
	 * 递归删除节点及其子节点
	 * @param path
	 * @throws Exception
	 */
	public void recurseDelete(String path) throws Exception {
		Stat stat = zk.exists(path, false);
		if (stat != null) {
			List<String> childrens = zk.getChildren(path, null);
			if (childrens != null && !childrens.isEmpty()) {
				for (String children : childrens) {
					recurseDelete(path+"/"+children);
				}
			}
			
			zk.delete(path, stat.getVersion());
		}
	}

}
