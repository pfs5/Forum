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
import hr.fer.objects.Warning;

@WebServlet("/Superuser")
public class Superuser extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private User currentUser;

	public Superuser() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		String username = (String) session.getAttribute("username");
		currentUser = new User(username);

		String accessLevel = currentUser.getAccessLevel();
		if (accessLevel.equals("admin")) {
			String action = request.getParameter("action");
			String actionUsername = request.getParameter("username");
			String[] data = new String[10];
			if (action == null)
				action = "";

			data[0] = action;
			data[1] = actionUsername;

			if (action.equals("deleteUser")) {
				String usernameDelete = request.getParameter("username");
				User userDelete = new User(usernameDelete);
				if (!username.equals(usernameDelete))
					userDelete.delete();
			}

			HtmlOutput output = new HtmlOutput(request, response, "Admin", TYPE.admin, data);
			output.printPage();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");

		String action = request.getParameter("action");
		// Grant access
		if (action.equals("grant")) {
			String accessLevel = request.getParameter("accessLevel");
			int cid = Integer.parseInt(request.getParameter("cid"));

			if (accessLevel.equals("admin")) {
				User user = new User(username);
				user.setAccessLevel("admin");
			}
			if (accessLevel.equals("moderator")) {
				User user = new User(username);
				user.setAccessLevel("moderator");
				user.setModerator(cid);
			}
		} else if (action.equals("send")) {
			String message = request.getParameter("message");
			Warning warning = new Warning(username, message);
		}

		doGet(request, response);
	}

}
