package secondBrain.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import secondBrain.data.Node;
import secondBrain.database.Database;

public class NodeService {
	private Database db;
	private final String TABLE_NAME = "nodes";

	public NodeService() throws ClassNotFoundException, SQLException {
		db = new Database();
	}


	public boolean insert(String title, String content, int projectId) throws SQLException {
		return insert(0, title, content, projectId, 0.0f, 0.0f);
	}


	public boolean insert(int userId, String title, String content, int projectId) throws SQLException {
		return insert(userId, title, content, projectId, 0.0f, 0.0f);
	}


	public boolean insert(String title, String content, int projectId, float xPosition, float yPosition)
			throws SQLException {
		return insert(0, title, content, projectId, xPosition, yPosition);
	}


	public boolean insert(int userId, String title, String content, int projectId, float xPosition, float yPosition)
			throws SQLException {
		if (title == null || content == null || projectId <= 0) {
			System.err.println("Invalid parameters: title, content, or projectId");
			return false;
		}

		Connection conn = this.db.getConn();
		String query = "INSERT INTO " + TABLE_NAME
				+ " (title, content, project_id, user_id, x_position, y_position, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())";
		PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		stmt.setString(1, title);
		stmt.setString(2, content);
		stmt.setInt(3, projectId);
		stmt.setInt(4, userId);
		stmt.setFloat(5, xPosition);
		stmt.setFloat(6, yPosition);

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
	 * Searches nodes by title or content using a partial LIKE match.
	 *
	 * @param searchText the text to search for (partial match supported)
	 * @param projectId  the project to search within
	 * @return list of matching Node objects
	 */
	public List<Node> searchByText(String searchText, int projectId, int userId) throws SQLException {
		List<Node> results = new ArrayList<>();

		if (searchText == null || searchText.trim().isEmpty() || projectId <= 0) {
			return results;
		}

		Connection conn = this.db.getConn();

		String query = "SELECT id, title, content, project_id, x_position, y_position, created_at "
				+ "FROM " + TABLE_NAME + " "
				+ "WHERE project_id = ? "
				+ "AND (title LIKE ? OR content LIKE ?) AND user_id = ?";

		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			String pattern = "%" + searchText.trim() + "%";
			stmt.setInt(1, projectId);
			stmt.setString(2, pattern);
			stmt.setString(3, pattern);
			stmt.setInt(4, userId);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Node node = mapRow(rs);
				results.add(node);
				System.out.println("Found node: " + node.getId() + " - " + node.getTitle());
			}

		} catch (SQLException e) {
			System.err.println("SQLException in searchByText: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}

		return results;
	}

	/**
	 * Searches nodes by tags (matches against title or content containing the tag).
	 * Tags are comma-separated words matched individually.
	 *
	 * @param tags      list of tag strings to search for
	 * @param projectId the project to search within
	 * @return list of matching Node objects
	 */
	public List<Node> searchByTags(List<String> tags, int projectId) throws SQLException {
		List<Node> results = new ArrayList<>();

		if (tags == null || tags.isEmpty() || projectId <= 0) {
			return results;
		}

		Connection conn = this.db.getConn();

		StringBuilder query = new StringBuilder(
				"SELECT id, title, content, project_id, x_position, y_position, created_at "
				+ "FROM " + TABLE_NAME + " "
				+ "WHERE project_id = ? AND (");

		for (int i = 0; i < tags.size(); i++) {
			if (i > 0) query.append(" OR ");
			query.append("title LIKE ? OR content LIKE ?");
		}
		query.append(")");

		try {
			PreparedStatement stmt = conn.prepareStatement(query.toString());
			stmt.setInt(1, projectId);

			int paramIndex = 2;
			for (String tag : tags) {
				String pattern = "%" + tag.trim() + "%";
				stmt.setString(paramIndex++, pattern);
				stmt.setString(paramIndex++, pattern);
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				results.add(mapRow(rs));
			}

		} catch (SQLException e) {
			System.err.println("SQLException in searchByTags: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}

		return results;
	}

	/**
	 * Searches nodes by filename mentioned in their content.
	 *
	 * @param fileName  the file name to search for
	 * @param projectId the project to search within
	 * @return list of matching Node objects
	 */
	public List<Node> searchByFiles(String fileName, int projectId) throws SQLException {
		List<Node> results = new ArrayList<>();

		if (fileName == null || fileName.trim().isEmpty() || projectId <= 0) {
			return results;
		}

		Connection conn = this.db.getConn();
		String query = "SELECT id, title, content, project_id, x_position, y_position, created_at "
				+ "FROM " + TABLE_NAME + " "
				+ "WHERE project_id = ? AND content LIKE ?";

		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, projectId);
			stmt.setString(2, "%" + fileName.trim() + "%");

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				results.add(mapRow(rs));
			}

		} catch (SQLException e) {
			System.err.println("SQLException in searchByFiles: " + e.getMessage());
			e.printStackTrace();
			throw e;
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