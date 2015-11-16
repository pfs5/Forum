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
import hr.fer.objects.Category;
import hr.fer.objects.Thread;
import hr.fer.objects.User;

@WebServlet({ "/NewThread", "/newThread" })
public class NewThread extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private int cid;
	private User currentUser;

	public NewThread() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		String username = (String) session.getAttribute("username");
		currentUser = new User(username);
		String cidString = request.getParameter("cid");
		cid = Integer.parseInt(request.getParameter("cid"));

		// Thread form
		HtmlOutput output;
		if (checkThreadAccess(cid)) 
			output = new HtmlOutput(request, response, "Nova tema", TYPE.createThread, cidString);
		else
			output = new HtmlOutput(request, response, "Nova tema", TYPE.createThread, cidString);
		output.printPage();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		String username = (String) session.getAttribute("username");
		
		cid = Integer.parseInt(request.getParameter("cid"));
		String threadTitle = request.getParameter("threadTitle");
		String threadDescription = request.getParameter("threadDescription");
		String postText = request.getParameter("postText");
		

		Thread thread = new Thread(threadTitle, threadDescription, cid, postText, username);
		HtmlOutput output = new HtmlOutput(request, response, "Uspjeh!", TYPE.text, "Tema stvorena !");
		output.printPage();
		
	}

	private boolean checkThreadAccess(int cid) {
		String accessLevel = currentUser.getAccessLevel();
		String username = currentUser.getUsername();

		// Admin
		if (accessLevel.equals("admin"))
			return true;

		// Moderator
		Category category = new Category(cid);
		if (category.isModerator(username))
			return true;

		//User
		if (accessLevel.equals("user"))
			return true;
		
		return false;
	}

}
