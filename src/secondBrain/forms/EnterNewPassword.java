package secondBrain.forms;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JPasswordField;
import javax.swing.JButton;

import secondBrain.services.UserService;

public class EnterNewPassword extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPasswordField passwordField;
	private JPasswordField confirmPasswordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EnterNewPassword frame = new EnterNewPassword();
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
	public EnterNewPassword() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 339, 539);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Reset password");
		lblNewLabel.setForeground(SystemColor.textHighlight);
		lblNewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblNewLabel.setBounds(97, 52, 117, 21);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Enter new Password");
		lblNewLabel_1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(39, 135, 132, 21);
		contentPane.add(lblNewLabel_1);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(39, 167, 247, 33);
		contentPane.add(passwordField);
		
		JLabel passwordErrorLabel = new JLabel("");
		passwordErrorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		passwordErrorLabel.setBounds(38, 211, 247, 14);
		contentPane.add(passwordErrorLabel);
		
		JLabel lblNewLabel_3 = new JLabel("Confirm new password");
		lblNewLabel_3.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblNewLabel_3.setBounds(39, 261, 151, 21);
		contentPane.add(lblNewLabel_3);
		
		confirmPasswordField = new JPasswordField();
		confirmPasswordField.setBounds(39, 293, 247, 33);
		contentPane.add(confirmPasswordField);
		
		JLabel confirmPasswordErrorLabel = new JLabel("");
		confirmPasswordErrorLabel.setBounds(39, 337, 247, 14);
		contentPane.add(confirmPasswordErrorLabel);
		
		JButton btnNewButton = new JButton("Reset password");
		btnNewButton.setBounds(70, 375, 175, 37);
		contentPane.add(btnNewButton);
		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String password = new String(passwordField.getPassword());
				String confirmPassword = new String(confirmPasswordField.getPassword());
				
				// Validate password is not empty
				if (password.equals("")) {
					passwordErrorLabel.setText("Password is required!");
					return;
				} else {
					passwordErrorLabel.setText("");
				}
				
				// Validate confirm password is not empty
				if (confirmPassword.equals("")) {
					confirmPasswordErrorLabel.setText("Please confirm your password!");
					return;
				} else {
					confirmPasswordErrorLabel.setText("");
				}
				
				// Validate passwords match
				if (!password.equals(confirmPassword)) {
					confirmPasswordErrorLabel.setText("Passwords do not match!");
					return;
				} else {
					confirmPasswordErrorLabel.setText("");
				}
				
				// Update password in database
				try {
					UserService service = new UserService();
					boolean isUpdated = service.updatePassword(password);
					
					if (isUpdated) {
						Login login = new Login();
						login.setVisible(true);
						login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						dispose();
					} else {
						confirmPasswordErrorLabel.setText("Failed to update password!");
					}
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
					confirmPasswordErrorLabel.setText("Error: " + e1.getMessage());
				}
			}
		});

	}

}
