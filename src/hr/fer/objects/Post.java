package hr.fer.objects;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Post {

	// Variables
	private int pid;
	private String text;
	private String author;
	private long creationTime;
	private int popularity;
	private int main;
	private int tid;

	// Create new post
	public Post(String text, String author, long creationTime, int popularity, int main, int tid) {
		super();
		this.text = text;
		this.author = author;
		this.creationTime = creationTime;
		this.popularity = popularity;
		this.main = main;
		this.tid = tid;

		createPost();
	}

	// Fetch existing post
	public Post(int pid) {
		fetchPost(pid);
	}

	// Find latest post
	public Post() {
		findLatest();
	}

	private void createPost() {
		pid = fetchPid();
		String sql = "INSERT INTO posts VALUES (" + pid + ",'" + text + "','" + author + "', " + creationTime + ","
				+ popularity + ", " + main + "," + tid + ")";
		Database db = new Database();
		db.execute(sql);
	}

	private void fetchPost(int pid) {
		String sql = "SELECT * FROM posts WHERE pid=" + pid;
		Database db = new Database();
		ResultSet results = db.select(sql);
		try {
			while (results.next()) {
				this.pid = results.getInt(1);
				text = results.getString(2);
				author = results.getString(3);
				creationTime = results.getLong(4);
				popularity = results.getInt(5);
				main = results.getInt(6);
				tid = results.getInt(7);
			}
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void findLatest() {
		String sql = "SELECT * FROM posts";
		Database db = new Database();
		ResultSet results = db.select(sql);

		long latestTime = -1;
		try {
			while (results.next()) {
				creationTime = results.getLong(4);
				if (creationTime > latestTime) {
					pid = results.getInt(1);
					text = results.getString(2);
					author = results.getString(3);
					popularity = results.getInt(5);
					main = results.getInt(6);
					tid = results.getInt(7);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();
	}

	public void vote(String type, String username) {
		User user = new User(username);

		// Check if voting is permitted
		Database db = new Database();
		String sql = "SELECT pid FROM votes WHERE username='" + username + "' AND pid=" + pid;
		ResultSet results = db.select(sql);
		try {
			if (results.next())
				return;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();

		int voteValue = user.getRank();
		if (type.equals("downvote"))
			voteValue *= -1;

		// Update post
		db = new Database();
		String sql1 = "UPDATE posts SET popularity=popularity+" + voteValue + " WHERE pid=" + pid;
		db.execute(sql1);

		// Add vote
		String sql2 = "INSERT INTO votes VALUES ('" + username + "'," + pid + ")";
		db.execute(sql2);
	}

	public int fetchPid() {
		Database db = new Database();
		String sql = "SELECT pid FROM posts";
		ResultSet results = db.select(sql);
		int pid = -1;
		try {
			while (results.next()) {
				if (results.getInt(1) > pid)
					pid = results.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();
		pid++;
		return pid;
	}

	public void delete() {
		Database db = new Database();
		String sql = "DELETE FROM posts WHERE pid=" + pid;
		db.execute(sql);
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public int getPopularity() {
		return popularity;
	}

	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}

	public int getMain() {
		return main;
	}

	public void setMain(int main) {
		this.main = main;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public int getCid() {
		Thread thread = new Thread(tid);
		return thread.getCid();
	}

}
