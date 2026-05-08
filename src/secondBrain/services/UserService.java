<<<<<<< HEAD
package secondBrain.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import secondBrain.database.Database;
import secondBrain.service.User;



public class UserService {
	private Database db;
	private final String TABLE_NAME = "users";
	public UserService() throws ClassNotFoundException, SQLException {
		db = new Database();
	}
	
	/**
	 * Returns boolean value that describes insert process result.
	 * Method requires all table specific fields as parameters.
	 * @param email			the value of the EMAIL field in the database table users.
	 * @param password		the value of the PASSWORD field in the database table users.
	 * @return boolean {@code TRUE} if the entry in the database has been inserted.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public boolean insert(String email, String password) throws ClassNotFoundException, SQLException {
		if (email == null || password == null) {
			return false;
		}
		Connection conn = this.db.getConn();
		
		List<User> users = selectAllUsers();
		
		if (users.size() > 0) {
			for (User user : users) {
				boolean isEmail = user.getEmail().equals(email);
				if (isEmail) {
					return false;
				}
			}
		}
		
		String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
		
		String query = "INSERT INTO " + TABLE_NAME + " (email, password_hash) VALUES (?, ?)";
		PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		stmt.setString(1, email);
		stmt.setString(2, encodedPassword);
		
		boolean isInserted = stmt.execute();
		
		return isInserted;
	}
	
	public boolean update(int id, String newEmail, String newPassword) throws SQLException {
		if (id <= 0) {
			return false;
		}
		
		User user = selectUserByID(id);
		if (user == null) {
			return false;
		}
		
		if (newEmail == null && newPassword == null) {
			return false;
		}
		
		Connection conn = this.db.getConn();
		int rows = 0;
		
		if (newEmail != null) {
			String query = "UPDATE " + TABLE_NAME + " SET email = ? WHERE id = ?";
			PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, newEmail);
			stmt.setInt(2, id);
			rows = stmt.executeUpdate();
		}
		
		if (newPassword != null) {
			String query = "UPDATE " + TABLE_NAME + " SET password_hash = ? WHERE id = ?";
			PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			String encodedNewPassword = Base64.getEncoder().encodeToString(newPassword.getBytes());
			stmt.setString(1, encodedNewPassword);
			stmt.setInt(2, id);
			rows = stmt.executeUpdate();
		}
		
		if (rows > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Updates the password for a user identified by email during password reset.
	 * This method searches for the most recently added user with the given email
	 * and updates their password. This is used in the password reset flow.
	 * @param email			the email of the user whose password needs to be updated.
	 * @param newPassword	the new password to set.
	 * @return boolean {@code TRUE} if the password has been successfully updated, {@code FALSE} otherwise.
	 * @throws SQLException
	 */
	public boolean updatePasswordByEmail(String email, String newPassword) throws SQLException {
		if (email == null || newPassword == null) {
			return false;
		}
		
		Connection conn = this.db.getConn();
		
		// First, find the user with the given email
		String selectQuery = "SELECT id FROM " + TABLE_NAME + " WHERE email = ? ORDER BY id DESC LIMIT 1";
		PreparedStatement selectStmt = conn.prepareStatement(selectQuery, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		selectStmt.setString(1, email);
		ResultSet rs = selectStmt.executeQuery();
		
		if (!rs.next()) {
			return false; // User not found
		}
		
		int userId = rs.getInt("id");
		
		// Update the password for this user
		String encodedNewPassword = Base64.getEncoder().encodeToString(newPassword.getBytes());
		String updateQuery = "UPDATE " + TABLE_NAME + " SET password_hash = ? WHERE id = ?";
		PreparedStatement updateStmt = conn.prepareStatement(updateQuery, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		updateStmt.setString(1, encodedNewPassword);
		updateStmt.setInt(2, userId);
		
		int rows = updateStmt.executeUpdate();
		
		if (rows > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Updates the password for the current user. This is a simplified version 
	 * that updates the most recently added user's password. Use updatePasswordByEmail() 
	 * for more specific password reset scenarios.
	 * @param newPassword	the new password to set.
	 * @return boolean {@code TRUE} if the password has been successfully updated, {@code FALSE} otherwise.
	 * @throws SQLException
	 */
	public boolean updatePassword(String newPassword) throws SQLException {
		if (newPassword == null) {
			return false;
		}
		
		Connection conn = this.db.getConn();
		
		// Get the most recently added user
		String selectQuery = "SELECT id FROM " + TABLE_NAME + " ORDER BY id DESC LIMIT 1";
		PreparedStatement selectStmt = conn.prepareStatement(selectQuery, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = selectStmt.executeQuery();
		
		if (!rs.next()) {
			return false; // No user found
		}
		
		int userId = rs.getInt("id");
		
		// Update the password for this user
		String encodedNewPassword = Base64.getEncoder().encodeToString(newPassword.getBytes());
		String updateQuery = "UPDATE " + TABLE_NAME + " SET password_hash = ? WHERE id = ?";
		PreparedStatement updateStmt = conn.prepareStatement(updateQuery, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		updateStmt.setString(1, encodedNewPassword);
		updateStmt.setInt(2, userId);
		
		int rows = updateStmt.executeUpdate();
		
		if (rows > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean delete(int id) throws SQLException {
		if (id <= 0) {
			return false;
		}
		Connection conn = this.db.getConn();
		
		String query = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
		PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		stmt.setInt(1, id);
		int rows = stmt.executeUpdate();
		if (rows > 0) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public int selectUserByIDEmail (int id) throws SQLException {
		Connection conn = this.db.getConn();
		
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
		
		PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		
		if(!rs.next()) {
			return -1;
		}
		
		String email = rs.getString("email");
		String encodedPassword = rs.getString("password_hash");
		String decodedPassword = new String(Base64.getDecoder().decode(encodedPassword));
		
		User user = new User (id, email, decodedPassword);
		
		return id;
		
	}
	
	/**
	 * Selects a user from database table users by field ID
	 * @param id			users email
	 * @return user if at least 1 entry is found returns the user, but if no entry is found returns null.
	 * @throws SQLException
	 */
	public User selectUserByID (int id) throws SQLException {
		Connection conn = this.db.getConn();
		
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
		
		PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		
		if(!rs.next()) {
			return null;
		}
		
		String email = rs.getString("email");
		String encodedPassword = rs.getString("password_hash");
		String decodedPassword = new String(Base64.getDecoder().decode(encodedPassword));
		
		User user = new User (id, email, decodedPassword);
		
		return user;
	}
	
	
	/**
	 * Selects all users from table users.
	 * @return List<User> Length is based on the amount of users in the table users.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List <User> selectAllUsers() throws ClassNotFoundException, SQLException {
		Connection conn = this.db.getConn();
		List<User> users = new ArrayList<>();
		
		String query = "SELECT * FROM " + TABLE_NAME;
		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		
		while(rs.next()) {
			int id = rs.getInt("id");
			String email = rs.getString("email");
			String encodedPassword = rs.getString("password_hash");
			String decodedPassword = new String(Base64.getDecoder().decode(encodedPassword));
			
			User user = new User(id, email, decodedPassword);
			users.add(user);
		}
		
		return users;
		
	}
}
=======
package secondBrain.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import secondBrain.database.Database;
import secondBrain.service.User;



public class UserService {
	private Database db;
	private final String TABLE_NAME = "users";
	public UserService() throws ClassNotFoundException, SQLException {
		db = new Database();
	}
	
	/**
	 * Returns boolean value that describes insert process result.
	 * Method requires all table specific fields as parameters.
	 * @param email			the value of the EMAIL field in the database table users.
	 * @param password		the value of the PASSWORD field in the database table users.
	 * @return boolean {@code TRUE} if the entry in the database has been inserted.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public boolean insert(String email, String password) throws ClassNotFoundException, SQLException {
		if (email == null || password == null) {
			return false;
		}
		Connection conn = this.db.getConn();
		
		List<User> users = selectAllUsers();
		
		if (users.size() > 0) {
			for (User user : users) {
				boolean isEmail = user.getEmail().equals(email);
				if (isEmail) {
					return false;
				}
			}
		}
		
		String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
		
		String query = "INSERT INTO " + TABLE_NAME + " (email, password_hash) VALUES (?, ?)";
		PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		stmt.setString(1, email);
		stmt.setString(2, encodedPassword);
		
		boolean isInserted = stmt.execute();
		
		return isInserted;
	}
	
	public boolean update(int id, String newEmail, String newPassword) throws SQLException {
		if (id <= 0) {
			return false;
		}
		
		User user = selectUserByID(id);
		if (user == null) {
			return false;
		}
		
		if (newEmail == null && newPassword == null) {
			return false;
		}
		
		Connection conn = this.db.getConn();
		int rows = 0;
		
		if (newEmail != null) {
			String query = "UPDATE " + TABLE_NAME + " SET email = ? WHERE id = ?";
			PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, newEmail);
			stmt.setInt(2, id);
			rows = stmt.executeUpdate();
		}
		
		if (newPassword != null) {
			String query = "UPDATE " + TABLE_NAME + " SET password_hash = ? WHERE id = ?";
			PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			String encodedNewPassword = Base64.getEncoder().encodeToString(newPassword.getBytes());
			stmt.setString(1, encodedNewPassword);
			stmt.setInt(2, id);
			rows = stmt.executeUpdate();
		}
		
		if (rows > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Updates the password for a user identified by email during password reset.
	 * This method searches for the most recently added user with the given email
	 * and updates their password. This is used in the password reset flow.
	 * @param email			the email of the user whose password needs to be updated.
	 * @param newPassword	the new password to set.
	 * @return boolean {@code TRUE} if the password has been successfully updated, {@code FALSE} otherwise.
	 * @throws SQLException
	 */
	public boolean updatePasswordByEmail(String email, String newPassword) throws SQLException {
		if (email == null || newPassword == null) {
			return false;
		}
		
		Connection conn = this.db.getConn();
		
		// First, find the user with the given email
		String selectQuery = "SELECT id FROM " + TABLE_NAME + " WHERE email = ? ORDER BY id DESC LIMIT 1";
		PreparedStatement selectStmt = conn.prepareStatement(selectQuery, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		selectStmt.setString(1, email);
		ResultSet rs = selectStmt.executeQuery();
		
		if (!rs.next()) {
			return false; // User not found
		}
		
		int userId = rs.getInt("id");
		
		// Update the password for this user
		String encodedNewPassword = Base64.getEncoder().encodeToString(newPassword.getBytes());
		String updateQuery = "UPDATE " + TABLE_NAME + " SET password_hash = ? WHERE id = ?";
		PreparedStatement updateStmt = conn.prepareStatement(updateQuery, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		updateStmt.setString(1, encodedNewPassword);
		updateStmt.setInt(2, userId);
		
		int rows = updateStmt.executeUpdate();
		
		if (rows > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Updates the password for the current user. This is a simplified version 
	 * that updates the most recently added user's password. Use updatePasswordByEmail() 
	 * for more specific password reset scenarios.
	 * @param newPassword	the new password to set.
	 * @return boolean {@code TRUE} if the password has been successfully updated, {@code FALSE} otherwise.
	 * @throws SQLException
	 */
	public boolean updatePassword(String newPassword) throws SQLException {
		if (newPassword == null) {
			return false;
		}
		
		Connection conn = this.db.getConn();
		
		// Get the most recently added user
		String selectQuery = "SELECT id FROM " + TABLE_NAME + " ORDER BY id DESC LIMIT 1";
		PreparedStatement selectStmt = conn.prepareStatement(selectQuery, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = selectStmt.executeQuery();
		
		if (!rs.next()) {
			return false; // No user found
		}
		
		int userId = rs.getInt("id");
		
		// Update the password for this user
		String encodedNewPassword = Base64.getEncoder().encodeToString(newPassword.getBytes());
		String updateQuery = "UPDATE " + TABLE_NAME + " SET password_hash = ? WHERE id = ?";
		PreparedStatement updateStmt = conn.prepareStatement(updateQuery, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		updateStmt.setString(1, encodedNewPassword);
		updateStmt.setInt(2, userId);
		
		int rows = updateStmt.executeUpdate();
		
		if (rows > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean delete(int id) throws SQLException {
		if (id <= 0) {
			return false;
		}
		Connection conn = this.db.getConn();
		
		String query = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
		PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		stmt.setInt(1, id);
		int rows = stmt.executeUpdate();
		if (rows > 0) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public int selectUserByIDEmail (int id) throws SQLException {
		Connection conn = this.db.getConn();
		
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
		
		PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		
		if(!rs.next()) {
			return -1;
		}
		
		String email = rs.getString("email");
		String encodedPassword = rs.getString("password_hash");
		String decodedPassword = new String(Base64.getDecoder().decode(encodedPassword));
		
		User user = new User (id, email, decodedPassword);
		
		return id;
		
	}
	
	/**
	 * Selects a user from database table users by field ID
	 * @param id			users email
	 * @return user if at least 1 entry is found returns the user, but if no entry is found returns null.
	 * @throws SQLException
	 */
	public User selectUserByID (int id) throws SQLException {
		Connection conn = this.db.getConn();
		
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
		
		PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		
		if(!rs.next()) {
			return null;
		}
		
		String email = rs.getString("email");
		String encodedPassword = rs.getString("password_hash");
		String decodedPassword = new String(Base64.getDecoder().decode(encodedPassword));
		
		User user = new User (id, email, decodedPassword);
		
		return user;
	}
	
	
	/**
	 * Selects all users from table users.
	 * @return List<User> Length is based on the amount of users in the table users.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List <User> selectAllUsers() throws ClassNotFoundException, SQLException {
		Connection conn = this.db.getConn();
		List<User> users = new ArrayList<>();
		
		String query = "SELECT * FROM " + TABLE_NAME;
		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		
		while(rs.next()) {
			int id = rs.getInt("id");
			String email = rs.getString("email");
			String encodedPassword = rs.getString("password_hash");
			String decodedPassword = new String(Base64.getDecoder().decode(encodedPassword));
			
			User user = new User(id, email, decodedPassword);
			users.add(user);
		}
		
		return users;
		
	}
}
>>>>>>> branch 'main' of git@github-jtt-1p:smansarturs/SecondBrain_KVD.git
