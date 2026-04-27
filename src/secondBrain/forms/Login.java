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
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import secondBrain.services.UserService;

public class Login extends JFrame {

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
					Login frame = new Login();
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
	public Login() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 377, 556);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Welcome back to SecondBrain!");
		lblNewLabel.setForeground(SystemColor.textHighlight);
		lblNewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblNewLabel.setBounds(64, 34, 214, 27);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Email");
		lblNewLabel_1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(38, 105, 54, 20);
		contentPane.add(lblNewLabel_1);
		
		emailField = new JTextField();
		emailField.setBounds(38, 127, 290, 33);
		contentPane.add(emailField);
		emailField.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Password");
		lblNewLabel_2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblNewLabel_2.setBounds(38, 209, 67, 20);
		contentPane.add(lblNewLabel_2);
		
		JLabel emailErrorLabel = new JLabel("");
		emailErrorLabel.setBounds(38, 171, 290, 14);
		contentPane.add(emailErrorLabel);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(38, 236, 290, 33);
		contentPane.add(passwordField);
		
		JLabel passwordErrorLabel = new JLabel("");
		passwordErrorLabel.setBounds(38, 280, 290, 14);
		contentPane.add(passwordErrorLabel);
		
		JButton btnNewButton = new JButton("Sign in");
		btnNewButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnNewButton.setBounds(38, 321, 290, 33);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Reset password");
		btnNewButton_1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnNewButton_1.setBounds(25, 411, 130, 23);
		contentPane.add(btnNewButton_1);
		
		btnNewButton_1.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        ResetPassword resetPasswordFrame = new ResetPassword();
		        resetPasswordFrame.setVisible(true);
		    }
		});
		
		JLabel lblNewLabel_3 = new JLabel("Forgot password?");
		lblNewLabel_3.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblNewLabel_3.setBounds(38, 396, 103, 14);
		contentPane.add(lblNewLabel_3);
		
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
	}

}
