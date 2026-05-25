package secondBrain.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import secondBrain.database.Database;
import secondBrain.service.Node;

public class NodeService {
	private Database db;
	private final String TABLE_NAME = "nodes";

	public NodeService() throws ClassNotFoundException, SQLException {
		db = new Database();
	}

	/**
	 * Inserts a new node into the database with default position (0, 0).
	 * 
	 * @param title     The title of the node
	 * @param content   The content of the node
	 * @param projectId The project ID the node belongs to
	 * @return boolean TRUE if insertion was successful, FALSE otherwise
	 * @throws SQLException
	 */
	public boolean insert(String title, String content, int projectId) throws SQLException {
		return insert(title, content, projectId, 0.0f, 0.0f);
	}

	/**
	 * Inserts a new node into the database with specified position.
	 * 
	 * @param title      The title of the node
	 * @param content    The content of the node
	 * @param projectId  The project ID the node belongs to
	 * @param xPosition  The X coordinate position on canvas
	 * @param yPosition  The Y coordinate position on canvas
	 * @return boolean TRUE if insertion was successful, FALSE otherwise
	 * @throws SQLException
	 */
	public boolean insert(String title, String content, int projectId, float xPosition, float yPosition)
			throws SQLException {
		if (title == null || content == null || projectId <= 0) {
			System.err.println("Invalid parameters: title, content, or projectId");
			return false;
		}

		Connection conn = this.db.getConn();

		String query = "INSERT INTO " + TABLE_NAME + " (title, content, project_id, x_position, y_position, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
		PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		stmt.setString(1, title);
		stmt.setString(2, content);
		stmt.setInt(3, projectId);
		stmt.setFloat(4, xPosition);
		stmt.setFloat(5, yPosition);

		try {
			int rowsAffected = stmt.executeUpdate();
			boolean isInserted = rowsAffected > 0;
			System.out.println("Node insertion result: " + isInserted + " (rows affected: " + rowsAffected + ")");
			return isInserted;
		} catch (SQLException e) {
			System.err.println("SQL Error during insert: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Updates an existing node in the database.
	 * 
	 * @param nodeId  The ID of the node to update
	 * @param title   The new title
	 * @param content The new content
	 * @return boolean TRUE if update was successful, FALSE otherwise
	 * @throws SQLException
	 */
	public boolean update(int nodeId, String title, String content) throws SQLException {
		if (nodeId <= 0 || title == null || content == null) {
			return false;
		}

		Connection conn = this.db.getConn();

		String query = "UPDATE " + TABLE_NAME + " SET title = ?, content = ? WHERE id = ?";
		PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		stmt.setString(1, title);
		stmt.setString(2, content);
		stmt.setInt(3, nodeId);

		int rowsAffected = stmt.executeUpdate();
		return rowsAffected > 0;
	}

	/**
	 * Deletes a node from the database.
	 * 
	 * @param nodeId The ID of the node to delete
	 * @return boolean TRUE if deletion was successful, FALSE otherwise
	 * @throws SQLException
	 */
	public boolean delete(int nodeId) throws SQLException {
		if (nodeId <= 0) {
			return false;
		}

		Connection conn = this.db.getConn();

		String query = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
		PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		stmt.setInt(1, nodeId);

		int rowsAffected = stmt.executeUpdate();
		return rowsAffected > 0;
	}

	/**
	 * Search nodes by text in title or content
	 * 
	 * @param searchText The text to search for
	 * @param projectId  The project ID to search within
	 * @return List of Node objects matching the search
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

		try {
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
		} catch (SQLException e) {
			System.err.println("SQLException in searchByText: " + e.getMessage());
			e.printStackTrace();
		}

		return results;
	}

	/**
	 * Search nodes by tags
	 * 
	 * @param tags      List of tag names to search for
	 * @param projectId The project ID to search within
	 * @return List of Node objects matching the tags
	 * @throws SQLException
	 */
	public List<Node> searchByTags(List<String> tags, int projectId) throws SQLException {
		List<Node> results = new ArrayList<>();

		if (tags == null || tags.isEmpty() || projectId <= 0) {
			return results;
		}

		Connection conn = this.db.getConn();

		// Build dynamic SQL with placeholders for each tag
		StringBuilder placeholders = new StringBuilder();
		for (int i = 0; i < tags.size(); i++) {
			if (i > 0)
				placeholders.append(",");
			placeholders.append("?");
		}

		String query = "SELECT DISTINCT n.* FROM " + TABLE_NAME + " n "
				+ "INNER JOIN node_tags nt ON n.id = nt.node_id "
				+ "INNER JOIN tags t ON nt.tag_id = t.id "
				+ "WHERE n.project_id = ? AND t.name IN (" + placeholders.toString() + ")";

		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, projectId);

			// Set tag parameters
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
	 * Search nodes by associated files
	 * 
	 * @param filename  The filename to search for
	 * @param projectId The project ID to search within
	 * @return List of Node objects with associated files
	 * @throws SQLException
	 */
	public List<Node> searchByFiles(String filename, int projectId) throws SQLException {
		List<Node> results = new ArrayList<>();

		if (filename == null || filename.trim().isEmpty() || projectId <= 0) {
			return results;
		}

		Connection conn = this.db.getConn();
		String query = "SELECT DISTINCT n.* FROM " + TABLE_NAME + " n "
				+ "INNER JOIN node_files nf ON n.id = nf.node_id "
				+ "INNER JOIN files f ON nf.file_id = f.id "
				+ "WHERE n.project_id = ? AND (f.filename LIKE ? OR f.file_path LIKE ?)";

		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, projectId);
			stmt.setString(2, "%" + filename + "%");
			stmt.setString(3, "%" + filename + "%");
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

	/**
	 * Get a single node by ID
	 * 
	 * @param nodeId The ID of the node
	 * @return Node object or null if not found
	 * @throws SQLException
	 */
	public Node getNodeById(int nodeId) throws SQLException {
		if (nodeId <= 0) {
			return null;
		}

		Connection conn = this.db.getConn();
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, nodeId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return new Node(rs.getInt("id"), rs.getInt("project_id"), rs.getString("title"),
						rs.getString("content"), rs.getFloat("x_position"), rs.getFloat("y_position"),
						rs.getTimestamp("created_at").getTime());
			}
		} catch (SQLException e) {
			System.err.println("SQLException in getNodeById: " + e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Get all nodes in a project
	 * 
	 * @param projectId The project ID
	 * @return List of all nodes in the project
	 * @throws SQLException
	 */
	public List<Node> getAllNodesByProject(int projectId) throws SQLException {
		List<Node> results = new ArrayList<>();

		if (projectId <= 0) {
			return results;
		}

		Connection conn = this.db.getConn();
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE project_id = ?";

		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, projectId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Node node = new Node(rs.getInt("id"), rs.getInt("project_id"), rs.getString("title"),
						rs.getString("content"), rs.getFloat("x_position"), rs.getFloat("y_position"),
						rs.getTimestamp("created_at").getTime());
				results.add(node);
			}
		} catch (SQLException e) {
			System.err.println("SQLException in getAllNodesByProject: " + e.getMessage());
			e.printStackTrace();
		}

		return results;
	}
}
