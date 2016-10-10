package daily.y2016.m09.d23;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class ResultHandler extends SimpleChannelUpstreamHandler {

	private final Client client;
	
	public ResultHandler(Client client) {
		this.client = client;
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		client.putResult(e.getChannel(), (byte[])e.getMessage());
	}
}
