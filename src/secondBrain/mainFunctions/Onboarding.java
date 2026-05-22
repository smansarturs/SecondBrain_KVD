package secondBrain.mainFunctions;

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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import secondBrain.services.NodeService;
import secondBrain.services.ProjectService;

public class Onboarding extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField titleField;
	private JTextArea contentArea;
	private JLabel errorLabel;
	private int userId;
	private int projectId;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Onboarding frame = new Onboarding();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame with default parameters
	 */
	public Onboarding() {
		this(0, 0);
	}

	/**
	 * Create the frame with user and project IDs
	 */
	public Onboarding(int userId, int projectId) {
		this.userId = userId;
		this.projectId = projectId;

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 459);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel titleLabel = new JLabel("Onboarding");
		titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		titleLabel.setForeground(SystemColor.textHighlight);
		titleLabel.setBounds(27, 11, 250, 30);
		contentPane.add(titleLabel);

		JLabel subtitleLabel = new JLabel("Create your note:");
		subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		subtitleLabel.setBounds(27, 45, 400, 15);
		contentPane.add(subtitleLabel);

		JLabel fieldTitleLabel = new JLabel("Node Title:");
		fieldTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		fieldTitleLabel.setBounds(27, 70, 100, 20);
		contentPane.add(fieldTitleLabel);

		titleField = new JTextField();
		titleField.setBounds(27, 90, 430, 30);
		contentPane.add(titleField);
		titleField.setColumns(10);

		JLabel contentLabel = new JLabel("Content:");
		contentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		contentLabel.setBounds(27, 125, 100, 20);
		contentPane.add(contentLabel);

		contentArea = new JTextArea();
		contentArea.setWrapStyleWord(true);
		contentArea.setLineWrap(true);
		contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		JScrollPane scrollPane = new JScrollPane(contentArea);
		scrollPane.setBounds(27, 145, 430, 120);
		contentPane.add(scrollPane);

		errorLabel = new JLabel("");
		errorLabel.setForeground(SystemColor.menuText);
		errorLabel.setBounds(27, 270, 430, 20);
		contentPane.add(errorLabel);

		JButton createButton = new JButton("Create Node");
		createButton.setBounds(27, 300, 210, 40);
		createButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		contentPane.add(createButton);

		JButton skipButton = new JButton("Skip");
		skipButton.setBounds(247, 300, 210, 40);
		skipButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		contentPane.add(skipButton);
		
		JButton btnBackhome = new JButton("Back to home");
		btnBackhome.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		btnBackhome.setBounds(132, 351, 210, 40);
		contentPane.add(btnBackhome);

		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNode();
			}
		});

		skipButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

	/**
	 * Creates a node in the database
	 */
	private void createNode() {
		String title = titleField.getText().trim();
		String content = contentArea.getText().trim();

		// Validation
		if (title.isEmpty()) {
			errorLabel.setText("Title cannot be empty!");
			errorLabel.setForeground(new java.awt.Color(255, 0, 0));
			return;
		}

		if (content.isEmpty()) {
			errorLabel.setText("Content cannot be empty!");
			errorLabel.setForeground(new java.awt.Color(255, 0, 0));
			return;
		}

		if (projectId <= 0) {
			try {
				System.out.println("Debug: projectId is " + projectId + ", getting or creating default project...");
				
				if (userId <= 0) {
					errorLabel.setText("Error: User ID not set!");
					errorLabel.setForeground(new java.awt.Color(255, 0, 0));
					System.err.println("Error: userId is " + userId);
					return;
				}
				
				ProjectService projectService = new ProjectService();
				projectId = projectService.getOrCreateDefaultProject(userId);
				
				if (projectId <= 0) {
					errorLabel.setText("Error: Could not create default project!");
					errorLabel.setForeground(new java.awt.Color(255, 0, 0));
					System.err.println("Error: Failed to get or create project");
					return;
				}
				
				System.out.println("Debug: Using projectId = " + projectId);
				
			} catch (ClassNotFoundException ex) {
				System.err.println("ClassNotFoundException in createDefaultProject: " + ex.getMessage());
				ex.printStackTrace();
				errorLabel.setText("Database driver error!");
				errorLabel.setForeground(new java.awt.Color(255, 0, 0));
				return;
			} catch (SQLException ex) {
				System.err.println("SQLException in createDefaultProject: " + ex.getMessage());
				ex.printStackTrace();
				errorLabel.setText("Database error: " + ex.getMessage());
				errorLabel.setForeground(new java.awt.Color(255, 0, 0));
				return;
			}
		}

		try {
			System.out.println("Debug: Creating node with title='" + title + "', content length=" + content.length() + ", projectId=" + projectId);
			
			NodeService nodeService = new NodeService();
			boolean isCreated = nodeService.insert(title, content, projectId);

			if (isCreated) {
				errorLabel.setText("Node created successfully!");
				errorLabel.setForeground(new java.awt.Color(0, 128, 0));
				System.out.println("Debug: Node created successfully!");
				
				titleField.setText("");
				contentArea.setText("");
				new Thread(() -> {
					try {
						Thread.sleep(2000);
						dispose();
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}).start();
			} else {
				errorLabel.setText("Failed to create node. Please try again.");
				errorLabel.setForeground(new java.awt.Color(255, 0, 0));
				System.err.println("Debug: NodeService.insert returned false");
			}
		} catch (ClassNotFoundException e) {
			System.err.println("ClassNotFoundException: " + e.getMessage());
			e.printStackTrace();
			errorLabel.setText("Database driver not found!");
			errorLabel.setForeground(new java.awt.Color(255, 0, 0));
		} catch (SQLException e) {
			System.err.println("SQLException: " + e.getMessage());
			e.printStackTrace();
			errorLabel.setText("Database error: " + e.getMessage());
			errorLabel.setForeground(new java.awt.Color(255, 0, 0));
		}
	}
}
