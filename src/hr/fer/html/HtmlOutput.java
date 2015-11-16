package hr.fer.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ThreadInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.plaf.basic.BasicTreeUI.TreeCancelEditingAction;

import org.apache.derby.iapi.store.raw.Page;
import org.eclipse.jdt.internal.compiler.util.HashtableOfIntValues;

import hr.fer.objects.Category;
import hr.fer.objects.Database;
import hr.fer.objects.Flags;
import hr.fer.objects.Thread;
import hr.fer.objects.User;
import hr.fer.objects.Warning;
import hr.fer.objects.Post;

/*
 *  					### Page layout ###
 *  
 *  	---------------------- header ----------------------
 *  	*title*									*login*
 *  
 *  	---------------------- options   ----------------------
 *  	*path*									*userOptions*
 *  
 *  	---------------------- latest ----------------------
 *  	*latestThread							*latestThreadInfo*
 *  
 *  	---------------------- special ----------------------
 *  							*row*
 * 		*content*								*edge*
 * 
 */
public class HtmlOutput {

	// FORUM
	private static String TITLE_TEXT = "forum";

	// Page type
	public enum TYPE {
		homepage, showThread, register, publicProfile, privateProfile, admin, text, createThread, createCategory, editPost
	}

	// Html data
	private HttpServletRequest request;
	private HttpServletResponse response;
	private PrintWriter out;
	private String contextPath;

	// Html constants
	private static String CSS = "<link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\">";
	private static String HTML_START = "<html>";
	private static String HTML_END = "</body></html>";
	private String HTML_HEAD;
	private String REDIRECT = "";

	// Current page data
	private String pageTitle;
	private TYPE type;
	private int tid;
	private int pid;

	private ArrayList<Category> categories;
	private ArrayList<Thread> threads;
	private ArrayList<Post> posts;

	private Object data;

	// Current user data
	private HttpSession session;
	private User currentUser;
	private String currentAccess;
	private boolean hasAvatar = true;
	private Warning warning;

	public HtmlOutput(HttpServletRequest request, HttpServletResponse response, String pageTitle, TYPE type,
			Object data) {
		this.request = request;
		this.response = response;
		this.pageTitle = pageTitle;
		this.type = type;
		this.data = data;

		// Set important variables
		contextPath = request.getContextPath();
		if (pageTitle.equals("Uspjeh!"))
			REDIRECT = "<meta http-equiv=\"refresh\" content=\"3;url=" + contextPath + "/Homepage\" />";
		HTML_HEAD = "<head>" + CSS + "<title>" + pageTitle + "</title>" + REDIRECT + "</head><body>";
		response.setContentType("text/html");
		try {
			out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Get current user
		session = request.getSession();
		String currentUsername = (String) session.getAttribute("username");
		currentUser = new User(currentUsername);
		currentAccess = currentUser.getAccessLevel();

		// Get lists
		if (type == TYPE.homepage || type == TYPE.showThread) {
			categories = getCategories();
			threads = getThreads();
		}

		// Set tid
		if (type == TYPE.showThread)
			tid = Integer.parseInt((String) data);
	}

	// ###### Print page ######
	public void printPage() {
		out.println(HTML_START);
		out.println(HTML_HEAD);

		// ### Common template part ###

		// Header table
		String titleHtml = "<td id=\"title\" class=\"left\">" + TITLE_TEXT + "</td>";
		String loginHtml = "<td id=\"login\" class=\"right\"><form method=\"post\" action=\"" + contextPath
				+ "/Homepage?action=login\">" + "username:" + "&nbsp;&nbsp;" + "<input type=\"text\" name=\"username\">"
				+ "<br><br>" + "password:" + "&nbsp;&nbsp;" + "<input type=\"password\" name=\"password\">" + "<br><br>"
				+ "<input type=\"submit\" value=\"login\">" + "</form></td>";
		if (!currentAccess.equals("anonymous"))
			loginHtml = "<td id=\"login\" class=\"right\">Trenutno ste ulogirani kao " + currentUser.getNickname()
					+ "</td>";
		String headerTable = "<table id=\"headerTable\"><tr>" + titleHtml + "" + loginHtml + "</tr></table>";

		// Options table
		String newCategoryLink = contextPath + "/newCategory";
		String newCategoryText = "";
		String sortLink = contextPath + "/showThread?tid=" + tid + "&sort=";
		String sortText = "";
		if (currentAccess.equals("admin") && type == TYPE.homepage)
			newCategoryText = "<a id=\"createCategory\" href=\"" + newCategoryLink + "\">[nova kategorija]</a>";
		if (type == TYPE.showThread) {
			sortText += "<a id=\"sort\" href=\"" + sortLink + "up\">[uzlazno]</a>&nbsp;";
			sortText += "<a id=\"sort\" href=\"" + sortLink + "down\">[silazno]</a>&nbsp;";
			sortText += "<a id=\"sort\" href=\"" + sortLink + "popularity\">[popularnost]</a>&nbsp;";
		}
		String pathHtml = "<td id=\"path\" class=\"left\">" + getAbsolutePath() + "<br>" + newCategoryText + ""
				+ sortText + "</td>";
		String userOptionsHtml = "<td id=\"userOptions\" class=\"right\">" + getUserOptions() + "</td>";
		String optionsTable = "<table id=\"optionsTable\"><tr>" + pathHtml + "" + userOptionsHtml + "</tr></table>";

		// Latest activity table
		String latestThreadHtml = "<td id=\"content\" class=\"left\">" + getLatestThread() + "</td>";
		String latestPostHtml = "<td id=\"edge\" class=\"right\">" + getLatestPost() + "</td>";
		String latestTitleHtml = "<tr><td id=\"categoryExtra\" colspan=\"2\"></td></tr>"
				+ "<tr><td id=\"category\" colspan=\"2\"><b>Aktualna tema</b></td></tr>";
		String latestTable = "<table id=\"latestTable\">" + latestTitleHtml + "<tr>" + latestThreadHtml + ""
				+ latestPostHtml + "</tr></table>";

		// ### Print the template part###
		out.println(headerTable);
		out.println(optionsTable);
		if (type == TYPE.homepage)
			out.println(latestTable);

		// ### Content part ###
		out.println("<table id=\"contentTable\">");

		switch (type) {
		case homepage:
			printHomepage();
			break;
		case publicProfile:
			printProfile("public");
			break;
		case privateProfile:
			printProfile("private");
			break;
		case showThread:
			printThread();
			break;
		case register:
			printRegister();
			break;
		case text:
			printText();
			break;
		case createThread:
			printCreateThread();
			break;
		case createCategory:
			printCreateCategory();
			break;
		case admin:
			printAdmin();
			break;
		case editPost:
			printEditPost();
			break;

		default:
			break;

		}

		out.println("</table>");
		out.println(HTML_END);
	}

	// ###### PRINT TYPE FUNCTIONS ######
	private void printHomepage() {
		String homepageHtml = "";

		for (Category category : categories) {
			int categoryCid = category.getCid();
			String categoryTitle = category.getTitle();

			// Categories html
			String createThreadLink = contextPath + "/newThread?cid=" + categoryCid;
			String deleteCategoryLink = contextPath + "/Homepage?action=deleteCategory&cid=" + categoryCid;

			homepageHtml += "<tr><td id=\"categoryExtra\" colspan=\"2\"></td></tr>";
			homepageHtml += "<tr><td id=\"category\" colspan=\"2\"><b>" + categoryTitle + "</b>&nbsp;&nbsp;";

			if (checkThreadCreateAccess(categoryCid))
				homepageHtml += "<a id=\"create\" href=\"" + createThreadLink + "\">[nova tema]</a>&nbsp;";
			if (checkCategoryAccess(categoryCid)) {
				homepageHtml += "<a id=\"delete\" href=\"" + deleteCategoryLink + "\">[obri&#353;i]</a></td></tr>";
			}

			for (Thread thread : threads) {
				int threadTid = thread.getTid();
				String threadTitle = thread.getTitle();
				String threadDescription = thread.getDescription();
				int threadCid = thread.getCid();

				Post latestPost = thread.getLastPost();
				String postAuthor = latestPost.getAuthor();
				String creationTime = new Date(latestPost.getCreationTime()) + "";

				if (threadCid == categoryCid) {
					String threadLink = contextPath + "/showThread?tid=" + threadTid;
					String deleteThreadLink = contextPath + "/Homepage?action=deleteThread&tid=" + threadTid;

					// Thread text
					homepageHtml += "<tr><td id=\"content\" class=\"left\">";
					homepageHtml += "<a href=\"" + threadLink + "\" id=\"threadTitle\">" + threadTitle + "</a>";
					homepageHtml += "<div id=\"threadDescription\">" + threadDescription + "</div>";
					homepageHtml += "</td><td id=\"edge\" class=\"right\">";

					// Thread info
					homepageHtml += "<div id=\"postAuthorHomepage\"><b>" + postAuthor + "</b></div>";
					homepageHtml += "<div id=\"postCreationTime\">" + creationTime + "</div>";
					if (checkThreadAccess(threadTid))
						homepageHtml += "<a id=\"delete\" href=\"" + deleteThreadLink + "\">[obri&#353;i]</a>";

					homepageHtml += "</td></tr>";
				}
			}
		}

		out.println(homepageHtml);
	}

	private void printProfile(String type) {
		User user = (User) data;
		String profileHtml = "";

		profileHtml += "<tr><td id=\"content\" class=\"left\">";
		if (type.equals("private")) {
			profileHtml += "<div><b>username:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getUsername()
					+ "</font></div>";
			profileHtml += "<div><b>nadimak:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getNickname()
					+ "</font></div>";
			profileHtml += "<div><b>spol:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getSex() + "</font></div>";
			profileHtml += "<div><b>ime:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getName() + "</font></div>";
			profileHtml += "<div><b>prezime:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getSurname()
					+ "</font></div>";
			profileHtml += "<div><b>dob:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getAge() + "</font></div>";
			profileHtml += "<div><b>lokacija:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getLocation()
					+ "</font></div>";
			profileHtml += "<div><b>e-mail:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getEmail() + "</font></div>";
			profileHtml += "<div><b>popularnost:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getPopularity()
					+ "</font></div>";
			profileHtml += "<div><b>rang:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getRankName() + "</font></div>";
			profileHtml += "<div><b>razina ovlast:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getAccessLevel()
					+ "</font></div>";

			String setFlagsHtml = "<form action=\"" + contextPath + "/Profile?type=private\">" + "<fieldset>"
					+ "<legend>Vidljivost na javnom profilu</legend>"
					+ "<input type=\"checkbox\" name=\"usernameF\">username<br>"
					+ "<input type=\"checkbox\" name=\"nameF\">ime<br>"
					+ "<input type=\"checkbox\" name=\"surnameF\">prezime<br>"
					+ "<input type=\"checkbox\" name=\"ageF\">dob<br>"
					+ "<input type=\"checkbox\" name=\"locationF\">lokacija<br>"
					+ "<input type=\"checkbox\" name=\"emailF\">e-mail<br>"
					+ "<input type=\"checkbox\" name=\"popularityF\">popularnost<br>"
					+ "<input type=\"checkbox\" name=\"rankF\">rang<br>"
					+ "<input type=\"checkbox\" name=\"accessLevelF\">razina pristupa<br><br>"
					+ "<input type=\"submit\" value=\"primijeni\">" + "</fieldset>" + "</form>";
			profileHtml += "</td><td id=\"edge\" class=\"right\">" + setFlagsHtml + "</td></tr>";
		}
		if (type.equals("public")) {

			Flags flags = new Flags(user.getFlags());

			// Public
			profileHtml += "<div><b>nadimak:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getNickname()
					+ "</font></div>";
			profileHtml += "<div><b>spol:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getSex() + "</font></div>";

			// Private
			if (flags.isUsername())
				profileHtml += "<div><b>username:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getUsername()
						+ "</font></div>";
			if (flags.isName())
				profileHtml += "<div><b>ime:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getName() + "</font></div>";
			if (flags.isSurname())
				profileHtml += "<div><b>prezime:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getSurname()
						+ "</font></div>";
			if (flags.isAge())
				profileHtml += "<div><b>dob:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getAge() + "</font></div>";
			if (flags.isLocation())
				profileHtml += "<div><b>lokacija:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getLocation()
						+ "</font></div>";
			if (flags.isEmail())
				profileHtml += "<div><b>e-mail:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getEmail()
						+ "</font></div>";
			if (flags.isPopularity())
				profileHtml += "<div><b>popularnost:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getPopularity()
						+ "</font></div>";
			if (flags.isRank())
				profileHtml += "<div><b>rang:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getRankName()
						+ "</font></div>";
			if (flags.isAccessLevel())
				profileHtml += "<div><b>razina ovlast:</b>&nbsp;&nbsp;<font color=\"grey\">" + user.getAccessLevel()
						+ "</font></div>";
			profileHtml += "</td><td id=\"edge\" class=\"right\"><img src=\"" + user.getAvatar() + "\"></td></tr>";
		}

		out.println(profileHtml);
	}

	private void printThread() {
		int threadTid = Integer.parseInt((String) data);
		tid = threadTid;
		String threadHtml = "";
		ArrayList<Post> posts = getPosts(threadTid);

		for (int i = 0; i < 2; i++) {
			int main;
			if (i == 0)
				main = 1;
			else
				main = 0;

			for (Post post : posts) {
				int postMain = post.getMain();
				if (postMain == main) {
					int postPid = post.getPid();
					String postText = post.getText();
					String postAuthor = post.getAuthor();
					User author = new User(postAuthor);
					String authorNickname = author.getNickname();
					String profileLink = contextPath + "/Profile?type=public&username=" + postAuthor;
					String authorRank = author.getRankName();
					String avatar = author.getAvatar();
					int postPopularity = post.getPopularity();

					String postCreationTime = new Date(post.getCreationTime()) + "";

					String upvoteLink = contextPath + "/showThread?tid=" + threadTid + "&action=upvote&pid=" + postPid;
					String downvoteLink = contextPath + "/showThread?tid=" + threadTid + "&action=downvote&pid="
							+ postPid;
					String deleteLink = contextPath + "/showThread?tid=" + threadTid + "&action=delete&pid=" + postPid;
					String editLink = contextPath + "/editPost?pid=" + postPid;

					// Post text
					threadHtml += "<tr><td id=\"postText\" class=\"left\">";
					threadHtml += "<div>" + postText + "</div>";
					threadHtml += "</td><td id=\"edge\" class=\"right\">";

					// Post info
					threadHtml += "<a href=\"" + profileLink + "\" id=\"postAuthor\"><b>" + authorNickname + "</b></a>";
					threadHtml += "<div id=\"authorRank\">&nbsp;&nbsp;" + authorRank + "</div><br>";
					if (!avatar.equals(""))
						threadHtml += "<div><img src=\"" + avatar + "\"></div><br>";

					threadHtml += "<div id=\"postPopularity\">popularnost:&nbsp;&nbsp;<b>" + postPopularity
							+ "</b></div>";
					threadHtml += "<div id=\"postCreationTime\">vrijeme nastanka:&nbsp;&nbsp;<b>" + postCreationTime
							+ "</b></div>";
					threadHtml += "<a id=\"upvote\" href=\"" + upvoteLink + "\">[upvote]</a>&nbsp;";
					threadHtml += "<a id=\"downvote\" href=\"" + downvoteLink + "\">[downvote]</a>&nbsp;";
					if (checkPostAccess(postPid)) {
						threadHtml += "<a id=\"delete\" href=\"" + editLink + "\">[uredi]&nbsp;</a>";
						threadHtml += "<a id=\"delete\" href=\"" + deleteLink + "\">[obri&#353;i]</a>";
					}

					threadHtml += "</td></tr>";
				}
			}
		}

		// New post textfield
		if (!currentAccess.equals("anonymous")) {
			String newPostLink = contextPath + "/showThread?tid=" + tid;
			threadHtml += "<tr><td id=\"postText\" class=\"left\">";
			threadHtml += "<form method=\"post\" action=\"" + newPostLink + "\">";
			threadHtml += "<textarea id=\"newPostText\" name=\"text\" rows=\"5\" cols=\"60\">Novi post ...</textarea>";
			threadHtml += "<br><input type=\"submit\" value=\"post\">";
			threadHtml += "</form>";
			threadHtml += "</td></tr>";
		}

		out.println(threadHtml);
	}

	private void printRegister() {
		String registerHtml = "";
		String registerLink = contextPath + "/Register?action=check";

		registerHtml += "<tr><td id=\"content\" class=\"left\">";
		registerHtml += "<font size=\"1\"> Polja ozna&#269;ena s * su obavezna</font><br><br>";

		registerHtml += "<form id=\"register\" action=\"" + registerLink + "\" method=\"post\">";

		// Required fields
		registerHtml += "username:*<br><input type=\"text\" name=\"username\" required><br>";
		registerHtml += "password:*<br><input type=\"password\" name=\"password\" required><br>";
		registerHtml += "ponovi password:*<br><input type=\"password\" name=\"password2\" required><br>";
		registerHtml += "nadimak:*<br><input type=\"text\" name=\"nickname\" required><br>";
		registerHtml += "avatar (link):<br><input type=\"text\" name=\"avatar\"><br>";

		registerHtml += "ime:<br><input type=\"text\" name=\"name\"><br>";
		registerHtml += "prezime:<br><input type=\"text\" name=\"surname\"><br>";
		registerHtml += "spol:<br><input type=\"radio\" name=\"sex\" value=\"male\">Muski&nbsp;"
				+ "<input type=\"radio\" name=\"sex\" value=\"female\">Zenski&nbsp"
				+ "<input type=\"radio\" name=\"sex\" value=\"neutral\" checked>Ne znam<br>";
		registerHtml += "dob:<br><input type=\"text\" name=\"age\"><br>";
		registerHtml += "lokacija:<br><input type=\"text\" name=\"location\"><br>";
		registerHtml += "e-mail:<br><input type=\"text\" name=\"email\"><br>";
		registerHtml += "<br><input type=\"submit\" value=\"registriraj!\">";

		registerHtml += "</form>";

		registerHtml += "</td><td id=\"edge\"></td></tr>";
		out.println(registerHtml);
	}

	private void printText() {
		String textHtml = "";
		String text = (String) data;
		textHtml += "<tr><td id=\"content\" class=\"left\">";
		textHtml += "<fieldset>";
		textHtml += text;
		textHtml += "</fieldset>";
		textHtml += "</td><td id=\"edge\"></td></tr>";
		out.println(textHtml);
	}

	private void printCreateThread() {
		String createHtml = "";
		int cid = Integer.parseInt((String) data);
		String newThreadLink = contextPath + "/newThread?cid=" + cid;

		createHtml += "<tr><td id=\"content\" class=\"left\">";
		createHtml += "<form id=\"new\" action=\"" + newThreadLink + "\" method=\"post\">";

		// Thread part
		createHtml += "<fieldset><legend>Tema</legend>";
		createHtml += "&nbsp;&nbsp;naslov teme:<br>&nbsp;<input type=\"text\" name=\"threadTitle\" size=\"40\" required><br><br>";
		createHtml += "&nbsp;&nbsp;opis teme:<br>&nbsp;<textarea name=\"threadDescription\" cols=\"30\"></textarea>";
		createHtml += "</fieldset><br>";
		// Post part
		createHtml += "<fieldset><legend>Glavni post</legend>";
		createHtml += "&nbsp;&nbsp;tekst posta:<br>&nbsp;<textarea name=\"postText\" cols=\"34\" rows=\"5\" required></textarea>";
		createHtml += "</fieldset><br>";

		createHtml += "&nbsp;<input type=\"submit\" value=\"stvori\">";

		createHtml += "</form>";
		createHtml += "</td><td id=\"edge\"></td></tr>";
		out.println(createHtml);
	}

	private void printCreateCategory() {
		String createHtml = "";
		String newCategoryLink = contextPath + "/newCategory";

		createHtml += "<tr><td id=\"content\" class=\"left\">";
		createHtml += "<form id=\"new\" action=\"" + newCategoryLink + "\" method=\"post\">";

		// Category part
		createHtml += "<fieldset><legend>Kategorija</legend>";
		createHtml += "&nbsp;&nbsp;naslov kategorije:<br>&nbsp;<input type=\"text\" name=\"categoryTitle\" size=\"40\" required><br><br>";
		createHtml += "</fieldset><br>";

		// Thread part
		createHtml += "<fieldset><legend>Tema</legend>";
		createHtml += "&nbsp;&nbsp;naslov teme:<br>&nbsp;<input type=\"text\" name=\"threadTitle\" size=\"40\" required><br><br>";
		createHtml += "&nbsp;&nbsp;opis teme:<br>&nbsp;<textarea name=\"threadDescription\" cols=\"30\"></textarea>";
		createHtml += "</fieldset><br>";
		// Post part
		createHtml += "<fieldset><legend>Glavni post</legend>";
		createHtml += "&nbsp;&nbsp;tekst posta:<br>&nbsp;<textarea name=\"postText\" cols=\"34\" rows=\"5\" required></textarea>";
		createHtml += "</fieldset><br>";

		createHtml += "&nbsp;<input type=\"submit\" value=\"stvori\">";

		createHtml += "</form>";
		createHtml += "</td><td id=\"edge\"></td></tr>";
		out.println(createHtml);
	}

	private void printAdmin() {
		String adminHtml = "";
		ArrayList<User> users = fetchUsers();
		ArrayList<Category> categories = getCategories();
		String[] actionData = (String[]) data;
		String action = actionData[0];
		String actionUsername = actionData[1];

		adminHtml += "<tr><td id=\"content\" class=\"left\">";
		adminHtml += "<fieldset><legend>Korisnici</legend>";
		for (User user : users) {
			String username = user.getUsername();
			String nickname = user.getNickname();
			String profileLink = contextPath + "/Profile?type=public&username=" + username;
			String grantAccessLink;
			if (action.equals("grantAccessOn"))
				grantAccessLink = contextPath + "/Superuser?action=grantAccessOff";
			else
				grantAccessLink = contextPath + "/Superuser?action=grantAccessOn&username=" + username;

			String sendWarningLink;
			if (action.equals("sendMessageOn"))
				sendWarningLink = contextPath + "/Superuser?action=sendWarningOff";
			else
				sendWarningLink = contextPath + "/Superuser?action=sendWarningOn&username=" + username;

			String deleteUserLink = contextPath + "/Superuser?action=deleteUser&username=" + username;

			adminHtml += "<br><a id=\"user\" href =\"" + profileLink + "\">" + nickname + "</a><br>";
			adminHtml += "<a id=\"adminOption\" href =\"" + grantAccessLink + "\">[dodijeli ovlasti]</a>&nbsp;";
			adminHtml += "<a id=\"adminOption\" href =\"" + sendWarningLink + "\">[po&#353;alji opomenu]</a>&nbsp;";
			adminHtml += "<a id=\"adminOption\" href =\"" + deleteUserLink + "\">[obri&#353;i]</a>";

			if (action.equals("grantAccessOn") && username.equals(actionUsername)) {
				String link = contextPath + "/Superuser?action=grant&username=" + username;
				adminHtml += "<br><fieldset>";
				adminHtml += "<form method=\"post\" action=\"" + link + "\">";
				adminHtml += "<input type=\"radio\" name=\"accessLevel\" value=\"admin\" checked><font size=\"1\">administrator</font><br>";
				adminHtml += "<input type=\"radio\" name=\"accessLevel\" value=\"moderator\"><font size=\"1\">moderator</font><br>";
				adminHtml += "<br><select name=\"cid\">";
				for (Category category : categories) {
					int categoryCid = category.getCid();
					String categoryTitle = category.getTitle();
					adminHtml += "<option value=\"" + categoryCid + "\">" + categoryTitle + "</option>";
				}
				adminHtml += "</select>";
				adminHtml += "<br><br><input type=\"submit\" value=\"primijeni\">";
				adminHtml += "</form>";
				adminHtml += "</fieldset>";
			}

			if (action.equals("sendWarningOn") && username.equals(actionUsername)) {
				String link = contextPath + "/Superuser?action=send&username=" + username;
				adminHtml += "<br><fieldset>";
				adminHtml += "<form method=\"post\" action=\"" + link + "\">";
				adminHtml += "<textarea name=\"message\">Poruka ...</textarea><br><br>";
				adminHtml += "<input type=\"submit\" value=\"po&#353;alji\">";
				adminHtml += "</form>";
				adminHtml += "</fieldset>";
			}
		}
		adminHtml += "</fieldset>";
		adminHtml += "</td><td id=\"edge\"></td></tr>";
		out.println(adminHtml);

	}

	private void printEditPost() {
		int pid = Integer.parseInt((String) data);
		Post post = new Post(pid);
		int tid = post.getTid();
		String text= post.getText();
		
		String editPostText = "";
		String postLink = contextPath + "/showThread?tid="+tid+"&action=editPost&pid="+pid;
		
		editPostText+= "<tr><td id=\"content\" class=\"left\">";
		editPostText+= "<fieldset>";
		editPostText+= "<legend>Tekst objave</legend>";
		editPostText+= "<form action=\""+postLink+"\" method=\"post\">";
		editPostText+= "<textarea name=\"postText\" cols=\"34\" rows=\"5\" required>"+text+"</textarea>";
		editPostText+= "<br><br>&nbsp;<input type=\"submit\" value=\"potvrdi\">";
		editPostText+= "</form>";
		editPostText+= "</fieldset>";
		editPostText+= "</td><td id=\"edge\" class=\"right\"></tr>";
		
		out.println(editPostText);
	}

	private ArrayList<User> fetchUsers() {
		ArrayList<User> users = new ArrayList<User>();

		Database db = new Database();
		String sql = "SELECT username FROM users";
		ResultSet results = db.select(sql);
		try {
			while (results.next()) {
				String username = results.getString(1);
				User user = new User(username);
				Flags flags = new Flags(user.getFlags());
				if (flags.isActivity())
					users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();

		return users;
	}

	private boolean checkCategoryAccess(int cid) {
		String accessLevel = currentUser.getAccessLevel();

		// Admin
		if (accessLevel.equals("admin"))
			return true;

		return false;
	}

	private boolean checkThreadCreateAccess(int cid) {
		String accessLevel = currentUser.getAccessLevel();
		String username = currentUser.getUsername();
		// Admin
		if (accessLevel.equals("admin"))
			return true;

		// Moderator
		Category category = new Category(cid);
		if (category.isModerator(username))
			return true;

		// User
		if (accessLevel.equals("user"))
			return true;

		return false;

	}

	private boolean checkThreadAccess(int tid) {
		String accessLevel = currentUser.getAccessLevel();
		String username = currentUser.getUsername();

		// Admin
		if (accessLevel.equals("admin"))
			return true;

		// Moderator
		Thread thread = new Thread(tid);
		if (thread.isModerator(username))
			return true;

		return false;
	}

	private boolean checkPostAccess(int pid) {
		Post post = new Post(pid);
		String author = post.getAuthor();
		String username = currentUser.getUsername();
		String accessLevel = currentUser.getAccessLevel();
		int cid = post.getCid();

		// Main post
		if (post.getMain() == 1)
			return false;

		// Author
		if (username.equals(author))
			return true;

		// Moderator
		Category category = new Category(cid);
		if (category.isModerator(username))
			return true;

		// Administrator
		if (accessLevel.equals("admin"))
			return true;

		// No access
		return false;
	}

	private ArrayList<Category> getCategories() {
		ArrayList<Category> categories = new ArrayList<Category>();
		Database db = new Database();
		String sql = "SELECT cid FROM categories";
		ResultSet results = db.select(sql);
		try {
			while (results.next()) {
				int cid = results.getInt(1);
				Category category = new Category(cid);
				categories.add(category);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();

		return categories;
	}

	private ArrayList<Thread> getThreads() {
		ArrayList<Thread> threads = new ArrayList<Thread>();
		Database db = new Database();
		String sql = "SELECT tid FROM threads";
		ResultSet results = db.select(sql);
		try {
			while (results.next()) {
				int tid = results.getInt(1);
				Thread thread = new Thread(tid);
				threads.add(thread);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();

		Collections.reverse(threads);

		return threads;
	}

	private ArrayList<Post> getPosts(int tid) {
		ArrayList<Post> postList = new ArrayList<Post>();
		Database db = new Database();
		String sql = "SELECT pid FROM posts WHERE tid=" + tid;
		ResultSet results = db.select(sql);
		try {
			while (results.next()) {
				int pid = results.getInt(1);
				Post post = new Post(pid);
				postList.add(post);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();

		// Sort
		String sort = "";
		sort = request.getParameter("sort");
		if (sort == null)
			sort = "";

		if (sort.equals("up"))
			Collections.sort(postList, new sortUp());
		if (sort.equals("down"))
			Collections.sort(postList, new sortDown());
		if (sort.equals("popularity"))
			Collections.sort(postList, new sortPopularity());

		return postList;
	}

	private String getAbsolutePath() {
		String path = "<a href=\"" + contextPath + "/Homepage\" id=\"pathLink\">Po&#269;etna</a> > ";

		if (type == TYPE.showThread) {

			// Find titles
			tid = Integer.parseInt((String) data);
			Thread currentThread = new Thread(tid);
			String currentThreadTitle = currentThread.getTitle();
			int cid = currentThread.getCid();
			Category currentCategory = new Category(cid);
			String currentCategoryTitle = currentCategory.getTitle();

			// Set path
			path += currentCategoryTitle + " > " + currentThreadTitle;
		} else
			path += pageTitle;
		return path;
	}

	private String getUserOptions() {
		String userOptions = "";
		String registerLink = contextPath + "/Register";
		String warningLink = contextPath + "/ReadWarning";
		// Check access level
		if (currentAccess.equals("anonymous")) {
			userOptions += "Pretra&#382;ujete forum kao anonimni korisnik.&nbsp;" + "<a id=\"userOptionsText\" href=\""
					+ registerLink + "\">[registriraj se]</a>";
			return userOptions;
		} else {
			if (checkWarnings())
				userOptions += "<a href=\"" + warningLink
						+ "\"><font color=\"red\"><b>Imate upozorenje !</b></font></a>&nbsp;&nbsp;";
			userOptions += "<a id=\"userOptionsText\" href=\"" + contextPath + "/Profile?type=private\">[profil]</a>"
					+ "&nbsp;&nbsp;";
		}
		if (currentAccess.equals("moderator") || currentAccess.equals("admin"))
			userOptions += "<a id=\"userOptionsText\" href=\"" + contextPath + "/Superuser\">[dodatne opcije]</a>"
					+ "&nbsp;&nbsp;";

		userOptions += "<a id=\"userOptionsText\" href=\"" + contextPath + "/Homepage?action=logout\">[odjava]</a>";

		return userOptions;
	}

	private boolean checkWarnings() {
		String username = currentUser.getUsername();
		Warning warning = new Warning(username);
		return warning.isExists();
	}

	private String getLatestPost() {
		String latestPostHtml = "";
		Post latestPost = new Post();
		String postAuthor = latestPost.getAuthor();
		String creationTime = new Date(latestPost.getCreationTime()) + "";

		latestPostHtml += "<div id=\"postAuthor\"><b>" + postAuthor + "</b></div>";
		latestPostHtml += "<div id=\"postCreationTime\">" + creationTime + "</div>";

		return latestPostHtml;

	}

	private String getLatestThread() {
		String latestThreadHtml = "";
		Thread latestThread = new Thread();
		String threadTitle = latestThread.getTitle();
		String threadDescription = latestThread.getDescription();
		int threadTid = latestThread.getTid();
		String threadLink = contextPath + "/showThread?tid=" + threadTid;

		latestThreadHtml += "<a href=\"" + threadLink + "\" id=\"threadTitle\">" + threadTitle + "</div>";
		latestThreadHtml += "<div id=\"threadDescription\">" + threadDescription + "</div>";

		return latestThreadHtml;
	}

}

// SORTING PART

class sortUp implements Comparator<Post> {

	@Override
	public int compare(Post object1, Post object2) {
		long pop1 = object1.getCreationTime();
		long pop2 = object2.getCreationTime();

		if (pop1 < pop2)
			return 1;
		if (pop1 > pop2)
			return -1;
		else
			return 0;
	}
}

class sortDown implements Comparator<Post> {

	@Override
	public int compare(Post object1, Post object2) {
		long pop1 = object1.getCreationTime();
		long pop2 = object2.getCreationTime();

		if (pop1 > pop2)
			return 1;
		if (pop1 < pop2)
			return -1;
		else
			return 0;
	}
}

class sortPopularity implements Comparator<Post> {

	@Override
	public int compare(Post object1, Post object2) {
		int pop1 = object1.getPopularity();
		int pop2 = object2.getPopularity();

		if (pop1 < pop2)
			return 1;
		if (pop1 > pop2)
			return -1;
		else
			return 0;
	}
}