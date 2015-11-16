package hr.fer.objects;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

	// Database data
	private String url;
	private String driver;

	// Connection and SQL data
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet results = null;

	public Database() {
		url = "jdbc:derby://localhost:1527/ForumDatabase;create=true";
		driver = "org.apache.derby.jdbc.ClientDriver";

		connect();
	}

	// Private methods
	private void connect() {
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url);

		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	// Public methods
	public ResultSet select(String sql) {
		results = null;
		try {
			stmt = conn.createStatement();
			results = stmt.executeQuery(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return results;
	}

	public void close() {
		try {
			stmt.close();
			results.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void execute(String sql) {
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
