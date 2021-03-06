package daily.y2016.m06.d24.rpc.example;

import daily.template.rpc.mayou.example.TestRpc;
import daily.y2016.m06.d24.rpc.proxy.ProxyFactory;

public class MayouRpcFactory {

	public static void main(String[] args) {
		for(int i=0;i<30;++i) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					byte[] data = new byte[5*1024];
					TestRpc testRpc = (TestRpc) ProxyFactory
							.getConsumerProxy(TestRpc.class, "127.0.0.1");
					
					while(true) {
						testRpc.test(data);
					}
				}
			});
			thread.start();
		}
	}
}
