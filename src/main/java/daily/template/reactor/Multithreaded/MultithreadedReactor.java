package daily.template.reactor.Multithreaded;

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

import daily.template.reactor.ReactorPool.MultipleReactor;


//reactor 1: setup
public class MultithreadedReactor implements Runnable {
	
	public static void main(String args[] ) throws IOException {
		MultithreadedReactor server = new MultithreadedReactor(8000);
		new Thread(server).start();
	}
	
	final Selector selector;
	final ServerSocketChannel serverSocketChannel;
	
	MultithreadedReactor(int port) throws IOException {
		selector = Selector.open();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().bind(new InetSocketAddress(port));
		serverSocketChannel.configureBlocking(false);
		SelectionKey sk = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		sk.attach(new Acceptor());
	}
	
	/*
	 Alternatively, use explicit SPI provider:
	 	SelectorProvider p = SelectorProvider.provider();
	 	selector = p.openSelector();
	 	serverSocket = p.openServerSocketChannel();
	 */
	
//reactor 2: dispatch loop
	public void run() { //normally in a new Thread
		try {
			while(!Thread.interrupted()) {
				selector.select();
				Set selectedSet = selector.selectedKeys();
				Iterator it = selectedSet.iterator();
				while(it.hasNext()) 
					dispatch((SelectionKey)(it.next()));
				selectedSet.clear();
			}
		} catch(IOException ex) {
			
		}
	}
	
	void dispatch(SelectionKey k) {
		Runnable r = (Runnable) (k.attachment());
		if(r!=null) 
			r.run();
	}
//reactor 3: acceptor
	class Acceptor implements Runnable {//inner
		public void run() {
			try {
				SocketChannel c = serverSocketChannel.accept();
				if(c !=null) 
					new Handler(selector, c);
			} catch(IOException ex) {
				
			}
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
	Selector selector;
	
	Handler(Selector sel, SocketChannel c) throws IOException {
		socket = c;
		selector = sel;
		c.configureBlocking(false);
		//Optionally try first read now
		sk = socket.register(sel, 0);
		sk.attach(this);
		sk.interestOps(SelectionKey.OP_READ);
		sel.wakeup();
	}
	
	public void run() {
		try {
			if(state == READING) 
				read();
			else if(state == SENDING) 
				send();
		} catch(IOException ex) {
			
		}
	}
	
	void send() throws IOException {
		socket.write(output);
		if(outputIsComplete()) 
			sk.cancel();
	}
	
	//uses util.concurrent thread pool
	static ExecutorService pool = Executors.newFixedThreadPool(10);
	static final int PROCESSING = 3;
	
	synchronized void read() throws IOException {
		socket.read(input);
		if(inputIsComplete()) {
			state = PROCESSING;
			pool.execute(new Processer());
		}
	}
	
	synchronized void processAndHandOff() {
		process();
		state = SENDING;//or rebind attachment
		sk.interestOps(SelectionKey.OP_WRITE);
		selector.wakeup();
	}
	
	class Processer implements Runnable {
		public void run() {
			processAndHandOff();
		}
	}
	
	boolean inputIsComplete() {
		System.out.println("inputIsComplete()");
		return true;
	}
	
	boolean outputIsComplete() {
		System.out.println("outputIsComplete()");
		return true;
	}
	
	void process() {
		System.out.println("process()");
	}
}
