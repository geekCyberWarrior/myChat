package com.sanjith.myChat;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtName;
	private JTextField txtIpAddress;
	private JTextField txtPort;
	private JButton btnLogin;

	// private LoginButton handle = new LoginButton();

	public Login() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setResizable(false);
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 380);
		setVisible(true);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		txtName = new JTextField();
		txtName.setText("sanjith s jadhav");
		txtName.setBounds(67, 33, 165, 19);
		contentPane.add(txtName);
		txtName.setColumns(10);

		JLabel lblName = new JLabel("Name :");
		lblName.setBounds(115, 12, 70, 15);
		contentPane.add(lblName);

		txtIpAddress = new JTextField();
		txtIpAddress.setText("localhost");
		txtIpAddress.setColumns(10);
		txtIpAddress.setBounds(67, 116, 165, 19);
		contentPane.add(txtIpAddress);

		JLabel lblIpAddress = new JLabel("IP Address :");
		lblIpAddress.setBounds(109, 95, 87, 15);
		contentPane.add(lblIpAddress);

		JLabel lblPort = new JLabel("Port :");
		lblPort.setBounds(121, 178, 47, 15);
		contentPane.add(lblPort);

		txtPort = new JTextField();
		txtPort.setText("8192");
		txtPort.setColumns(10);
		txtPort.setBounds(67, 199, 165, 19);
		contentPane.add(txtPort);

		btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = txtName.getText();
				String address = txtIpAddress.getText();
				int port = Integer.parseInt(txtPort.getText());
				login(name, address, port);
			}

		});
		// btnLogin.addActionListener(handle);
		btnLogin.setBounds(91, 278, 117, 25);

		contentPane.add(btnLogin);
		btnLogin.requestFocus();
	}

	private void login(String name, String address, int port) {
		dispose();
		new ClientWindow(name, address, port);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
