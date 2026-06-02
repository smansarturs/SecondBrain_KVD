package secondBrain.forms;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.SystemColor;
import javax.swing.JToggleButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Settings extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Settings frame = new Settings();
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
	public Settings() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 397, 534);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Settings ⚙️");
		lblNewLabel.setForeground(SystemColor.textHighlight);
		lblNewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		lblNewLabel.setBounds(26, 11, 102, 32);
		contentPane.add(lblNewLabel);
		
		JToggleButton tglbtnNewToggleButton = new JToggleButton("Turn on dark mode");
		tglbtnNewToggleButton.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		tglbtnNewToggleButton.setBounds(10, 114, 361, 38);
		contentPane.add(tglbtnNewToggleButton);
		
		JButton btnNewButton = new JButton("Delete account");
		btnNewButton.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		btnNewButton.setBounds(10, 375, 361, 38);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Log out");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton_1.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		btnNewButton_1.setBounds(10, 326, 361, 38);
		contentPane.add(btnNewButton_1);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		comboBox.setBounds(214, 208, 90, 38);
		contentPane.add(comboBox);
		
		JLabel lblNewLabel_1 = new JLabel("Change a language");
		lblNewLabel_1.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		lblNewLabel_1.setBounds(40, 206, 173, 38);
		contentPane.add(lblNewLabel_1);
		
		JButton btnAboutUs = new JButton("About us");
		btnAboutUs.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		btnAboutUs.setBounds(10, 424, 361, 38);
		contentPane.add(btnAboutUs);

	}
}
