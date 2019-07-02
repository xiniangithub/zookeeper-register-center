package com.dscomm.zookeeper2;

/**
 * zookeeper注册中心
 * 功能：通过在zookeeper中注册服务端信息，监控节点变化，实现主备服务的自动切换。
 * @author Admin
 *
 */
public class MainApp {
	
	public static void main(String[] args) throws Exception {
		BusinessExecuter executer = new BusinessExecuter() {
			@Override
			public void execute() {
				// do something
				while (true) {
					System.out.println("execute business.");
				}
			}
		};
		
		ZookeeperRegister zookeeper = new ZookeeperRegister(executer);
		zookeeper.connect();
		zookeeper.listenNode();
		
		while (true) {
			Thread.sleep(3000);
		}
	}

}
