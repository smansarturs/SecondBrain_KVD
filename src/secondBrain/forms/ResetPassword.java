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

import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Color;

public class ResetPassword extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField emailField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ResetPassword frame = new ResetPassword();
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
	public ResetPassword() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 353, 553);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Reset password");
		lblNewLabel.setForeground(SystemColor.textHighlight);
		lblNewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblNewLabel.setBounds(111, 50, 120, 21);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Email");
		lblNewLabel_1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(26, 155, 39, 21);
		contentPane.add(lblNewLabel_1);
		
		emailField = new JTextField();
		emailField.setBounds(26, 187, 287, 29);
		contentPane.add(emailField);
		emailField.setColumns(10);
		
		JButton btnNewButton = new JButton("Cancel");
		btnNewButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnNewButton.setBounds(26, 243, 120, 23);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Reset password");
		btnNewButton_1.setBounds(166, 243, 147, 25);
		contentPane.add(btnNewButton_1);
		
		JLabel emailErrorLabel = new JLabel("");
		emailErrorLabel.setBounds(26, 218, 287, 14);
		contentPane.add(emailErrorLabel);
		
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String email = emailField.getText();
				
				if (email.equals("")) {
					emailErrorLabel.setText("Email is required!");
					return;
				} else {
					emailErrorLabel.setText("");
				}
				
				
				
			}
			
		});
	}
}
