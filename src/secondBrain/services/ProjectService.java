package secondBrain.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import secondBrain.database.Database;
import secondBrain.service.Project;

public class ProjectService {
	private Database db;
	private final String TABLE_NAME = "projects";

	public ProjectService() throws ClassNotFoundException, SQLException {
		db = new Database();
	}

	/**
	 * Creates a new project for a user
	 * 
	 * @param userId the ID of the user who owns the project
	 * @param name   the name of the project
	 * @return the ID of the created project, or -1 if creation failed
	 * @throws SQLException
	 */
	public int insert(int userId, String name) throws SQLException {
		if (userId <= 0 || name == null || name.trim().isEmpty()) {
			System.err.println("Invalid parameters: userId=" + userId + ", name=" + name);
			return -1;
		}

		Connection conn = this.db.getConn();
		String query = "INSERT INTO " + TABLE_NAME + " (user_id, name) VALUES (?, ?)";

		try {
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, userId);
			stmt.setString(2, name);

			int affectedRows = stmt.executeUpdate();

			if (affectedRows == 0) {
				System.err.println("Failed to insert project");
				return -1;
			}

			// Get the generated ID
			ResultSet generatedKeys = stmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				int projectId = generatedKeys.getInt(1);
				System.out.println("Project created successfully with ID: " + projectId);
				return projectId;
			}

		} catch (SQLException e) {
			System.err.println("SQLException in insert: " + e.getMessage());
			e.printStackTrace();
		}

		return -1;
	}

	/**
	 * Updates an existing project
	 * 
	 * @param projectId the ID of the project to update
	 * @param newName   the new name for the project
	 * @return true if update was successful, false otherwise
	 * @throws SQLException
	 */
	public boolean update(int projectId, String newName) throws SQLException {
		if (projectId <= 0 || newName == null || newName.trim().isEmpty()) {
			return false;
		}

		Connection conn = this.db.getConn();
		String query = "UPDATE " + TABLE_NAME + " SET name = ? WHERE id = ?";

		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, newName);
			stmt.setInt(2, projectId);

			int rows = stmt.executeUpdate();
			return rows > 0;

		} catch (SQLException e) {
			System.err.println("SQLException in update: " + e.getMessage());
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Deletes a project
	 * 
	 * @param projectId the ID of the project to delete
	 * @return true if deletion was successful, false otherwise
	 * @throws SQLException
	 */
	public boolean delete(int projectId) throws SQLException {
		if (projectId <= 0) {
			return false;
		}

		Connection conn = this.db.getConn();
		String query = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, projectId);

			int rows = stmt.executeUpdate();
			return rows > 0;

		} catch (SQLException e) {
			System.err.println("SQLException in delete: " + e.getMessage());
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Retrieves a project by ID
	 * 
	 * @param projectId the ID of the project
	 * @return a Project object, or null if not found
	 * @throws SQLException
	 */
	public Project selectProjectByID(int projectId) throws SQLException {
		if (projectId <= 0) {
			return null;
		}

		Connection conn = this.db.getConn();
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, projectId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				int id = rs.getInt("id");
				int userId = rs.getInt("user_id");
				String name = rs.getString("name");
				long createdAt = rs.getTimestamp("created_at").getTime();

				return new Project(id, userId, name, createdAt);
			}

		} catch (SQLException e) {
			System.err.println("SQLException in selectProjectByID: " + e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retrieves all projects for a specific user
	 * 
	 * @param userId the ID of the user
	 * @return a List of Project objects
	 * @throws SQLException
	 */
	public List<Project> selectProjectsByUserId(int userId) throws SQLException {
		List<Project> projects = new ArrayList<>();

		if (userId <= 0) {
			return projects;
		}

		Connection conn = this.db.getConn();
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE user_id = ?";

		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				long createdAt = rs.getTimestamp("created_at").getTime();

				projects.add(new Project(id, userId, name, createdAt));
			}

		} catch (SQLException e) {
			System.err.println("SQLException in selectProjectsByUserId: " + e.getMessage());
			e.printStackTrace();
		}

		return projects;
	}

	/**
	 * Retrieves all projects
	 * 
	 * @return a List of all Project objects
	 * @throws SQLException
	 */
	public List<Project> selectAllProjects() throws SQLException {
		List<Project> projects = new ArrayList<>();

		Connection conn = this.db.getConn();
		String query = "SELECT * FROM " + TABLE_NAME;

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				int id = rs.getInt("id");
				int userId = rs.getInt("user_id");
				String name = rs.getString("name");
				long createdAt = rs.getTimestamp("created_at").getTime();

				projects.add(new Project(id, userId, name, createdAt));
			}

		} catch (SQLException e) {
			System.err.println("SQLException in selectAllProjects: " + e.getMessage());
			e.printStackTrace();
		}

		return projects;
	}

	/**
	 * Gets the first project for a user (for onboarding purposes)
	 * If no projects exist, creates a default one
	 * 
	 * @param userId the ID of the user
	 * @return the project ID, or -1 if operation failed
	 * @throws SQLException
	 */
	public int getOrCreateDefaultProject(int userId) throws SQLException {
		if (userId <= 0) {
			System.err.println("Invalid userId: " + userId);
			return -1;
		}

		List<Project> userProjects = selectProjectsByUserId(userId);

		if (!userProjects.isEmpty()) {
			int projectId = userProjects.get(0).getId();
			System.out.println("Found existing project: " + projectId);
			return projectId;
		}

		System.out.println("No projects found for userId " + userId + ". Creating default project...");
		int newProjectId = insert(userId, "My First Project");

		if (newProjectId > 0) {
			System.out.println("Default project created with ID: " + newProjectId);
			return newProjectId;
		} else {
			System.err.println("Failed to create default project");
			return -1;
		}
	}

	/**
	 * Inner class to represent a Project
	 */

}
