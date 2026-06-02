package secondBrain.forms.mainFunctions;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Color;
import java.awt.BasicStroke;

public class CanvasView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private CanvasPanel canvasPanel;
	private int userId;
	private int projectId;
	private JButton btnNewElement;
	private JButton btnDelete;
	private JButton btnUndo;
	private JButton btnRedo;
	private JButton btnSave;
	private JButton btnLoad;
	private CanvasDatabase database;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CanvasView frame = new CanvasView(1, 1);
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
	public CanvasView(int userId, int projectId) {
	    this.userId = userId;
	    this.projectId = projectId;
	    this.database = new CanvasDatabase();
	    
	    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    setBounds(100, 100, 1150, 650);
	    setTitle("SecondBrain - Canvas View [User: " + userId + " | Project: " + projectId + "]");
	    
	    contentPane = new JPanel();
	    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	    setContentPane(contentPane);
	    contentPane.setLayout(null);
	    
	    JLabel lblTitle = new JLabel("Canvas View - Double-click to edit text | Drag corners to resize | Right-click for color");
	    lblTitle.setForeground(SystemColor.textHighlight);
	    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
	    lblTitle.setBounds(10, 10, 700, 22);
	    contentPane.add(lblTitle);
	    
	    btnNewElement = new JButton("+ New");
	    btnNewElement.setBounds(720, 8, 70, 25);
	    btnNewElement.addActionListener(e -> canvasPanel.addNewElement());
	    contentPane.add(btnNewElement);
	    
	    btnUndo = new JButton("↶ Undo");
	    btnUndo.setBounds(800, 8, 70, 25);
	    btnUndo.addActionListener(e -> canvasPanel.undo());
	    contentPane.add(btnUndo);
	    
	    btnRedo = new JButton("↷ Redo");
	    btnRedo.setBounds(880, 8, 70, 25);
	    btnRedo.addActionListener(e -> canvasPanel.redo());
	    contentPane.add(btnRedo);
	    
	    btnDelete = new JButton("🗑 Delete");
	    btnDelete.setBounds(960, 8, 80, 25);
	    btnDelete.addActionListener(e -> canvasPanel.deleteSelected());
	    contentPane.add(btnDelete);
	    
	    btnSave = new JButton("💾 Save");
	    btnSave.setBounds(1050, 8, 80, 25);
	    btnSave.addActionListener(e -> canvasPanel.saveCanvas());
	    contentPane.add(btnSave);
	    
	    canvasPanel = new CanvasPanel();
	    canvasPanel.setBounds(10, 40, 1120, 570);
	    contentPane.add(canvasPanel);
	    canvasPanel.setFocusable(true);
	    
	    canvasPanel.loadCanvas();
	}

	
	private class CanvasPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
		private static final long serialVersionUID = 1L;
		private List<CanvasElement> elements;
		private Stack<List<CanvasElement>> undoStack;
		private Stack<List<CanvasElement>> redoStack;
		private CanvasElement selectedElement;
		private int lastMouseX;
		private int lastMouseY;
		private boolean isDragging;
		private boolean isResizing;
		private CanvasElement editingElement;
		private EditDialog editDialog;

		public CanvasPanel() {
			elements = new ArrayList<>();
			undoStack = new Stack<>();
			redoStack = new Stack<>();
			isDragging = false;
			isResizing = false;
			
			elements.add(new CanvasElement(50, 50, 200, 150, "Welcome", new Color(100, 150, 255)));
			elements.add(new CanvasElement(300, 100, 180, 120, "My Ideas", new Color(100, 255, 150)));
			
			addMouseListener(this);
			addMouseMotionListener(this);
			addKeyListener(this);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			drawGrid(g2d);
			drawElements(g2d);
		}

		private void drawGrid(Graphics2D g2d) {
			int gridSize = 20;
			g2d.setColor(new Color(230, 230, 230));
			
			for (int i = 0; i < getWidth(); i += gridSize) {
				g2d.drawLine(i, 0, i, getHeight());
			}
			for (int i = 0; i < getHeight(); i += gridSize) {
				g2d.drawLine(0, i, getWidth(), i);
			}
		}

		private void drawElements(Graphics2D g2d) {
			for (CanvasElement element : elements) {
				element.draw(g2d);
				
				if (element == selectedElement) {
					g2d.setColor(new Color(0, 0, 0));
					g2d.setStroke(new BasicStroke(3.0f));
					g2d.drawRect(element.x, element.y, element.width, element.height);
					
					drawResizeHandles(g2d, element);
				}
			}
		}

		private void drawResizeHandles(Graphics2D g2d, CanvasElement element) {
			g2d.setColor(new Color(255, 0, 0));
			int handleSize = 10;
			
			g2d.fillRect(element.x - handleSize/2, element.y - handleSize/2, 
					handleSize, handleSize);
			g2d.fillRect(element.x + element.width - handleSize/2, element.y - handleSize/2, 
					handleSize, handleSize);
			g2d.fillRect(element.x - handleSize/2, element.y + element.height - handleSize/2, 
					handleSize, handleSize);
			g2d.fillRect(element.x + element.width - handleSize/2, element.y + element.height - handleSize/2, 
					handleSize, handleSize);
		}

		public void addNewElement() {
			saveState();
			int newX = (int) (Math.random() * (getWidth() - 200));
			int newY = (int) (Math.random() * (getHeight() - 150));
			Color randomColor = new Color(
				(int)(Math.random() * 256),
				(int)(Math.random() * 256),
				(int)(Math.random() * 256)
			);
			elements.add(new CanvasElement(newX, newY, 200, 150, "New Note", randomColor));
			redoStack.clear();
			repaint();
		}

		public void deleteSelected() {
			if (selectedElement != null) {
				saveState();
				elements.remove(selectedElement);
				selectedElement = null;
				redoStack.clear();
				repaint();
			}
		}

		public void undo() {
			if (!undoStack.isEmpty()) {
				redoStack.push(deepCopyElements(elements));
				elements = deepCopyElements(undoStack.pop());
				selectedElement = null;
				repaint();
			}
		}

		public void redo() {
			if (!redoStack.isEmpty()) {
				undoStack.push(deepCopyElements(elements));
				elements = deepCopyElements(redoStack.pop());
				selectedElement = null;
				repaint();
			}
		}

		public void saveCanvas() {
			database.saveCanvas(userId, projectId, elements);
			JOptionPane.showMessageDialog(this, "Canvas saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
		}

		public void loadCanvas() {
			List<CanvasElement> loaded = database.loadCanvas(userId, projectId);
			if (loaded != null && !loaded.isEmpty()) {
				elements = loaded;
				repaint();
			}
		}

		private void saveState() {
			undoStack.push(deepCopyElements(elements));
		}

		private List<CanvasElement> deepCopyElements(List<CanvasElement> original) {
			List<CanvasElement> copy = new ArrayList<>();
			for (CanvasElement elem : original) {
				copy.add(new CanvasElement(elem.x, elem.y, elem.width, elem.height, elem.text, new Color(elem.color.getRGB())));
			}
			return copy;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			lastMouseX = e.getX();
			lastMouseY = e.getY();
			
			if (e.getButton() == MouseEvent.BUTTON3) {
				for (int i = elements.size() - 1; i >= 0; i--) {
					CanvasElement element = elements.get(i);
					if (element.contains(e.getX(), e.getY())) {
						selectedElement = element;
						Color newColor = JColorChooser.showDialog(CanvasPanel.this, "Choose Color", element.color);
						if (newColor != null) {
							saveState();
							element.color = newColor;
							redoStack.clear();
							repaint();
						}
						return;
					}
				}
			}
			
			if (selectedElement != null && selectedElement.getResizeHandle(e.getX(), e.getY()) != -1) {
				isResizing = true;
				isDragging = false;
				return;
			}
			
			selectedElement = null;
			for (int i = elements.size() - 1; i >= 0; i--) {
				CanvasElement element = elements.get(i);
				if (element.contains(e.getX(), e.getY())) {
					selectedElement = element;
					elements.remove(i);
					elements.add(selectedElement);
					isDragging = true;
					break;
				}
			}
			
			requestFocus();
			repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			isDragging = false;
			isResizing = false;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (isResizing && selectedElement != null) {
				int deltaX = e.getX() - lastMouseX;
				int deltaY = e.getY() - lastMouseY;
				
				selectedElement.width = Math.max(50, selectedElement.width + deltaX);
				selectedElement.height = Math.max(50, selectedElement.height + deltaY);
				
				lastMouseX = e.getX();
				lastMouseY = e.getY();
				
				repaint();
			} else if (isDragging && selectedElement != null) {
				int deltaX = e.getX() - lastMouseX;
				int deltaY = e.getY() - lastMouseY;
				
				selectedElement.x += deltaX;
				selectedElement.y += deltaY;
				
				lastMouseX = e.getX();
				lastMouseY = e.getY();
				
				repaint();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (selectedElement != null && selectedElement.getResizeHandle(e.getX(), e.getY()) != -1) {
				setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
			} else {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				for (CanvasElement element : elements) {
					if (element.contains(e.getX(), e.getY())) {
						editingElement = element;
						showEditDialog(element);
						return;
					}
				}
			}
		}

		private void showEditDialog(CanvasElement element) {
			String newText = JOptionPane.showInputDialog(CanvasPanel.this, "Edit element text:", element.text);
			if (newText != null && !newText.isEmpty()) {
				saveState();
				element.text = newText;
				redoStack.clear();
				repaint();
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_DELETE) {
				deleteSelected();
			} else if (e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown()) {
				undo();
			} else if (e.getKeyCode() == KeyEvent.VK_Y && e.isControlDown()) {
				redo();
			} else if (e.getKeyCode() == KeyEvent.VK_S && e.isControlDown()) {
				saveCanvas();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}

	private static class CanvasElement implements Serializable {
		private static final long serialVersionUID = 1L;
		public int x;
		public int y;
		public int width;
		public int height;
		public String text;
		public Color color;
		private static final int HANDLE_SIZE = 10;

		public CanvasElement(int x, int y, int width, int height, String text, Color color) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.text = text;
			this.color = color;
		}

		public boolean contains(int px, int py) {
			return px >= x && px <= x + width && py >= y && py <= y + height;
		}

		public int getResizeHandle(int px, int py) {
			// 0 = TL, 1 = TR, 2 = BL, 3 = BR
			if (Math.abs(px - (x + width)) < HANDLE_SIZE && Math.abs(py - (y + height)) < HANDLE_SIZE) {
				return 3;
			}
			return -1;
		}

		public void draw(Graphics2D g2d) {
			g2d.setColor(new Color(0, 0, 0, 20));
			g2d.fillRect(x + 2, y + 2, width, height);
			
			g2d.setColor(color);
			g2d.fillRect(x, y, width, height);
			
			g2d.setColor(new Color(50, 50, 50));
			g2d.setStroke(new BasicStroke(1.5f));
			g2d.drawRect(x, y, width, height);
			
			g2d.setColor(new Color(255, 255, 255));
			g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
			
			String[] lines = text.split("\n");
			int lineHeight = 16;
			for (int i = 0; i < lines.length && (y + 30 + (i * lineHeight)) < (y + height); i++) {
				g2d.drawString(lines[i], x + 10, y + 30 + (i * lineHeight));
			}
		}
	}

	/**
	 * Database handler for saving/loading canvas elements
	 */
	private static class CanvasDatabase {
		private java.util.Map<String, List<CanvasElement>> storage = new java.util.HashMap<>();

		public void saveCanvas(int userId, int projectId, List<CanvasElement> elements) {
			String key = userId + "_" + projectId;
			storage.put(key, deepCopyForStorage(elements));
		}

		public List<CanvasElement> loadCanvas(int userId, int projectId) {
			String key = userId + "_" + projectId;
			List<CanvasElement> stored = storage.get(key);
			if (stored != null) {
				return deepCopyForStorage(stored);
			}
			return null;
		}

		private List<CanvasElement> deepCopyForStorage(List<CanvasElement> original) {
			List<CanvasElement> copy = new ArrayList<>();
			for (CanvasElement elem : original) {
				copy.add(new CanvasElement(elem.x, elem.y, elem.width, elem.height, elem.text, new Color(elem.color.getRGB())));
			}
			return copy;
		}
	}

	private static class EditDialog extends JDialog {
		private static final long serialVersionUID = 1L;
		private JTextField textField;

		public EditDialog(JFrame parent, String initialText) {
			super(parent, "Edit Element", true);
			textField = new JTextField(initialText, 20);
			add(textField);
			setSize(400, 100);
			setLocationRelativeTo(parent);
		}

		public String getEditedText() {
			return textField.getText();
		}
	}
}
