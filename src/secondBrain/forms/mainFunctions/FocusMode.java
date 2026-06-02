package secondBrain.forms.mainFunctions;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.ScrollPaneConstants;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.ActionMap;

public class FocusMode extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextArea noteTextArea;
	private JLabel noteTitle;
	private String noteContent = "";
	private String noteHeading = "";
	private int userId;
	private int nodeId;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FocusMode frame = new FocusMode();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public FocusMode() {
		this(0, 0);
	}

	public FocusMode(int userId, int nodeId) {
		this.userId = userId;
		this.nodeId = nodeId;
		
		setTitle("Focus Mode - SecondBrain");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 900, 700);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(30, 40, 30, 40));
		contentPane.setLayout(new BorderLayout(0, 20));
		setContentPane(contentPane);
		
		initializeUI();
		setupEscapeKeyBinding();
	}

	private void setupEscapeKeyBinding() {
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		
		InputMap inputMap = noteTextArea.getInputMap();
		ActionMap actionMap = noteTextArea.getActionMap();
		
		inputMap.put(escapeKeyStroke, "exitFocusMode");
		actionMap.put("exitFocusMode", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exitFocusMode();
			}
		});
	}

	private void initializeUI() {
		noteTitle = new JLabel(noteHeading);
		noteTitle.setFont(new Font("Arial", Font.BOLD, 32));
		noteTitle.setForeground(new Color(50, 50, 50));
		contentPane.add(noteTitle, BorderLayout.NORTH);
		
		noteTextArea = new JTextArea();
		noteTextArea.setText(noteContent);
		noteTextArea.setFont(new Font("Arial", Font.PLAIN, 16));
		noteTextArea.setLineWrap(true);
		noteTextArea.setWrapStyleWord(true);
		noteTextArea.setEditable(true);
		noteTextArea.setBackground(Color.WHITE);
		noteTextArea.setForeground(new Color(50, 50, 50));
		noteTextArea.setCaretColor(new Color(0, 150, 200));
		noteTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));
		noteTextArea.setMargin(new java.awt.Insets(10, 10, 10, 10));
		
		JScrollPane scrollPane = new JScrollPane(noteTextArea);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(null);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		JLabel footerHint = new JLabel("Press ESC to exit focus mode");
		footerHint.setFont(new Font("Arial", Font.ITALIC, 12));
		footerHint.setForeground(new Color(150, 150, 150));
		contentPane.add(footerHint, BorderLayout.SOUTH);
	}

	/**
	 * Set the note content to display in focus mode.
	 * 
	 * @param title   The note title/heading
	 * @param content The note content
	 */
	public void setNoteContent(String title, String content) {
		this.noteHeading = title != null ? title : "";
		this.noteContent = content != null ? content : "";
		
		if (noteTitle != null) {
			noteTitle.setText(this.noteHeading);
		}
		if (noteTextArea != null) {
			noteTextArea.setText(this.noteContent);
			noteTextArea.setCaretPosition(0);
		}
	}

	private void exitFocusMode() {
		String updatedContent = noteTextArea.getText();
		System.out.println("Debug: Exiting focus mode. Content length: " + updatedContent.length());
		this.dispose();
	}

	/**
	 * Get the current content being edited in focus mode.
	 * 
	 * @return The current text in the focus mode editor
	 */
	public String getNoteContent() {
		return noteTextArea != null ? noteTextArea.getText() : "";
	}

	/**
	 * Enable/disable editing in focus mode.
	 * 
	 * @param editable True to allow editing, false for read-only mode
	 */
	public void setEditable(boolean editable) {
		if (noteTextArea != null) {
			noteTextArea.setEditable(editable);
		}
	}

	public int getUserId() {
		return userId;
	}

	public int getNodeId() {
		return nodeId;
	}

}
