package secondBrain.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import secondBrain.data.Node;
import secondBrain.database.Database;

public class NodeSearchService {
    private Database db;
    private final String TABLE_NAME = "nodes";

    public NodeSearchService() throws ClassNotFoundException, SQLException {
        db = new Database();
    }

    /**
     * Searches nodes by text in title or content.
     * Uses CAST(content AS CHAR) because the content column is stored as BLOB,
     * which does not support LIKE string matching without explicit casting.
     */
    public List<Node> searchByText(String searchText, int projectId) throws SQLException {
        List<Node> results = new ArrayList<>();

        if (searchText == null || searchText.trim().isEmpty() || projectId <= 0) {
            return results;
        }

        Connection conn = this.db.getConn();

        // CAST(content AS CHAR) converts BLOB to string so LIKE works correctly
        String query = "SELECT id, user_id, project_id, title, CAST(content AS CHAR) AS content, "
                + "x_position, y_position, created_at "
                + "FROM " + TABLE_NAME + " "
                + "WHERE project_id = ? "
                + "AND (title LIKE ? OR content LIKE ?)";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            String pattern = "%" + searchText.trim() + "%";
            stmt.setInt(1, projectId);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(mapRow(rs));
                System.out.println("Found: " + rs.getString("title"));
            }
        } catch (SQLException e) {
            System.err.println("SQLException in searchByText: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return results;
    }

    /**
     * Searches nodes by tags via node_tags junction table.
     */
    public List<Node> searchByTags(List<String> tags, int projectId) throws SQLException {
        List<Node> results = new ArrayList<>();

        if (tags == null || tags.isEmpty() || projectId <= 0) {
            return results;
        }

        Connection conn = this.db.getConn();

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }

        String sqlQuery = "SELECT DISTINCT n.id, n.user_id, n.project_id, n.title, "
                + "CAST(n.content AS CHAR) AS content, n.x_position, n.y_position, n.created_at "
                + "FROM " + TABLE_NAME + " n "
                + "INNER JOIN node_tags nt ON n.id = nt.node_id "
                + "INNER JOIN tags t ON nt.tag_id = t.id "
                + "WHERE n.project_id = ? AND t.name IN (" + placeholders + ")";

        try {
            PreparedStatement stmt = conn.prepareStatement(sqlQuery);
            stmt.setInt(1, projectId);
            for (int i = 0; i < tags.size(); i++) {
                stmt.setString(i + 2, tags.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) results.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("SQLException in searchByTags: " + e.getMessage());
            e.printStackTrace();
        }

        return results;
    }

    /**
     * Searches nodes by associated filename via node_files junction table.
     */
    public List<Node> searchByFiles(String filename, int projectId) throws SQLException {
        List<Node> results = new ArrayList<>();

        if (filename == null || filename.trim().isEmpty() || projectId <= 0) {
            return results;
        }

        Connection conn = this.db.getConn();
        String searchTerm = "%" + filename.trim() + "%";

        String sqlQuery = "SELECT DISTINCT n.id, n.user_id, n.project_id, n.title, "
                + "CAST(n.content AS CHAR) AS content, n.x_position, n.y_position, n.created_at "
                + "FROM " + TABLE_NAME + " n "
                + "INNER JOIN node_files nf ON n.id = nf.node_id "
                + "INNER JOIN files f ON nf.file_id = f.id "
                + "WHERE n.project_id = ? AND (f.filename LIKE ? OR f.file_path LIKE ?)";

        try {
            PreparedStatement stmt = conn.prepareStatement(sqlQuery);
            stmt.setInt(1, projectId);
            stmt.setString(2, searchTerm);
            stmt.setString(3, searchTerm);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) results.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("SQLException in searchByFiles: " + e.getMessage());
            e.printStackTrace();
        }

        return results;
    }

    private Node mapRow(ResultSet rs) throws SQLException {
        return new Node(
            rs.getInt("id"),
            rs.getInt("project_id"),
            rs.getString("title"),
            rs.getString("content"),
            rs.getFloat("x_position"),
            rs.getFloat("y_position"),
            rs.getTimestamp("created_at").getTime(),
            rs.getInt("user_id")
        );
    }
}