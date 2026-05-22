package secondBrain.mainFunctions;

import secondBrain.database.Database;
import secondBrain.services.ProjectService;
import secondBrain.services.ProjectService.Project;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.CubicCurve2D;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Interactive node-graph Connection Editor.
 *
 * Controls
 * ─────────────────────────────────────────────
 *  Left-click on empty canvas → create a new node (shows title dialog)
 *  Left-drag on a node        → move the node (position saved on release)
 *  Drag from output port (●)  → draw a new connection to another node
 *  Right-click on a node      → context menu: connect from here / delete node
 *  Right-click on a curve     → context menu: delete connection
 *  Project combo (top bar)    → switch project; canvas reloads from DB
 */
public class ConnectionEditor extends JFrame {

    private static final long serialVersionUID = 1L;

    private final int userId;
    private int projectId;

    private JComboBox<ProjectItem> cmbProject;
    private JLabel lblStatus;

    // Canvas
    private NodeCanvas canvas;

    private final List<NodeModel>       nodes       = new ArrayList<>();
    private final List<ConnectionModel> connections = new ArrayList<>();

    private NodeModel  draggedNode    = null;
    private int        dragOffX, dragOffY;
    private NodeModel  connectingFrom = null;
    private Point      connectingMouse = null;
    private NodeModel  hoveredNode    = null;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try { new ConnectionEditor(1, 0).setVisible(true); }
            catch (Exception e) { e.printStackTrace(); }
        });
    }

    public ConnectionEditor(int userId, int projectId) {
        this.userId    = userId;
        this.projectId = projectId;

        setTitle("SecondBrain – Connection Editor");
        setSize(1000, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        buildTopBar();
        canvas = new NodeCanvas();
        add(canvas, BorderLayout.CENTER);
        buildBottomBar();

        loadProjects();
    }


    private void buildTopBar() {
        JPanel top = new JPanel(null);
        top.setPreferredSize(new Dimension(0, 46));
        top.setBackground(new Color(30, 30, 30));

        JLabel title = new JLabel("Connection Editor");
        title.setForeground(new Color(0, 120, 215));
        title.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        title.setBounds(12, 10, 220, 26);
        top.add(title);

        JLabel lblProj = new JLabel("Project:");
        lblProj.setForeground(Color.LIGHT_GRAY);
        lblProj.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblProj.setBounds(240, 14, 55, 20);
        top.add(lblProj);

        cmbProject = new JComboBox<>();
        cmbProject.setBounds(298, 12, 260, 24);
        cmbProject.addActionListener(e -> onProjectChanged());
        top.add(cmbProject);

        JLabel legend = new JLabel(
            "Left-click canvas = new node   |   Drag node = move   |   Right-click = options   |   Drag ● = connect");
        legend.setForeground(new Color(110, 110, 110));
        legend.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        legend.setBounds(570, 14, 420, 20);
        top.add(legend);

        add(top, BorderLayout.NORTH);
    }

    private void buildBottomBar() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        bottom.setBackground(new Color(30, 30, 30));
        bottom.setPreferredSize(new Dimension(0, 28));
        lblStatus = new JLabel("Ready");
        lblStatus.setForeground(new Color(150, 150, 150));
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        bottom.add(lblStatus);
        add(bottom, BorderLayout.SOUTH);
    }

    private void setStatus(String msg) {
        SwingUtilities.invokeLater(() -> lblStatus.setText(msg));
    }


    private void loadProjects() {
        cmbProject.removeAllItems();
        try {
            ProjectService ps = new ProjectService();
            List<Project> list = ps.selectProjectsByUserId(userId);
            for (Project p : list) cmbProject.addItem(new ProjectItem(p.getId(), p.getName()));
            for (int i = 0; i < cmbProject.getItemCount(); i++) {
                if (cmbProject.getItemAt(i).id == projectId) { cmbProject.setSelectedIndex(i); break; }
            }
        } catch (Exception ex) {
            setStatus("Error loading projects: " + ex.getMessage());
            ex.printStackTrace();
        }
        onProjectChanged();
    }

    private void onProjectChanged() {
        ProjectItem sel = (ProjectItem) cmbProject.getSelectedItem();
        if (sel == null) return;
        projectId = sel.id;
        loadNodesAndConnections();
    }


    private void loadNodesAndConnections() {
        nodes.clear();
        connections.clear();
        try {
            Database db = new Database();
            Connection conn = db.getConn();

            PreparedStatement ps = conn.prepareStatement(
                "SELECT id, title, x_position, y_position FROM nodes WHERE project_id = ?");
            ps.setInt(1, projectId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                nodes.add(new NodeModel(rs.getInt("id"), rs.getString("title"),
                    (int) rs.getFloat("x_position"), (int) rs.getFloat("y_position")));
            }

            ps = conn.prepareStatement(
                "SELECT c.id, c.from_node_id, c.to_node_id, c.type " +
                "FROM connections c JOIN nodes n ON c.from_node_id = n.id " +
                "WHERE n.project_id = ?");
            ps.setInt(1, projectId);
            rs = ps.executeQuery();
            while (rs.next()) {
                NodeModel from = findNodeById(rs.getInt("from_node_id"));
                NodeModel to   = findNodeById(rs.getInt("to_node_id"));
                if (from != null && to != null)
                    connections.add(new ConnectionModel(rs.getInt("id"), from, to, rs.getString("type")));
            }

            setStatus(nodes.size() + " nodes, " + connections.size() + " connections.");
        } catch (Exception ex) {
            setStatus("DB error: " + ex.getMessage());
            ex.printStackTrace();
        }
        canvas.repaint();
    }


    private int dbInsertNode(String title, int x, int y) {
        try {
            Database db = new Database();
            Connection conn = db.getConn();
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO nodes (title, content, project_id, x_position, y_position, created_at) " +
                "VALUES (?, '', ?, ?, ?, NOW())", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title); ps.setInt(2, projectId);
            ps.setFloat(3, x); ps.setFloat(4, y);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (Exception ex) { setStatus("Error creating node: " + ex.getMessage()); ex.printStackTrace(); }
        return -1;
    }

    private void dbUpdateNodePosition(NodeModel n) {
        try {
            Database db = new Database();
            PreparedStatement ps = db.getConn().prepareStatement(
                "UPDATE nodes SET x_position = ?, y_position = ? WHERE id = ?");
            ps.setFloat(1, n.x); ps.setFloat(2, n.y); ps.setInt(3, n.id);
            ps.executeUpdate();
        } catch (Exception ex) { setStatus("Error saving position: " + ex.getMessage()); }
    }

    private void dbDeleteNode(NodeModel n) {
        try {
            Database db = new Database();
            Connection conn = db.getConn();
            PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM connections WHERE from_node_id = ? OR to_node_id = ?");
            ps.setInt(1, n.id); ps.setInt(2, n.id); ps.executeUpdate();
            ps = conn.prepareStatement("DELETE FROM nodes WHERE id = ?");
            ps.setInt(1, n.id); ps.executeUpdate();
        } catch (Exception ex) { setStatus("Error deleting node: " + ex.getMessage()); }
    }

    private int dbInsertConnection(int fromId, int toId, String type) {
        try {
            Database db = new Database();
            PreparedStatement ps = db.getConn().prepareStatement(
                "INSERT INTO connections (from_node_id, to_node_id, type, created_at) VALUES (?,?,?,NOW())",
                Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, fromId); ps.setInt(2, toId); ps.setString(3, type);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (Exception ex) { setStatus("Error creating connection: " + ex.getMessage()); ex.printStackTrace(); }
        return -1;
    }

    private void dbDeleteConnection(ConnectionModel c) {
        try {
            Database db = new Database();
            PreparedStatement ps = db.getConn().prepareStatement("DELETE FROM connections WHERE id = ?");
            ps.setInt(1, c.id); ps.executeUpdate();
        } catch (Exception ex) { setStatus("Error deleting connection: " + ex.getMessage()); }
    }


    private NodeModel findNodeById(int id) {
        for (NodeModel n : nodes) if (n.id == id) return n;
        return null;
    }

    private NodeModel nodeAt(int mx, int my) {
        for (int i = nodes.size() - 1; i >= 0; i--) {
            NodeModel n = nodes.get(i);
            if (mx >= n.x && mx <= n.x + NodeModel.W && my >= n.y && my <= n.y + NodeModel.H) return n;
        }
        return null;
    }

    private ConnectionModel connectionNear(int mx, int my) {
        for (ConnectionModel c : connections) if (curveContains(c, mx, my)) return c;
        return null;
    }

    private boolean curveContains(ConnectionModel c, int mx, int my) {
        double x1 = c.from.x + NodeModel.W, y1 = c.from.y + NodeModel.H / 2.0;
        double x2 = c.to.x,                 y2 = c.to.y   + NodeModel.H / 2.0;
        double ctrl = Math.abs(x2 - x1) / 2.0;
        for (int t = 0; t <= 20; t++) {
            double u = t / 20.0;
            double px = cubicBezier(u, x1, x1 + ctrl, x2 - ctrl, x2);
            double py = cubicBezier(u, y1, y1, y2, y2);
            if (Math.hypot(px - mx, py - my) < 8) return true;
        }
        return false;
    }

    private double cubicBezier(double t, double p0, double p1, double p2, double p3) {
        double u = 1 - t;
        return u*u*u*p0 + 3*u*u*t*p1 + 3*u*t*t*p2 + t*t*t*p3;
    }

    private Point outputPort(NodeModel n) { return new Point(n.x + NodeModel.W, n.y + NodeModel.H / 2); }
    private Point inputPort(NodeModel n)  { return new Point(n.x,               n.y + NodeModel.H / 2); }

    private boolean nearOutputPort(int mx, int my, NodeModel n) {
        Point p = outputPort(n);
        return Math.hypot(mx - p.x, my - p.y) <= 10;
    }

    class NodeCanvas extends JPanel {

        NodeCanvas() {
            setBackground(new Color(36, 36, 36));

            MouseAdapter ma = new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    int mx = e.getX(), my = e.getY();
                    NodeModel hit = nodeAt(mx, my);

                    if (SwingUtilities.isRightMouseButton(e)) {
                        if (hit != null) showNodeContextMenu(hit, mx, my);
                        else {
                            ConnectionModel conn = connectionNear(mx, my);
                            if (conn != null) showConnectionContextMenu(conn, mx, my);
                        }
                        return;
                    }

                    if (hit != null) {
                        if (nearOutputPort(mx, my, hit)) {
                            connectingFrom = hit;
                            connectingMouse = new Point(mx, my);
                        } else {
                            draggedNode = hit;
                            dragOffX = mx - hit.x;
                            dragOffY = my - hit.y;
                        }
                    } else {
                        createNodeAt(mx, my);
                    }
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    int mx = e.getX(), my = e.getY();
                    if (draggedNode != null) {
                        draggedNode.x = mx - dragOffX;
                        draggedNode.y = my - dragOffY;
                        repaint();
                    } else if (connectingFrom != null) {
                        connectingMouse = new Point(mx, my);
                        repaint();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    int mx = e.getX(), my = e.getY();
                    if (draggedNode != null) {
                        dbUpdateNodePosition(draggedNode);
                        setStatus("Moved \u201c" + draggedNode.title + "\u201d");
                        draggedNode = null;
                    }
                    if (connectingFrom != null) {
                        NodeModel target = nodeAt(mx, my);
                        if (target != null && target != connectingFrom) finishConnection(connectingFrom, target);
                        connectingFrom = null;
                        connectingMouse = null;
                        repaint();
                    }
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    NodeModel prev = hoveredNode;
                    hoveredNode = nodeAt(e.getX(), e.getY());
                    if (hoveredNode != prev) repaint();
                    if (hoveredNode != null && nearOutputPort(e.getX(), e.getY(), hoveredNode))
                        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    else
                        setCursor(Cursor.getDefaultCursor());
                }
            };
            addMouseListener(ma);
            addMouseMotionListener(ma);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            drawGrid(g2);
            for (ConnectionModel c : connections) drawConnection(g2, c);
            if (connectingFrom != null && connectingMouse != null)
                drawLiveEdge(g2, outputPort(connectingFrom), connectingMouse);
            for (NodeModel n : nodes) drawNode(g2, n);
        }


        private void drawGrid(Graphics2D g2) {
            g2.setColor(new Color(48, 48, 48));
            g2.setStroke(new BasicStroke(0.5f));
            int gap = 40;
            for (int x = 0; x < getWidth();  x += gap) g2.drawLine(x, 0, x, getHeight());
            for (int y = 0; y < getHeight(); y += gap)  g2.drawLine(0, y, getWidth(), y);
        }

        private void drawNode(Graphics2D g2, NodeModel n) {
            boolean hov = n == hoveredNode;
            boolean drg = n == draggedNode;

            g2.setColor(new Color(0, 0, 0, 55));
            g2.fillRoundRect(n.x + 4, n.y + 4, NodeModel.W, NodeModel.H, 12, 12);

            Color body = drg ? new Color(70, 90, 120) : hov ? new Color(62, 72, 92) : new Color(50, 56, 70);
            g2.setColor(body);
            g2.fillRoundRect(n.x, n.y, NodeModel.W, NodeModel.H, 12, 12);

            g2.setStroke(new BasicStroke(hov ? 1.8f : 1f));
            g2.setColor(hov ? new Color(0, 150, 255) : new Color(75, 88, 108));
            g2.drawRoundRect(n.x, n.y, NodeModel.W, NodeModel.H, 12, 12);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            String label = n.title.length() > 16 ? n.title.substring(0, 14) + "\u2026" : n.title;
            g2.drawString(label,
                n.x + (NodeModel.W - fm.stringWidth(label)) / 2,
                n.y + (NodeModel.H + fm.getAscent() - fm.getDescent()) / 2);

            drawPort(g2, outputPort(n), hov);
            drawPort(g2, inputPort(n),  false);
        }

        private void drawPort(Graphics2D g2, Point p, boolean hi) {
            int r = 5;
            g2.setColor(hi ? new Color(0, 180, 255) : new Color(90, 110, 140));
            g2.fillOval(p.x - r, p.y - r, r * 2, r * 2);
            g2.setStroke(new BasicStroke(1));
            g2.setColor(new Color(190, 200, 215));
            g2.drawOval(p.x - r, p.y - r, r * 2, r * 2);
        }

        private void drawConnection(Graphics2D g2, ConnectionModel c) {
            Point src  = outputPort(c.from);
            Point dest = inputPort(c.to);
            drawCurve(g2, src.x, src.y, dest.x, dest.y, new Color(0, 120, 215), 2f, false);

            if (c.type != null && !c.type.isEmpty()) {
                double ctrl = Math.abs(dest.x - (double)src.x) / 2.0;
                double midX = cubicBezier(0.5, src.x, src.x + ctrl, dest.x - ctrl, dest.x);
                double midY = cubicBezier(0.5, src.y, src.y, dest.y, dest.y);
                g2.setFont(new Font("Segoe UI", Font.ITALIC, 10));
                g2.setColor(new Color(130, 165, 210));
                g2.drawString(c.type, (int) midX - 15, (int) midY - 7);
            }

            drawArrow(g2, src, dest, new Color(0, 120, 215));
        }

        private void drawLiveEdge(Graphics2D g2, Point src, Point dest) {
            drawCurve(g2, src.x, src.y, dest.x, dest.y, new Color(0, 200, 100), 1.5f, true);
        }

        private void drawCurve(Graphics2D g2, int x1, int y1, int x2, int y2, Color color, float stroke, boolean dashed) {
            double ctrl = Math.abs(x2 - x1) / 2.0;
            CubicCurve2D curve = new CubicCurve2D.Double (x1, y1, x1 + ctrl, y1, x2 - ctrl, y2, x2, y2);
            g2.setStroke(dashed
                ? new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[]{6,4}, 0)
                : new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(color);
            g2.draw(curve);
        }

        private void drawArrow(Graphics2D g2, Point from, Point to, Color color) {
            double angle = Math.atan2(to.y - from.y, to.x - from.x);
            int len = 10;
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(to.x, to.y, (int)(to.x - len * Math.cos(angle - 0.4)), (int)(to.y - len * Math.sin(angle - 0.4)));
            g2.drawLine(to.x, to.y, (int)(to.x - len * Math.cos(angle + 0.4)), (int)(to.y - len * Math.sin(angle + 0.4)));
        }
    }


    private void showNodeContextMenu(NodeModel n, int mx, int my) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem miConn = new JMenuItem("Draw connection from here\u2026");
        miConn.addActionListener(e -> {
            connectingFrom = n; connectingMouse = new Point(mx, my);
            setStatus("Click a target node to finish the connection.");
            canvas.repaint();
        });
        menu.add(miConn);
        menu.addSeparator();
        JMenuItem miDel = new JMenuItem("Delete node");
        miDel.setForeground(Color.RED);
        miDel.addActionListener(e -> deleteNode(n));
        menu.add(miDel);
        menu.show(canvas, mx, my);
    }

    private void showConnectionContextMenu(ConnectionModel c, int mx, int my) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem miDel = new JMenuItem(
            "Delete: " + c.from.title + " \u2192 " + c.to.title + " (" + c.type + ")");
        miDel.setForeground(Color.RED);
        miDel.addActionListener(e -> deleteConnection(c));
        menu.add(miDel);
        menu.show(canvas, mx, my);
    }


    private void createNodeAt(int x, int y) {
        if (projectId <= 0) { setStatus("No project selected."); return; }
        String title = JOptionPane.showInputDialog(this, "Node title:", "New Node", JOptionPane.PLAIN_MESSAGE);
        if (title == null || title.trim().isEmpty()) return;
        int id = dbInsertNode(title.trim(), x, y);
        if (id > 0) {
            nodes.add(new NodeModel(id, title.trim(), x, y));
            setStatus("Node \u201c" + title + "\u201d created.");
            canvas.repaint();
        }
    }

    private void deleteNode(NodeModel n) {
        int ok = JOptionPane.showConfirmDialog(this,
            "Delete \u201c" + n.title + "\u201d and all its connections?",
            "Delete Node", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;
        dbDeleteNode(n);
        nodes.remove(n);
        connections.removeIf(c -> c.from == n || c.to == n);
        setStatus("Node \u201c" + n.title + "\u201d deleted.");
        canvas.repaint();
    }

    private void finishConnection(NodeModel from, NodeModel to) {
        for (ConnectionModel c : connections) {
            if (c.from == from && c.to == to) { setStatus("Connection already exists."); return; }
        }
        String[] types = {"related", "depends_on", "leads_to", "blocks", "references", "custom"};
        String type = (String) JOptionPane.showInputDialog(
            this, "Connection type:", "New Connection",
            JOptionPane.PLAIN_MESSAGE, null, types, types[0]);
        if (type == null) return;
        int id = dbInsertConnection(from.id, to.id, type);
        if (id > 0) {
            connections.add(new ConnectionModel(id, from, to, type));
            setStatus("Connected: " + from.title + " \u2192 " + to.title);
            canvas.repaint();
        }
    }

    private void deleteConnection(ConnectionModel c) {
        dbDeleteConnection(c);
        connections.remove(c);
        setStatus("Connection deleted.");
        canvas.repaint();
    }


    static class NodeModel {
        static final int W = 130, H = 44;
        int id, x, y;
        String title;
        NodeModel(int id, String title, int x, int y) {
            this.id = id; this.title = title; this.x = x; this.y = y;
        }
    }

    static class ConnectionModel {
        int id; NodeModel from, to; String type;
        ConnectionModel(int id, NodeModel from, NodeModel to, String type) {
            this.id = id; this.from = from; this.to = to; this.type = type;
        }
    }

    static class ProjectItem {
        final int id; final String name;
        ProjectItem(int id, String name) { this.id = id; this.name = name; }
        @Override 
        public String toString() { 
        	return name;
        }
    }
}