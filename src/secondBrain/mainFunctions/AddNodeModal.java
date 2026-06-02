package secondBrain.mainFunctions;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;

import secondBrain.services.NodeService;
import secondBrain.services.ProjectService;

/**
 * An Add Node modal is a popup window used to create a new note or item in a note-taking app.
 * You enter details like the node's title and content, then save it to 
 * add it to your notes or connect it to other nodes.
 */

public class AddNodeModal extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField titleField;
	private JTextArea contentArea;
	private JButton saveButton;
	private JButton cancelButton;
	private boolean saved = false;
	private int userId;
	private int projectId;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AddNodeModal dialog = new AddNodeModal(null, 1, 0);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public AddNodeModal() {
		
	}

	/**
	 * Create the modal dialog.
	 */
	public AddNodeModal(java.awt.Frame parent, int userId, int projectId) {
		super(parent, "Add Node", true);
		this.userId = userId;
		this.projectId = projectId;
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 300);
		setLocationRelativeTo(parent);
		setResizable(false);
		
		javax.swing.JPanel contentPane = new javax.swing.JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		JLabel titleLabel = new JLabel("Title:");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 11));
		titleLabel.setBounds(10, 10, 50, 20);
		contentPane.add(titleLabel);

		titleField = new JTextField();
		titleField.setFont(new Font("Arial", Font.PLAIN, 11));
		titleField.setBounds(10, 32, 360, 28);
		contentPane.add(titleField);

		JLabel contentLabel = new JLabel("Content:");
		contentLabel.setFont(new Font("Arial", Font.BOLD, 11));
		contentLabel.setBounds(10, 65, 60, 20);
		contentPane.add(contentLabel);

		contentArea = new JTextArea();
		contentArea.setFont(new Font("Arial", Font.PLAIN, 11));
		contentArea.setLineWrap(true);
		contentArea.setWrapStyleWord(true);
		
		JScrollPane scrollPane = new JScrollPane(contentArea);
		scrollPane.setBounds(10, 87, 360, 130);
		contentPane.add(scrollPane);

		saveButton = new JButton("Save");
		saveButton.setFont(new Font("Arial", Font.PLAIN, 11));
		saveButton.setBounds(215, 230, 75, 28);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveNode();
			}
		});
		contentPane.add(saveButton);

		cancelButton = new JButton("Cancel");
		cancelButton.setFont(new Font("Arial", Font.PLAIN, 11));
		cancelButton.setBounds(295, 230, 75, 28);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		contentPane.add(cancelButton);
	}

	private void saveNode() {
		String title = titleField.getText().trim();
		String content = contentArea.getText().trim();

		if (title.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Title cannot be empty!", "Validation Error", JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (content.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Content cannot be empty!", "Validation Error", JOptionPane.WARNING_MESSAGE);
			return;
		}

		try {
			if (projectId <= 0) {
				ProjectService projectService = new ProjectService();
				projectId = projectService.getOrCreateDefaultProject(userId);
			}

			NodeService nodeService = new NodeService();
			boolean isCreated = nodeService.insert(userId, title, content, projectId);

			if (isCreated) {
				saved = true;
				dispose();
			} else {
				JOptionPane.showMessageDialog(this, "Failed to create node.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public boolean isSaved() {
		return saved;
	}

	public String getNodeTitle() {
		return titleField.getText().trim();
	}

	public String getNodeContent() {
		return contentArea.getText().trim();
	}
}
