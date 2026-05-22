package secondBrain.mainFunctions;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.SystemColor;
import java.awt.Font;
import javax.swing.JButton;

public class Home extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private int userId;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Home frame = new Home(1);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame with default userId
	 */
	public Home() {
		this(0);
	}

	/**
	 * Create the frame with userId
	 */
	public Home(int userId) {
		this.userId = userId;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 379, 612);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Home");
		lblNewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 30));
		lblNewLabel.setForeground(SystemColor.textHighlight);
		lblNewLabel.setBounds(27, 29, 130, 34);
		contentPane.add(lblNewLabel);
		
		JButton btnNewButton = new JButton("Onboarding");
		btnNewButton.setBounds(27, 138, 311, 34);
		contentPane.add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openOnboarding();
			}
		});
		
		JButton btnNewButton_1 = new JButton("Connection editor");
		btnNewButton_1.setBounds(27, 183, 311, 34);
		contentPane.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("Focus mode");
		btnNewButton_2.setBounds(27, 228, 311, 34);
		contentPane.add(btnNewButton_2);
		
		JButton btnNewButton_3 = new JButton("Search view");
		btnNewButton_3.setBounds(27, 273, 311, 34);
		contentPane.add(btnNewButton_3);
		
		JButton btnNewButton_4 = new JButton("Canvas view");
		btnNewButton_4.setBounds(27, 318, 311, 34);
		contentPane.add(btnNewButton_4);
		
		JLabel lblNewLabel_1 = new JLabel("What do you want to do?");
		lblNewLabel_1.setForeground(SystemColor.textHighlight);
		lblNewLabel_1.setFont(new Font("Segoe UI", Font.PLAIN, 28));
		lblNewLabel_1.setBounds(27, 93, 326, 34);
		contentPane.add(lblNewLabel_1);
		
		JButton btnNewButton_5 = new JButton("Add node modal");
		btnNewButton_5.setBounds(27, 363, 311, 34);
		contentPane.add(btnNewButton_5);
		
		JButton btnNewButton_6 = new JButton("Settings");
		btnNewButton_6.setBounds(27, 408, 311, 34);
		contentPane.add(btnNewButton_6);

	}

	/**
	 * Opens the Onboarding window with current userId
	 */
	private void openOnboarding() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.out.println("Debug: Opening Onboarding with userId=" + userId);
					Onboarding onboardingFrame = new Onboarding(userId, 0);
					onboardingFrame.setVisible(true);
				} catch (Exception e) {
					System.err.println("Error opening Onboarding: " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
	}
	
	

	/**
	 * Get the current userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * Set the userId (useful when called from Login form)
	 */
	public void setUserId(int userId) {
		this.userId = userId;
		System.out.println("Debug: UserId set to " + userId);
	}
	
	
}
