package hr.fer.main;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import hr.fer.html.HtmlOutput;
import hr.fer.html.HtmlOutput.TYPE;
import hr.fer.objects.User;

@WebServlet("/Profile")
public class Profile extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Html variables
	HttpServletRequest request;
	HttpServletResponse response;

	public Profile() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.request = request;
		this.response = response;

		String username = request.getParameter("username");
		String type = request.getParameter("type");

		if (type == null) {
			HttpSession session = request.getSession();
			username = (String) session.getAttribute("username");
			User user = new User(username);

			// Apply flag changes
			applyFlags(user);
			
			HtmlOutput output = new HtmlOutput(request, response, "Korisni&#269;ki profil", TYPE.privateProfile, user);
			output.printPage();
		}

		else if (type.equals("private")) {
			HttpSession session = request.getSession();
			username = (String) session.getAttribute("username");
			User user = new User(username);

			HtmlOutput output = new HtmlOutput(request, response, "Korisni&#269;ki profil", TYPE.privateProfile, user);
			output.printPage();
		}

		else if (type.equals("public")) {
			User user = new User(username);
			HtmlOutput output = new HtmlOutput(request, response, "Korisni&#269;ki profil", TYPE.publicProfile, user);
			output.printPage();
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	private void applyFlags(User user) {
		char username = flagToChar(request.getParameter("usernameF"));
		char name = flagToChar(request.getParameter("nameF"));
		char surname = flagToChar(request.getParameter("surnameF"));
		char age = flagToChar(request.getParameter("ageF"));
		char location = flagToChar(request.getParameter("locationF"));
		char email = flagToChar(request.getParameter("emailF"));
		char popularity = flagToChar(request.getParameter("popularityF"));
		char rank = flagToChar(request.getParameter("rankF"));
		char accessLevel = flagToChar(request.getParameter("accessF"));

		String flags = username + "" + name + "" + surname + "" + age + "" + location + "" + email + "" + popularity
				+ "" + rank + "" + accessLevel;
		user.setFlags(flags);
	}

	private char flagToChar(String flag) {
		if (flag == null)
			return '0';
		if (flag.equals("on"))
			return '1';
		return '2';
	}

}
