package hr.fer.main;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.derby.iapi.error.PassThroughException;

import hr.fer.html.HtmlOutput;
import hr.fer.html.HtmlOutput.TYPE;
import hr.fer.objects.Category;
import hr.fer.objects.Database;
import hr.fer.objects.Flags;
import hr.fer.objects.Password;
import hr.fer.objects.Thread;
import hr.fer.objects.User;

@WebServlet({ "/Homepage", "/index.html", "/homepage" })
public class Homepage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Http data
	HttpServletRequest request;
	HttpServletResponse response;

	//Action data
	private int cid;
	private int tid;
	private User currentUser;
	
	public Homepage() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.request = request;
		this.response = response;
		
		HttpSession session = request.getSession();
		String username = (String) session.getAttribute("username");
		currentUser = new User(username);

		String action = (String) request.getParameter("action");
		if (action == null)
			action = "";

		//Check logout
		if (action.equals("logout"))
			logout();
		
		//Check deletions
		if (action.equals("deleteThread")) {
			tid = Integer.parseInt(request.getParameter("tid")); 
			Thread thread = new Thread(tid);
			if (checkThreadAccess(tid))
				thread.delete();
		}
		
		if (action.equals("deleteCategory")) {
			cid = Integer.parseInt(request.getParameter("cid")); 
			Category category = new Category(cid);
			if (checkCategoryAccess(cid))
				category.delete();
		}

		HtmlOutput output = new HtmlOutput(request, response, "Homepage", TYPE.homepage, null);
		output.printPage();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.request = request;
		this.response = response;

		String action = (String) request.getParameter("action");
		if (action.equals("login"))
			checkLogin();
		if (action.equals("logout"))
			logout();
		doGet(request, response);
	}

	private void checkLogin() {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		User user = new User(username);

		// Check if login is correct
		Database db = new Database();
		String sql = "SELECT password FROM users WHERE username='" + username + "'";
		ResultSet results = db.select(sql);
		try {
			while (results.next()) {
				// Check activity
				String flags = user.getFlags();
				Flags f = new Flags(flags);
				if (!f.isActivity())
					return;
				// Check password
				Password pw = new Password();
				String correctPassword = pw.decode(results.getString(1));
				if (password.equals(correctPassword)) {
					login(username);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();
	}

	private void login(String username) {
		HttpSession session = request.getSession();
		session.invalidate();
		session = request.getSession(true);
		session.setAttribute("username", username);
		session.setMaxInactiveInterval(300);
	}

	private void logout() {
		HttpSession session = request.getSession(true);
		session.invalidate();
	}

	private boolean checkCategoryAccess(int cid) {
		String accessLevel = currentUser.getAccessLevel();

		// Admin
		if (accessLevel.equals("admin"))
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
	
}
