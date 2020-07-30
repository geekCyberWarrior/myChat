package com.sanjith.myChat.Server;

import java.net.InetAddress;

public class ServerClient {
	private String name;
	private InetAddress address;
	private int port;
	private final int ID;
	private int attempt;
	
	public ServerClient(String name, InetAddress address, int port, final int iD) {
		this.name = name;
		this.address = address;
		this.port = port;
		ID = iD;
		attempt = 0;
	}

	public int getID() {
		return ID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getAttempt() {
		return attempt;
	}

	public void setAttempt(int attempt) {
		this.attempt = attempt;
	}
	
	public void incAttempt() {
		this.attempt++;
	}
}
