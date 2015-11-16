package hr.fer.objects;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Category {

	// Variables
	private int cid;
	private String title;

	// Create new category
	public Category(String categoryTitle, String threadTitle, String threadDescription, String postText, String username) {
		super();
		this.title = categoryTitle;

		createCategory(threadTitle, threadDescription, postText, username);
	}

	// Fetch existing category
	public Category(int cid) {
		fetchCategory(cid);
	}

	private void createCategory(String threadTitle, String threadDescription, String postText, String username) {
		fetchCid();
		String sql = "INSERT INTO categories VALUES (" + cid + ",'" + title + "'" + ")";
		Database db = new Database();
		db.execute(sql);
		Thread thread = new Thread(threadTitle, threadDescription, cid, postText, username);
	}

	private void fetchCategory(int cid) {
		String sql = "SELECT * FROM categories WHERE cid=" + cid;
		Database db = new Database();
		ResultSet results = db.select(sql);
		try {
			while (results.next()) {
				this.cid = results.getInt(1);
				title = results.getString(2);
			}
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int fetchCid() {
		Database db = new Database();
		String sql = "SELECT cid FROM categories";
		ResultSet results = db.select(sql);
		int cid=-1;
		try {
			while (results.next()) {
				if (results.getInt(1)>cid)
					cid = results.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();
		cid++;
		return cid;
	}

	public boolean isModerator (String username) {
		Database db = new Database();
		String sql = "SELECT * FROM moderators WHERE username='"+username+"' AND cid="+cid;
		ResultSet results = db.select(sql);
		try {
			if (results.next())
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();
		return false;
	}
	
	public void delete() {
		Database db = new Database();
		String sql = "DELETE FROM categories WHERE cid="+cid;
		db.execute(sql);
	}
	
	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
