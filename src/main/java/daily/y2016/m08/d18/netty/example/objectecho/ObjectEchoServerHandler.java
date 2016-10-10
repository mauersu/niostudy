package daily.y2016.m08.d18.netty.example.objectecho;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class ObjectEchoServerHandler extends SimpleChannelUpstreamHandler {

	private static final Logger logger = Logger.getLogger(
			ObjectEchoServerHandler.class.getName());
	
	private final AtomicLong transferredMessages = new AtomicLong();
	
	public long getTransferredMessages() {
		return transferredMessages.get();
	}
	
	@Override
	public void handleUpstream(
			ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
		if(e instanceof ChannelStateEvent && 
				((ChannelStateEvent) e).getState() != ChannelState.INTEREST_OPS) {
			logger.info(e.toString());
		}
		super.handleUpstream(ctx, e);
	}
	
	@Override
	public void messageReceived(
			ChannelHandlerContext ctx, MessageEvent e) {
		transferredMessages.incrementAndGet();
		e.getChannel().write(e.getMessage());
	}
	
	@Override
	public void exceptionCaught(
			ChannelHandlerContext ctx, ExceptionEvent e) {
		logger.log(
				Level.WARNING,
				"Unexpected exception from downstream.",
				e.getCause());
		e.getChannel().close();
	}
}
