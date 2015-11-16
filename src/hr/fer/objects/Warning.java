package hr.fer.objects;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Warning {
	
	private String username;
	private String message;
	private boolean exists = false;
	
	//New warning
	public Warning (String username, String message) {
		this.username = username;
		this.message = message;
		
		createWarning();
	}
	
	//Fetch existing warning
	public Warning (String username) {
		this.username = username;
		exists = fetchWarning();
	}
	
	public void createWarning () {
		Database db = new Database();
		String sql = "INSERT INTO warnings VALUES ('"+username+"', '"+message+"')";
		db.execute(sql);
	}

	public boolean fetchWarning () {
		Database db = new Database();
		String sql = "SELECT text FROM warnings WHERE username='"+username+"'";
		ResultSet results = db.select(sql);
		try {
			if (results.next()) {
				message = results.getString(1);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();
		return false;
	}
	
	public void delete() {
		Database db = new Database();
		String sql = "DELETE FROM warnings WHERE username='"+username+"' AND text='"+message+"'";
		db.execute(sql);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isExists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}
	
}
