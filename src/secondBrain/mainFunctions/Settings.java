package secondBrain.mainFunctions;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import secondBrain.data.LanguageManager;
import secondBrain.data.ThemeManager;
import secondBrain.data.UserPreferences;
import secondBrain.services.UserService;

public class Settings extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private int userId;
	private UserService userService;
	private ThemeManager themeManager;
	private LanguageManager languageManager;
	private UserPreferences userPreferences;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Settings frame = new Settings(1);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame with userId
	 */
	public Settings(int userId) {
		this.userId = userId;
		
		try {
			this.userService = new UserService();
			this.themeManager = ThemeManager.getInstance();
			this.languageManager = LanguageManager.getInstance();
			this.userPreferences = UserPreferences.getInstance();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error initializing services: " + e.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
		
		initializeUI();
	}
	
	private void initializeUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 397, 534);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel(languageManager.getString("settings.title"));
		lblNewLabel.setForeground(SystemColor.textHighlight);
		lblNewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		lblNewLabel.setBounds(26, 11, 102, 32);
		contentPane.add(lblNewLabel);
		
		JToggleButton tglbtnNewToggleButton = new JToggleButton(languageManager.getString("settings.darkmode.off"));
		tglbtnNewToggleButton.setSelected(userPreferences.isDarkModeEnabled());
		tglbtnNewToggleButton.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		tglbtnNewToggleButton.setBounds(10, 114, 361, 38);
		tglbtnNewToggleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleDarkMode(tglbtnNewToggleButton);
			}
		});
		contentPane.add(tglbtnNewToggleButton);
		
		JButton btnNewButton = new JButton(languageManager.getString("settings.delete.account"));
		btnNewButton.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		btnNewButton.setBounds(10, 375, 361, 38);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteAccount();
			}
		});
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton(languageManager.getString("settings.logout"));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logout();
			}
		});
		btnNewButton_1.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		btnNewButton_1.setBounds(10, 326, 361, 38);
		contentPane.add(btnNewButton_1);
		
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.addItem("English");
		comboBox.addItem("Español");
		comboBox.addItem("Français");
		comboBox.addItem("Deutsch");
		comboBox.addItem("日本語");
		comboBox.addItem("Latviešu");
		comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		comboBox.setBounds(214, 208, 90, 38);
		comboBox.setSelectedItem(userPreferences.getLanguage());
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeLanguage((String) comboBox.getSelectedItem());
			}
		});
		contentPane.add(comboBox);
		
		JLabel lblNewLabel_1 = new JLabel(languageManager.getString("settings.language"));
		lblNewLabel_1.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		lblNewLabel_1.setBounds(40, 206, 173, 38);
		contentPane.add(lblNewLabel_1);
		
		JButton btnAboutUs = new JButton(languageManager.getString("settings.about"));
		btnAboutUs.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		btnAboutUs.setBounds(10, 424, 361, 38);
		btnAboutUs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showAboutUs();
			}
		});
		contentPane.add(btnAboutUs);
		
		// Apply current theme
		themeManager.applyTheme(this);
	}
	
	/**
	 * Toggle dark mode on/off and update all open GUI windows
	 */
	private void toggleDarkMode(JToggleButton toggleButton) {
		boolean darkModeEnabled = toggleButton.isSelected();
		userPreferences.setDarkModeEnabled(darkModeEnabled);
		
		if (darkModeEnabled) {
			toggleButton.setText(languageManager.getString("settings.darkmode.on"));
		} else {
			toggleButton.setText(languageManager.getString("settings.darkmode.off"));
		}
		
		// Apply theme to all open windows
		themeManager.setDarkMode(darkModeEnabled);
		JOptionPane.showMessageDialog(this, 
			languageManager.getString("settings.theme.changed"),
			languageManager.getString("settings.theme.title"),
			JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Handle account deletion with confirmation and database deletion
	 */
	private void deleteAccount() {
		int response = JOptionPane.showConfirmDialog(this,
			languageManager.getString("settings.delete.confirm"),
			languageManager.getString("settings.delete.title"),
			JOptionPane.YES_NO_OPTION,
			JOptionPane.WARNING_MESSAGE);
		
		if (response == JOptionPane.YES_OPTION) {
			performAccountDeletion();
		}
	}
	
	/**
	 * Execute account deletion logic in database
	 */
	private void performAccountDeletion() {
		try {
			boolean success = userService.delete(userId);
			
			if (success) {
				JOptionPane.showMessageDialog(this,
					languageManager.getString("settings.delete.success"),
					languageManager.getString("settings.delete.title"),
					JOptionPane.INFORMATION_MESSAGE);
				
				userPreferences.clearUserData();
				returnToLogin();
			} else {
				JOptionPane.showMessageDialog(this,
					languageManager.getString("settings.delete.failed"),
					languageManager.getString("error.title"),
					JOptionPane.ERROR_MESSAGE);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this,
				languageManager.getString("error.database") + ": " + e.getMessage(),
				languageManager.getString("error.title"),
				JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	/**
	 * Handle user logout
	 */
	private void logout() {
		int response = JOptionPane.showConfirmDialog(this,
			languageManager.getString("settings.logout.confirm"),
			languageManager.getString("settings.logout.title"),
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE);
		
		if (response == JOptionPane.YES_OPTION) {
			userPreferences.clearUserSession();
			returnToLogin();
		}
	}
	
	/**
	 * Change application language
	 */
	private void changeLanguage(String language) {
		try {
			userPreferences.setLanguage(language);
			languageManager.setLanguage(language);
			
			JOptionPane.showMessageDialog(this,
				languageManager.getString("settings.language.changed") + ": " + language,
				languageManager.getString("settings.language.title"),
				JOptionPane.INFORMATION_MESSAGE);
			
			// Refresh UI with new language
			refreshUI();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
				languageManager.getString("error.language") + ": " + e.getMessage(),
				languageManager.getString("error.title"),
				JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	/**
	 * Refresh UI with new language strings
	 */
	private void refreshUI() {
		// Dispose and recreate the frame with updated language
		this.dispose();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Settings newFrame = new Settings(userId);
					newFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Display About Us dialog
	 */
	private void showAboutUs() {
		String aboutMessage = languageManager.getString("about.app.name") + "\n\n" +
							  languageManager.getString("about.version") + ": 1.0.0\n\n" +
							  languageManager.getString("about.description") + "\n\n" +
							  languageManager.getString("about.copyright");
		
		JOptionPane.showMessageDialog(this,
			aboutMessage,
			languageManager.getString("settings.about.title"),
			JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Return to login screen (placeholder implementation)
	 */
	private void returnToLogin() {
		this.dispose();

	}
}
