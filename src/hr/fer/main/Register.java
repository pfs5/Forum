package hr.fer.main;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hr.fer.html.HtmlOutput;
import hr.fer.html.HtmlOutput.TYPE;
import hr.fer.objects.Database;
import hr.fer.objects.User;

@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Flags
	private String flags = null;

	public Register() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HtmlOutput output = new HtmlOutput(request, response, "Registracija", TYPE.register, flags);
		output.printPage();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = (String) request.getAttribute("action");

		// Get form
		String username = (String) request.getParameter("username");
		String password = (String) request.getParameter("password");
		String password2 = (String) request.getParameter("password2");
		String nickname = (String) request.getParameter("nickname");
		String avatar = (String) request.getParameter("avatar");
		String name = (String) request.getParameter("name");
		String surname = (String) request.getParameter("surname");
		String sex = (String) request.getParameter("sex");
		String age = (String) request.getParameter("age");
		String location = (String) request.getParameter("location");
		String email = (String) request.getParameter("email");

		// Check fields
		int usernameFlag = checkUsername(username);
		int passwordFlag = checkPassword(password, password2);
		int nicknameFlag = checkNickname(nickname);

		flags = usernameFlag + "" + passwordFlag + "" + nicknameFlag;

		if (name.equals(""))
			name = "Nepoznato";
		if (surname.equals(""))
			surname = "Nepoznato";
		if (age.equals(""))
			age = "0";
		if (location.equals(""))
			location = "Nepoznato";
		if (email.equals(""))
			email = "Nepoznato";
		
		if (flags.equals("000")) {
			User newUser = new User(username, password2, nickname, avatar, sex, name, surname, Integer.parseInt(age), location, email, "0000000001", 0, 1, "user");
			HtmlOutput output = new HtmlOutput(request, response, "Uspjeh!", TYPE.text, "Uspje&#353;na registracija !");
			output.printPage();
		}
		else 
			doGet(request, response);
	}

	private int checkUsername(String username) {
		Database db = new Database();
		String sql = "SELECT username FROM users where username='" + username + "'";
		ResultSet results = db.select(sql);
		try {
			if (results.next())
				return 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();
		return 0;

	}

	private int checkPassword(String password1, String password2) {
		if (password1.equals(password2))
			return 0;
		else
			return 1;
	}

	private int checkNickname(String nickname) {
		Database db = new Database();
		String sql = "SELECT nickname FROM users where username='" + nickname + "'";
		ResultSet results = db.select(sql);
		try {
			if (results.next())
				return 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();
		return 0;
	}

}
