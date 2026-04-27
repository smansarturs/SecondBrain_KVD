package secondBrain.forms;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import secondBrain.services.UserService;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Register extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField emailField;
	private JPasswordField passwordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Register frame = new Register();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Register() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 360, 618);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Welcome to SecondBrain!");
		lblNewLabel.setForeground(SystemColor.textHighlight);
		lblNewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		lblNewLabel.setBounds(50, 42, 236, 31);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Email");
		lblNewLabel_1.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblNewLabel_1.setBounds(27, 133, 43, 20);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Password");
		lblNewLabel_2.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblNewLabel_2.setBounds(27, 217, 66, 20);
		contentPane.add(lblNewLabel_2);
		
		JButton btnNewButton = new JButton("Register");
		btnNewButton.setForeground(SystemColor.menuText);
		btnNewButton.setBounds(27, 321, 288, 41);
		contentPane.add(btnNewButton);
		
		JLabel emailErrorLabel = new JLabel("");
		emailErrorLabel.setBounds(27, 202, 288, 14);
		contentPane.add(emailErrorLabel);
		
		JLabel passwordErrorLabel = new JLabel("");
		passwordErrorLabel.setBounds(27, 287, 288, 14);
		contentPane.add(passwordErrorLabel);
		
		emailField = new JTextField();
		emailField.setBounds(27, 155, 288, 36);
		contentPane.add(emailField);
		emailField.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("Already have an account?");
		lblNewLabel_4.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblNewLabel_4.setBounds(37, 376, 137, 14);
		contentPane.add(lblNewLabel_4);
		
		JButton btnNewButton_1 = new JButton("Log in!");
		btnNewButton_1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnNewButton_1.setBounds(174, 373, 89, 23);
		contentPane.add(btnNewButton_1);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(27, 248, 288, 36);
		contentPane.add(passwordField);
		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String email = emailField.getText();
				String password = new String(passwordField.getPassword());


				if (email.equals("")) {
					emailErrorLabel.setText("Email is required!");
					return;
				} else {
					emailErrorLabel.setText("");
				}
				
				if (password.equals("")) {
					passwordErrorLabel.setText("Password is required!");
					return;
				} else {
					passwordErrorLabel.setText("");
				}
				
				try {
					UserService service = new UserService();
					
					boolean isInserted = service.insert(email, password);
					
					if (!isInserted) {
					    Login login = new Login();
					    login.setVisible(true);
					    login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					    dispose();
					} else {
					    System.out.println("Nav pievienots!");
					}
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
				}
			}
			
		});
		
		 btnNewButton_1.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		          new Login();
		          dispose();
		        }
		    });

	}
}
