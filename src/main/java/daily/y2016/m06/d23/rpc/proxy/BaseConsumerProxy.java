package daily.y2016.m06.d23.rpc.proxy;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.channel.ChannelFuture;

import daily.y2016.m06.d23.rpc.api.RpcFactory;
import daily.y2016.m06.d23.rpc.client.ClientFactory;
import daily.y2016.m06.d23.rpc.common.Parameters;
import daily.y2016.m06.d23.rpc.common.Result;
import daily.y2016.m06.d23.rpc.serialize.SerializeException;
import daily.y2016.m06.d23.rpc.serialize.Serializer;

public class BaseConsumerProxy {

	private ThreadLocal<ChannelFuture> channelFutureLocal;
	
	public BaseConsumerProxy(final String remoteAddress) {
		channelFutureLocal = new ThreadLocal<ChannelFuture>() {
			
			@Override
			protected ChannelFuture initialValue() {
				return ClientFactory.getClient().getConnection(remoteAddress, 
						RpcFactory.DEFAULT_PORT);
			}
		};
	}
	
	protected Object doInterval(String interfaceName, Object[] objs)  {
		List<Class<?>> clazzs = new ArrayList<Class<?>>(objs.length);
		List<Object> params = new ArrayList<Object>();
		
		for(Object obj: objs) {
			clazzs.add(obj.getClass());
			params.add(obj);
		}
		
		Parameters parameters = new Parameters();
		parameters.setInterfaceName(interfaceName);
		parameters.setParameterTypes(clazzs);
		parameters.setParameters(params);
		
		while(!channelFutureLocal.get().getChannel().isConnected())
			;
		
		try {
			byte[] data = Serializer.serialize(parameters);
			
			channelFutureLocal.get().getChannel().write(data);
			synchronized(channelFutureLocal.get().getChannel()) {
				channelFutureLocal.get().getChannel().wait();
			}
			data = ClientFactory.getClient().getResult(
					channelFutureLocal.get().getChannel());
			Result result= Serializer.deserializer(data, Result.class);
			
			if(!result.isSuccess()) {
				System.out.println("error");
			}
			
			return result.getResult();
		} catch(SerializeException e) {
			return null;
		} catch(InterruptedException e) {
			return null;
		}
	}
}
