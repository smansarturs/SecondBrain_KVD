package secondBrain.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import secondBrain.database.Database;
import secondBrain.service.Node;

public class NodeSearchService {
	private Database db;
	private final String TABLE_NAME = "nodes";

	public NodeSearchService() throws ClassNotFoundException, SQLException {
		db = new Database();
	}

	/**
	 * Searches nodes by text in title or content.
	 * 
	 * @param query     The search query string
	 * @param projectId The project ID to search within
	 * @return List of nodes matching the search query
	 * @throws SQLException
	 */
	public List<Node> searchByText(String searchText, int projectId) throws SQLException {
	    List<Node> results = new ArrayList<>();

	    if (searchText == null || searchText.trim().isEmpty() || projectId <= 0) {
	        return results;
	    }

	    Connection conn = this.db.getConn();
	    String query = "SELECT * FROM " + TABLE_NAME
	            + " WHERE project_id = ? AND (title LIKE ? OR content LIKE ?)";

	    PreparedStatement stmt = conn.prepareStatement(query);
	    stmt.setInt(1, projectId);
	    stmt.setString(2, "%" + searchText + "%");
	    stmt.setString(3, "%" + searchText + "%");
	    ResultSet rs = stmt.executeQuery();

	    while (rs.next()) {
	        Node node = new Node(rs.getInt("id"), rs.getInt("project_id"), rs.getString("title"),
	                rs.getString("content"), rs.getFloat("x_position"), rs.getFloat("y_position"),
	                rs.getTimestamp("created_at").getTime());
	        results.add(node);
	    }

	    return results;
	}

	/**
	 * Searches nodes by tags. Note: This assumes a tags table and node_tags
	 * junction table exist.
	 * 
	 * @param tags      List of tag names to search for
	 * @param projectId The project ID to search within
	 * @return List of nodes matching any of the provided tags
	 * @throws SQLException
	 */
	public List<Node> searchByTags(List<String> tags, int projectId) throws SQLException {
		List<Node> results = new ArrayList<>();

		if (tags == null || tags.isEmpty() || projectId <= 0) {
			return results;
		}

		Connection conn = this.db.getConn();

		StringBuilder placeholders = new StringBuilder();
		for (int i = 0; i < tags.size(); i++) {
			if (i > 0)
				placeholders.append(",");
			placeholders.append("?");
		}

		String sqlQuery = "SELECT DISTINCT n.id, n.project_id, n.title, n.content, n.x_position, n.y_position, n.created_at "
				+ "FROM " + TABLE_NAME + " n " + "INNER JOIN node_tags nt ON n.id = nt.node_id "
				+ "INNER JOIN tags t ON nt.tag_id = t.id " + "WHERE n.project_id = ? AND t.name IN (" + placeholders
				+ ")";

		try {
			PreparedStatement stmt = conn.prepareStatement(sqlQuery);
			stmt.setInt(1, projectId);

			for (int i = 0; i < tags.size(); i++) {
				stmt.setString(i + 2, tags.get(i));
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Node node = new Node(rs.getInt("id"), rs.getInt("project_id"), rs.getString("title"),
						rs.getString("content"), rs.getFloat("x_position"), rs.getFloat("y_position"),
						rs.getTimestamp("created_at").getTime());
				results.add(node);
			}
		} catch (SQLException e) {
			System.err.println("SQLException in searchByTags: " + e.getMessage());
			e.printStackTrace();
		}

		return results;
	}

	/**
	 * Searches nodes by associated files. Note: This assumes a files table and
	 * node_files junction table exist.
	 * 
	 * @param filename  The filename or file path to search for
	 * @param projectId The project ID to search within
	 * @return List of nodes associated with files matching the filename
	 * @throws SQLException
	 */
	public List<Node> searchByFiles(String filename, int projectId) throws SQLException {
		List<Node> results = new ArrayList<>();

		if (filename == null || filename.trim().isEmpty() || projectId <= 0) {
			return results;
		}

		Connection conn = this.db.getConn();
		String searchTerm = "%" + filename + "%";

		String sqlQuery = "SELECT DISTINCT n.id, n.project_id, n.title, n.content, n.x_position, n.y_position, n.created_at "
				+ "FROM " + TABLE_NAME + " n " + "INNER JOIN node_files nf ON n.id = nf.node_id "
				+ "INNER JOIN files f ON nf.file_id = f.id "
				+ "WHERE n.project_id = ? AND (f.filename LIKE ? OR f.file_path LIKE ?)";

		try {
			PreparedStatement stmt = conn.prepareStatement(sqlQuery);
			stmt.setInt(1, projectId);
			stmt.setString(2, searchTerm);
			stmt.setString(3, searchTerm);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Node node = new Node(rs.getInt("id"), rs.getInt("project_id"), rs.getString("title"),
						rs.getString("content"), rs.getFloat("x_position"), rs.getFloat("y_position"),
						rs.getTimestamp("created_at").getTime());
				results.add(node);
			}
		} catch (SQLException e) {
			System.err.println("SQLException in searchByFiles: " + e.getMessage());
			e.printStackTrace();
		}

		return results;
	}
}
