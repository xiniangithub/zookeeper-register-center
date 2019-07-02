package com.dscomm.zookeeper2;

public class ZookeeperRegisterTest {

	public static void main(String[] args) throws Exception {
		BusinessExecuter executer = new BusinessExecuter() {
			@Override
			public void execute() {
				while (true) {
					System.out.println(1234);
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
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
