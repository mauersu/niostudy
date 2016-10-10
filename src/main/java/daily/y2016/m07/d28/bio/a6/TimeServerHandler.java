package daily.y2016.m07.d28.bio.a6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TimeServerHandler implements Runnable {

	private Socket socket;
	
	public TimeServerHandler(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			out = new PrintWriter(this.socket.getOutputStream(), true);
			String currentTime = null;
			String body = null;
			while(true) {
				body = in.readLine();
				if(body ==null) {
					continue;
				}
				out.println("this is server");
			}
		} catch(IOException e) {
			
		} finally {
			if(in !=null) {
				try {
					in.close();
				} catch(IOException e) {
					
				}
			}
			if(out!=null) {
				out.close();
				out = null;
			}
			if(this.socket!=null) {
				try {
					this.socket.close();
				} catch(IOException e) {
					
				}
				this.socket = null;
			}
		}
	}
}