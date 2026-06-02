package secondBrain.mainFunctions;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.SystemColor;
import com.jgoodies.forms.factories.DefaultComponentFactory;

public class CanvasView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private CanvasPanel canvasPanel;
	private int userId;
	private int projectId;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CanvasView frame = new CanvasView();
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
	    
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setBounds(100, 100, 800, 600);
	    setTitle("SecondBrain - Canvas View");
	    
	    contentPane = new JPanel();
	    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	    setContentPane(contentPane);
	    contentPane.setLayout(null);
	    
	    JLabel lblTitle = new JLabel("Canvas view");
	    lblTitle.setForeground(SystemColor.textHighlight);
	    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
	    lblTitle.setBounds(10, 10, 105, 22);
	    contentPane.add(lblTitle);
	    
	    canvasPanel = new CanvasPanel();
	    canvasPanel.setBounds(10, 40, 770, 530);
	    contentPane.add(canvasPanel);
	}

	
	private static class CanvasPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			drawGrid(g2d);
			
			drawContent(g2d);
		}

		private void drawGrid(Graphics2D g2d) {
			int gridSize = 20;
			g2d.setColor(new java.awt.Color(230, 230, 230));
			
			for (int i = 0; i < getWidth(); i += gridSize) {
				g2d.drawLine(i, 0, i, getHeight());
			}
			for (int i = 0; i < getHeight(); i += gridSize) {
				g2d.drawLine(0, i, getWidth(), i);
			}
		}

		private void drawContent(Graphics2D g2d) {
			g2d.setColor(new java.awt.Color(100, 150, 255));
			g2d.fillRect(50, 50, 200, 150);
			
			g2d.setColor(new java.awt.Color(0, 0, 0));
			g2d.drawString("Your canvas content goes here", 60, 70);
		}
	}
}
