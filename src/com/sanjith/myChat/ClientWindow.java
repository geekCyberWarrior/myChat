package com.sanjith.myChat;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class ClientWindow extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtMessage;
	private JTextArea history;
	private DefaultCaret caret;

	private Client client;

	private int ID = -1;

	private Thread listen, run;
	private boolean running;

	public ClientWindow(String name, String address, int port) {
		setTitle("Client");
		client = new Client(name, address, port);

		boolean connect = client.openConnection(address);
		if (!connect) {
			System.out.println("Connection failed!!");
			// console("Connection failed!!");
		}
		createWindow();
		console("Attempting a connection to " + address + " : " + port + ", user : " + name);
		String connection = "/c/" + name;
		client.send(connection.getBytes());
		run = new Thread(this, "Running");
		running = true;
		run.start();
	}

	@Override
	public void run() {
		listen();
	}

	public void listen() {
		listen = new Thread() {
			public void run() {
				while (running) {
					String message = client.recieve().trim();
					// System.out.println("The messsage: " + message);
					if (message.startsWith("/c/")) {
						// System.out.println(message.split("/c/")[1]);
						ID = Integer.parseInt(message.split("/c/")[1]);
						client.setID(ID);
						console("Successfully Connected to Server!! ID: " + client.getID());
					} else if (message.startsWith("/m/")) {
						console(message.substring(3));
					} else if (message.startsWith("/i/")) {
						String data = "/i/" + Integer.toString(client.getID());
						client.send(data.getBytes());
					} else {
						console(message);
					}
				}
			}
		};
		listen.start();
	}

	private void createWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(880, 550);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 10, 810, 30, 30 };
		gbl_contentPane.rowHeights = new int[] { 10, 520, 10, 10 };
		gbl_contentPane.columnWeights = new double[] { 0.011363636363636364, 0.9204545454545454, 0.03409090909090909,
				0.03409090909090909 };
		gbl_contentPane.rowWeights = new double[] { 0.01818181818181818, 0.8527272727272728, 0.01818181818181818,
				0.01818181818181818 };
		contentPane.setLayout(gbl_contentPane);

		history = new JTextArea();
		history.setEditable(false);
		caret = (DefaultCaret) history.getCaret();
		// caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scroll = new JScrollPane(history);
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		scrollConstraints.insets = new Insets(0, 0, 5, 5);
		scrollConstraints.fill = GridBagConstraints.BOTH;
		scrollConstraints.gridx = 1;
		scrollConstraints.gridy = 1;
		scrollConstraints.gridwidth = 3;
		contentPane.add(scroll, scrollConstraints);

		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					send(txtMessage.getText());
				}
			}
		});
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessage.gridx = 1;
		gbc_txtMessage.gridy = 2;
		contentPane.add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);

		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send(txtMessage.getText());
				txtMessage.requestFocusInWindow();
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.insets = new Insets(0, 0, 0, 5); // PADDING
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 2;
		gbc_btnSend.gridwidth = 2;
		contentPane.add(btnSend, gbc_btnSend);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				String message = "/d/" + client.getID();
				client.send(message.getBytes());
				running = false;
				client.close();
			}
		});

		setVisible(true);

		txtMessage.requestFocusInWindow();
		/*
		 * requestFocus(); txtMessage.requestFocus();
		 */
	}

	public void console(String message) {
		history.append(message + "\n");
		// history.setCaretPosition(history.getDocument().getLength());
	}

	public void send(String message) {
		if (!message.equals("")) {
			message = client.getName() + " : " + message;
			// console(message);
			message = "/m/" + message;
			client.send(message.getBytes());
			// running = false;
			txtMessage.setText("");
		}
	}

//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					ClientWindow frame = new ClientWindow();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

}
