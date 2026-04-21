package secondBrain.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
	protected Connection conn;
	
	public Database () throws SQLException, ClassNotFoundException {
		Properties properties = new Properties ();
		properties.put("charSet", "utf-8");
		properties.put("user", "admin");
		properties.put("password", "jtt");
		String url = "jdbc:mysql://localhost:3306/jtt_db";
		
		this.conn = DriverManager.getConnection(url, properties);
	}
	
	public Connection getConn () {
		return this.conn;
	}

	public static Connection connect() {
		return null;
	}
}
