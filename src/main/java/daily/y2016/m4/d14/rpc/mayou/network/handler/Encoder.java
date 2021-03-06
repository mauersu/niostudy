package daily.y2016.m4.d14.rpc.mayou.network.handler;


import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;

public class Encoder extends SimpleChannelDownstreamHandler {

	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		
		byte[] data = (byte[])e.getMessage();
		
		ChannelBuffer buf = ChannelBuffers.buffer(data.length + 4);
		System.out.println(data.length);
		
		buf.writeInt(data.length);
		buf.writeBytes(data);
		
		Channels.write(ctx, e.getFuture(), buf);
	}
}
