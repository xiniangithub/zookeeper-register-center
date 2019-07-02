package com.dscomm.zookeeper1;

import java.util.List;

import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import com.dscomm.zookeeper1.ZookeeperJavaAPITest;

public class ZookeeperJavaAPITest {

	private ZookeeperJavaAPI zoo = new ZookeeperJavaAPI();
	
	/*
	 * 测试连接
	 */
	@Test
	public void testConnect() throws Exception {
		zoo.connect();
		zoo.close();
	}

	/*
	 * 测试创建节点
	 */
	@Test
	public void testCreate() throws Exception {
		String path = "/MyFirstZnode";

		byte[] data = "My first zookeeper app".getBytes();

		try {
			zoo.connect();
			zoo.create(path, data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zoo != null) {
				zoo.close();
			}
		}
	}

	/*
	 * 测试节点是否存在
	 */
	@Test
	public void testExists() throws Exception {
		String path = "/MyFirstZnode";

		try {
			zoo.connect();
			Stat stat = zoo.exists(path);

			if (stat != null) {
				System.out.println("Node exists and the node version is " + stat.getVersion());
			} else {
				System.out.println("Node does not exists");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zoo != null) {
				zoo.close();
			}
		}
	}

	/*
	 * 获取节点的子节点
	 */
	@Test
	public void testGetChildren() throws Exception {
		String path = "/MyFirstZnode";
		
		try {
			zoo.connect();
			Stat stat = zoo.exists(path);

			if (stat != null) {
				// 先创建两个子节点
				zoo.create(path+"/node1", "node1".getBytes());
				zoo.create(path+"/node2", "node2".getBytes());
				
				// 获取子节点
				List<String> children = zoo.getChildren(path);
				// 遍历每个子节点
				for (int i = 0; i < children.size(); i++) {
					System.out.println(children.get(i));
				}
			} else {
				System.out.println("Node does not exists");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (zoo != null) {
				zoo.close();
			}
		}
	}
	
	/*
	 * 测试获取节点数据
	 */
	@Test
	public void testGetData() throws Exception {
		String path = "/MyFirstZnode";

		try {
			zoo.connect();
			Stat stat = zoo.exists(path);

			if (stat != null) {
				byte[] data = zoo.getData(path);
				String str = new String(data, "UTF-8");
				System.out.println(str);
			} else {
				System.out.println("Node does not exists");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (zoo != null) {
				zoo.close();
			}
		}
	}
	
	/*
	 * 测试修改节点数据
	 */
	@Test
	public void testSetData() throws Exception {
		String path = "/MyFirstZnode";
		byte[] data = "Success".getBytes();

		try {
			zoo.connect();
			zoo.update(path, data);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (zoo != null) {
				zoo.close();
			}
		}
	}

	/*
	 * 测试删除节点
	 */
	@Test
	public void testDelete() throws Exception {
		String path = "/MyFirstZnode";

		try {
			zoo.connect();
			zoo.delete(path);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zoo != null) {
				zoo.close();
			}
		}
	}
	
	/*
	 * 测试递归删除节点
	 */
	@Test
	public void testRecurseDelete() throws Exception {
		String path = "/MyFirstZnode";
		
		try {
			zoo.connect();
			zoo.recurseDelete(path);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zoo != null) {
				zoo.close();
			}
		}
		
	}

}
