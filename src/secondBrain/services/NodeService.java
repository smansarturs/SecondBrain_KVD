package secondBrain.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import secondBrain.database.Database;

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
}
