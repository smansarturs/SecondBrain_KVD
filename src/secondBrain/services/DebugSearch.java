package secondBrain.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import secondBrain.database.Database;

public class DebugSearch {
    public static void debugSearch(String searchText, int projectId) {
        System.out.println("=== DebugSearch START ===");
        System.out.println("searchText  : '" + searchText + "'");
        System.out.println("projectId   : " + projectId);
 
        try {
            Database db = new Database();
            Connection conn = db.getConn();
            System.out.println("DB connected : " + (conn != null && !conn.isClosed()));
 
            // 1. How many nodes exist in total?
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM nodes");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) System.out.println("Total nodes in DB : " + rs.getInt(1));
 
            // 2. How many nodes for this projectId?
            ps = conn.prepareStatement("SELECT COUNT(*) FROM nodes WHERE project_id = ?");
            ps.setInt(1, projectId);
            rs = ps.executeQuery();
            if (rs.next()) System.out.println("Nodes for project " + projectId + " : " + rs.getInt(1));
 
            // 3. Print all nodes for this project (title + content type info)
            ps = conn.prepareStatement(
                "SELECT id, title, content, project_id FROM nodes WHERE project_id = ?");
            ps.setInt(1, projectId);
            rs = ps.executeQuery();
            System.out.println("--- Nodes for project " + projectId + " ---");
            while (rs.next()) {
                Object contentObj = rs.getObject("content");
                String contentType = contentObj == null ? "null" : contentObj.getClass().getSimpleName();
                String contentVal  = contentObj == null ? "null"
                    : (contentObj instanceof byte[] ? new String((byte[]) contentObj) : contentObj.toString());
                System.out.println("  id=" + rs.getInt("id")
                    + " title='" + rs.getString("title") + "'"
                    + " content_type=" + contentType
                    + " content='" + contentVal + "'");
            }
 
            // 4. Try the LIKE query with CAST
            String pattern = "%" + searchText + "%";
            ps = conn.prepareStatement(
                "SELECT id, title FROM nodes WHERE project_id = ? "
                + "AND (title LIKE ? OR CAST(content AS CHAR) LIKE ?)");
            ps.setInt(1, projectId);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            rs = ps.executeQuery();
            System.out.println("--- CAST LIKE results ---");
            int count = 0;
            while (rs.next()) {
                System.out.println("  MATCH id=" + rs.getInt("id") + " title='" + rs.getString("title") + "'");
                count++;
            }
            if (count == 0) System.out.println("  No matches with CAST LIKE");
 
            // 5. Try without project_id filter (in case projectId is wrong)
            ps = conn.prepareStatement(
                "SELECT id, title, project_id FROM nodes WHERE title LIKE ?");
            ps.setString(1, pattern);
            rs = ps.executeQuery();
            System.out.println("--- Title LIKE (no project filter) ---");
            count = 0;
            while (rs.next()) {
                System.out.println("  id=" + rs.getInt("id")
                    + " title='" + rs.getString("title") + "'"
                    + " project_id=" + rs.getInt("project_id"));
                count++;
            }
            if (count == 0) System.out.println("  No matches even without project filter");
 
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("DebugSearch ERROR: " + e.getMessage());
            e.printStackTrace();
        }
 
        System.out.println("=== DebugSearch END ===");
    }
}
