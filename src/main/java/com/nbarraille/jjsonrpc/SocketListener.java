package com.nbarraille.jjsonrpc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketListener extends Thread {
	private final Logger _log = Logger.getLogger(this.getClass().getCanonicalName()); // The logger object.
	
	private final int _port;
	private final TcpServer _server;
	private final Object _handler;
	private ServerSocket _socket;
	private boolean running = false;
	
	public SocketListener(int port, TcpServer server, Object handler) {
		_port = port;
		_socket = null;
		_server = server;
		_handler = handler;
	}
	
	public void run() {
		try {
			_socket = new ServerSocket(_port);
		} catch (IOException e1) {
			_log.log(Level.SEVERE, "Could not start RPC server.");
		}

		running = true;
		
		while(running) {
			try {
				Socket connected = _socket.accept();
				JJsonPeer jp = new JJsonPeer(connected, _handler);
				_log.log(Level.INFO, "New client connected on port " + connected.getPort());
				_server.addPeer(jp);
				jp.start();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		running = false;
		this.interrupt();
		try {
			_socket.close();
		} catch(IOException e) {
			_log.log(Level.SEVERE, "Could not close RPC server socket.", e);
		}
	}

}
