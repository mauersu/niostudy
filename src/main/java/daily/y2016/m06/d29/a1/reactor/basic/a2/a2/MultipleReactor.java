package daily.y2016.m06.d29.a1.reactor.basic.a2.a2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultipleReactor implements Runnable {

	public static void main(String[] args) throws IOException {
		MultipleReactor server = new MultipleReactor(8000);
		new Thread(server).start();
	}
	
	final Selector selector;
	final ServerSocketChannel serverSocket;
	
	final int DEFAULT_WORKER_SIZE = 10;
	Selector[] selectors;
	int next = 0;
	ExecutorService workerpool = Executors.newFixedThreadPool(DEFAULT_WORKER_SIZE);
	
	MultipleReactor(int port) throws IOException {
		selector = Selector.open();
		serverSocket = ServerSocketChannel.open();
		serverSocket.socket().bind(new InetSocketAddress(port));
		serverSocket.configureBlocking(false);
		SelectionKey sk = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
		sk.attach(new Acceptor());
		selectors = new Selector[DEFAULT_WORKER_SIZE];
		for(int i=0;i<DEFAULT_WORKER_SIZE;i++) {
			final Selector selector = Selector.open();
			selectors[i] = selector;
			workerpool.execute(new Runnable() {
				
				@Override
				public void run() {
					try {
						while(!Thread.interrupted()) {
							selector.select(500);
							Set selectedSet = selector.selectedKeys();
							Iterator it = selectedSet.iterator();
							while(it.hasNext())
								dispatch((SelectionKey)(it.next()));
							selectedSet.clear();
						}
					} catch(IOException e) {
						
					}
				}
			});
		}
	}
	
	public void run() {
		try {
			while(!Thread.interrupted()) {
				selector.select(500);
				Set selectedSet = selector.selectedKeys();
				Iterator it = selectedSet.iterator();
				while(it.hasNext()) 
					dispatch((SelectionKey)(it.next()));
				selectedSet.clear();
			}
		} catch(IOException e) {
			
		}
	}
	
	void dispatch(SelectionKey k) {
		Runnable r = (Runnable) (k.attachment());
		if(r!=null)
			r.run();
	}
	
	class Acceptor implements Runnable {
		public synchronized void run() {
			SocketChannel connection = null;
			try {
				connection = serverSocket.accept();
			} catch(IOException e) {
				
			}
			if(connection!=null) 
				try {
					new Handler(selectors[next], connection);
				} catch(IOException e) {
					
				}
			if(++next ==selectors.length) next =0;
		}
	}
}

class Handler implements Runnable {
	int MAXIN = 1024;
	int MAXOUT = 1024;
	
	final SocketChannel socket;
	final SelectionKey sk;
	ByteBuffer input = ByteBuffer.allocate(MAXIN);
	ByteBuffer output = ByteBuffer.allocate(MAXOUT);
	static final int READING = 0, SENDING = 1;
	int state = READING;
	
	Handler(Selector sel, SocketChannel c) throws IOException {
		socket = c;
		c.configureBlocking(false);
		sk = socket.register(sel, 0);
		sk.attach(this);
		sk.interestOps(SelectionKey.OP_READ);
		sel.wakeup();
	}
	
	public void run() {
		try {
			if(state ==READING)
				read();
			else if(state ==SENDING)
				send();
		} catch(IOException e) {
			
		}
	}
	
	void send() throws IOException {
		output = output.put("send()".getBytes());
		socket.write(output);
		if(outputIsComplete())
			sk.cancel();
	}
	
	static ExecutorService pool = Executors.newFixedThreadPool(10);
	static final int PROCESSING = 3;
	
	synchronized void read() throws IOException {
		socket.read(input);
		byte[] requestBytes = new byte[1024];
		input.flip();
		input.get(requestBytes, 0, input.limit());
		System.out.println(new String(requestBytes));
		if(inputIsComplete()){
			state = PROCESSING;
			pool.execute(new Processer());
		}
	}
	
	synchronized void processAndHandOff() {
		process();
		state = SENDING;
		sk.interestOps(SelectionKey.OP_WRITE);
	}
	
	class Processer implements Runnable {
		public void run() {
			processAndHandOff();
		}
	}
	
	boolean inputIsComplete() {
		return true;
	}
	
	boolean outputIsComplete() {
		return true;
	}
	
	void process() {
		
	}
}
