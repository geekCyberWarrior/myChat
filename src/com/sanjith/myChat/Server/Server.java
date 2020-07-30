package com.sanjith.myChat.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Server implements Runnable {
	private List<ServerClient> clients;
	private List<Integer> attempts;
	
	private DatagramSocket socket;
	private int port;
	private Thread run, manage, send, recieve;
	private boolean running = true;
	
	private final int MAX_ATTEMPTS = 5;
	
	public Server(int port) {
		attempts = new ArrayList<>();
		clients = new ArrayList<>();
		this.port = port;	
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return ;
		}
		run = new Thread(this,"Server");
		run.start();
	}

	@Override
	public void run() {
		running = true;
		System.out.println("Server Started on Port " + port);
		manageClients();
		recieve();
		Scanner in = new Scanner(System.in);
		while(running) {
			String text = in.nextLine();
			if(text.startsWith("/")) {
				sendToAll("Server: " + text.substring(1));
			}
		}
	}
	
	private void manageClients() {
			manage = new Thread("Manage" ) {
				public void run() {
					while(running) {
						sendToAll("/i/");
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						for(int i=0; i<clients.size(); i++) {
							ServerClient c = clients.get(i);
							if(!attempts.contains(c.getID())) {
//								System.out.println(c.getAttempt());
								if(c.getAttempt()>=MAX_ATTEMPTS) {
									disconnect(c.getID(), false);
								} else {
									c.incAttempt();
								}
							} else {
								attempts.remove(i);
								c.setAttempt(0);
								//System.out.println("Client " + c.getID() + " Active!!");
							}
						}
					}
				}
			};
			manage.start();
	}
	
	private void recieve() {
		recieve = new Thread("Recieve") {
			public void run() {
				while(running) {
					//System.out.println(clients.size());
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
					process(packet);
					
//					clients.add(new ServerClient("Sanjith", packet.getAddress(), packet.getPort(), 50));
//					System.out.println(clients.size());
//					System.out.println(clients.get(0).getAddress().toString() + " : " + clients.get(0).getPort());
				}
			}
		};
		recieve.start();
	}
	
	private void send(final byte[] data, InetAddress address, int port) {
		send = new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	
	private void sendToAll(String message) {
		for(int i=0; i<clients.size();i++) {
			ServerClient client = clients.get(i);
			send(message.getBytes(), client.getAddress(), client.getPort());
		}
	}
	
	private void process(DatagramPacket packet) {
		String string = new String(packet.getData()).trim();
		if(string.startsWith("/c/")) {
			//UUID id = UUID.randomUUID();
			//System.out.println(id.toString());
			ServerClient c = new ServerClient(string.substring(3), packet.getAddress(), packet.getPort(), UniqueIdentifier.getIdentifier());
			clients.add(c);
//			System.out.println("Identifier: " + c.getID());
//			System.out.println("Name: " + c.getName());
//			System.out.println(c.getAddress() + ":" + c.getPort());
			String con ="/c/" + Integer.toString(c.getID());
//			System.out.println("con: " + con + "/" + Integer.toString(c.getID()));
			send(con.getBytes(), packet.getAddress(), packet.getPort());
		} else if(string.startsWith("/m/")) {
			//String message = string.substring(3);
			//System.out.println("message: " + string);
			sendToAll(string);
		} else if(string.startsWith("/d/")) {
			int ID = Integer.parseInt(string.split("/d/")[1]);
			disconnect(ID,true);
		} else if(string.startsWith("/i/")) {
			int ID = Integer.parseInt(string.split("/i/")[1]);
			if(!attempts.contains(ID)) {
				attempts.add(ID);
			}
		}
		else {
			System.out.println(string);
		}
	}
	
	private void disconnect(int ID, boolean status) {
		ServerClient c = null;
		for(int i=0; i<clients.size();i++) {
			c = clients.get(i);
			if(c.getID()==ID) {
				clients.remove(i);
				i=clients.size();
			}
		}
		String message = "Client " + c.getName() + "(" + c.getID() + ") @ " + c.getAddress().toString() + " : " + c.getPort();
		if(status) {
			message += " Disconnected!!";
		} else {
			message += " Timed Out!!";
		}
		System.out.println(message);
	}
}
