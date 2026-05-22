package secondBrain.mainFunctions;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.util.ArrayList;
import java.util.List;

public class ConnectionEditor extends JFrame {

    public ConnectionEditor() {
        setTitle("Second Brain - Connection Editor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        NodeCanvas canvas = new NodeCanvas();
        add(canvas);
        
        setLocationRelativeTo(null);
    }

    class NodeCanvas extends JPanel {
        
        public NodeCanvas() {
            setBackground(new Color(45, 45, 45));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Point nodeA = new Point(100, 100);
            Point nodeB = new Point(400, 300);

            drawNode(g2, nodeA.x, nodeA.y, "Thought A");
            drawNode(g2, nodeB.x, nodeB.y, "Thought B");
            
            drawConnection(g2, nodeA, nodeB);
        }

        private void drawNode(Graphics2D g2, int x, int y, String title) {
            g2.setColor(new Color(60, 63, 65));
            g2.fillRoundRect(x, y, 120, 50, 10, 10);
            g2.setColor(Color.WHITE);
            g2.drawString(title, x + 10, y + 30);
        }

        private void drawConnection(Graphics2D g2, Point start, Point end) {
        	
            double x1 = start.x + 120;
            double y1 = start.y + 25;
            double x2 = end.x;
            double y2 = end.y + 25;

            double ctrlOffset = Math.abs(x2 - x1) / 2;
            
            CubicCurve2D curve = new CubicCurve2D.Double(
                x1, y1,             
                x1 + ctrlOffset, y1,
                x2 - ctrlOffset, y2,
                x2, y2              
            );

            g2.setStroke(new BasicStroke(2));
            g2.setColor(new Color(0, 120, 215));
            g2.draw(curve);
        }
    }
}