package hr.fer.objects;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Thread {

	// Variables
	private int tid;
	private String title;
	private String description;
	private int cid;
	
	private Post lastPost;

	// Create new thread
	public Thread(String title, String description, int cid, String postText, String author) {
		super();
		tid = fetchTid();
		this.title = title;
		this.description = description;
		this.cid = cid;
		createThread(postText, author);
	}

	// Fetch existing thread
	public Thread(int tid) {
		fetchThread(tid);
		fetchLastPost();
	}

	// Find latest thread
	public Thread() {
		findLatest();
	}

	private void createThread(String postText, String author) {
		tid=fetchTid();
		String sql = "INSERT INTO threads VALUES (" + tid + ",'" + title + "','" + description + "'," + cid + ")";
		Database db = new Database();
		db.execute(sql);
		
		long creationTime = System.currentTimeMillis();
		Post post = new Post(postText, author, creationTime, 0, 1, tid);
	}

	private void fetchThread(int tid) {
		String sql = "SELECT * FROM threads WHERE tid=" + tid;
		Database db = new Database();
		ResultSet results = db.select(sql);
		try {
			while (results.next()) {
				this.tid = results.getInt(1);
				title = results.getString(2);
				description = results.getString(3);
				cid = results.getInt(4);
			}
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void fetchLastPost() {
		Database db = new Database();
		String sql = "SELECT pid,creationTime FROM posts WHERE tid="+tid;
		ResultSet results = db.select(sql);
		try {
			long latestTime = -1;
			while (results.next()) {
				long creationTime = results.getLong(2);
				if (creationTime>latestTime) {
					int pid = results.getInt(1);
					lastPost = new Post(pid);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();
	}

	private void findLatest() {
		Post latestPost = new Post();
		int tid = latestPost.getTid();
		fetchThread(tid);
	}
	
	public int fetchTid() {
		Database db = new Database();
		String sql = "SELECT tid FROM threads";
		ResultSet results = db.select(sql);
		int tid=-1;
		try {
			while (results.next()) {
				if (results.getInt(1)>tid)
					tid = results.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();
		tid++;
		return tid;
	}
	
	public void delete() {
		Database db = new Database();
		String sql = "DELETE FROM threads WHERE tid="+tid;
		db.execute(sql);
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

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public Post getLastPost() {
		return lastPost;
	}

	public void setLastPost(Post lastPost) {
		this.lastPost = lastPost;
	}

}
