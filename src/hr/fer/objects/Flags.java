package hr.fer.objects;

public class Flags {

	String flags;

	// Flags
	private boolean username = false;
	private boolean name = false;
	private boolean surname = false;
	private boolean age = false;
	private boolean location = false;
	private boolean email = false;
	private boolean popularity = false;
	private boolean rank = false;
	private boolean accessLevel = false;

	private boolean activity = false;

	public Flags(String flags) {
		this.flags = flags;
		decodeFlags();
	}

	private void decodeFlags() {
		username = charToBoolean(flags.charAt(0));
		name = charToBoolean(flags.charAt(1));
		surname = charToBoolean(flags.charAt(2));
		age = charToBoolean(flags.charAt(3));
		location = charToBoolean(flags.charAt(4));
		email = charToBoolean(flags.charAt(5));
		popularity = charToBoolean(flags.charAt(6));
		rank = charToBoolean(flags.charAt(7));
		accessLevel = charToBoolean(flags.charAt(8));
		activity = charToBoolean(flags.charAt(9));
	}

	private boolean charToBoolean(char input) {
		if (input == '0')
			return false;
		return true;
	}

	private char booleanToChar(boolean input) {
		if (input)
			return '1';
		else
			return '0';
	}

	public String getFlags() {
		flags = booleanToChar(username) + "" + booleanToChar(username) + "" + booleanToChar(username) + ""
				+ booleanToChar(username) + "" + booleanToChar(username) + "" + booleanToChar(username) + ""
				+ booleanToChar(username) + "" + booleanToChar(username) + "" + booleanToChar(username) + ""
				+ booleanToChar(username);
		return flags;
	}

	public void setFlags(String flags) {
		this.flags = flags;
	}

	public boolean isUsername() {
		return username;
	}

	public void setUsername(boolean username) {
		this.username = username;
	}

	public boolean isName() {
		return name;
	}

	public void setName(boolean name) {
		this.name = name;
	}

	public boolean isSurname() {
		return surname;
	}

	public void setSurname(boolean surname) {
		this.surname = surname;
	}

	public boolean isAge() {
		return age;
	}

	public void setAge(boolean age) {
		this.age = age;
	}

	public boolean isLocation() {
		return location;
	}

	public void setLocation(boolean location) {
		this.location = location;
	}

	public boolean isEmail() {
		return email;
	}

	public void setEmail(boolean email) {
		this.email = email;
	}

	public boolean isPopularity() {
		return popularity;
	}

	public void setPopularity(boolean popularity) {
		this.popularity = popularity;
	}

	public boolean isRank() {
		return rank;
	}

	public void setRank(boolean rank) {
		this.rank = rank;
	}

	public boolean isAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(boolean accessLevel) {
		this.accessLevel = accessLevel;
	}

	public boolean isActivity() {
		return activity;
	}

	public void setActivity(boolean activity) {
		this.activity = activity;
	}

}
