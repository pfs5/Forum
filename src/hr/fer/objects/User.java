package hr.fer.objects;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {

	// Main variables
	private String username;
	private String password;

	// Public variables
	private String nickname;
	private String avatar;
	private String sex;

	// Private variables
	private String name;
	private String surname;
	private int age;
	private String location;
	private String email;

	// Data variables
	private String flags;
	private int popularity;
	private int rank;
	private String accessLevel;
	
	//Objects
	private Password pw;

	// Create new user
	public User(String username, String password, String nickname, String avatar, String sex, String name, String surname,
			int age, String location, String email, String flags, int popularity, int rank, String accessLevel) {
		super();
		pw = new Password();
		this.username = username;
		this.password = pw.code(password);
		this.nickname = nickname;
		this.avatar = avatar;
		this.sex = sex;
		this.name = name;
		this.surname = surname;
		this.age = age;
		this.location = location;
		this.email = email;
		this.flags = flags;
		this.popularity = popularity;
		this.rank = rank;
		this.accessLevel = accessLevel;

		createUser();
	}

	// Fetch existing user
	public User(String username) {
		if (username == null) {
			this.username = "";
			this.accessLevel = "anonymous";
		} else
			fetchUser(username);
	}

	public void createUser() {
		Database db = new Database();
		String sql = "INSERT INTO users VALUES (" + "'" + username + "'," + "'" + password + "'," + "'" + nickname
				+ "','" + avatar + "'," + "'" + sex + "'," + "'" + name + "'," + "'" + surname + "'," + age + ",'"
				+ location + "'," + "'" + email + "'," + "'" + flags + "'," + popularity + "," + rank + "," + "'"
				+ accessLevel + "'" + ")";
		db.execute(sql);
	}

	public void fetchUser(String username) {
		Database db = new Database();
		String sql = "SELECT * FROM users WHERE username='" + username + "'";
		ResultSet results = db.select(sql);
		try {
			if (results.next()) {
				this.username = results.getString(1);
				this.password = results.getString(2);
				this.nickname = results.getString(3);
				this.avatar = results.getString(4);
				this.sex = results.getString(5);
				this.name = results.getString(6);
				this.surname = results.getString(7);
				this.age = results.getInt(8);
				this.location = results.getString(9);
				this.email = results.getString(10);
				this.flags = results.getString(11);
				this.popularity = results.getInt(12);
				this.rank = results.getInt(13);
				this.accessLevel = results.getString(14);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();
	}

	private void updateFlags() {
		Database db = new Database();
		String sql = "UPDATE users SET flags='" + flags + "' WHERE username='" + username + "'";
		db.execute(sql);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFlags() {
		return flags;
	}

	public void setFlags(String flags) {
		this.flags = flags;
		updateFlags();
	}

	public int getPopularity() {
		Database db = new Database();
		String sql = "SELECT popularity FROM posts WHERE author='" + username + "'";
		ResultSet results = db.select(sql);
		popularity = 0;
		try {
			while (results.next()) {
				int postPopularity = results.getInt(1);
				popularity += postPopularity;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();
		setRank(rank);
		return popularity;
	}

	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}

	public int getRank() {
		getPopularity();
		return rank;
	}

	public void setRank(int rank) {
		if (popularity < 10)
			rank = 1;
		if (popularity >= 10 && popularity < 50)
			rank = 2;
		if (popularity >= 50 && popularity < 100)
			rank = 3;
		if (popularity >= 100 && popularity < 150)
			rank = 4;
		if (popularity >= 150)
			rank = 5;

		this.rank = rank;
	}
	
	public String getRankName () {
		switch (rank) {
		case 1:
			return "[prvi]";
		case 2:
			return "[drugi]";
		case 3:
			return "[treci]";
		case 4:
			return "[cetvrti]";
		case 5:
			return "[peti]";
		default:
			return "[error]";
		}
	}

	public String getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
		
		Database db = new Database();
		String sql = "UPDATE users SET access='"+accessLevel+"' WHERE username='"+username+"'";
		db.execute(sql);
	}

	public void setModerator (int cid) {
		Database db = new Database();
		String sql = "INSERT INTO moderators VALUES ('"+username+"', "+cid+")";
		db.execute(sql);
	}
	
	public void delete () {
		Flags f = new Flags(flags);
		f.setActivity(false);
		flags = f.getFlags();
		Database db = new Database();
		String sql = "UPDATE users SET flags='"+flags+"' WHERE username='"+username+"'";
		db.execute(sql);
	}
}
