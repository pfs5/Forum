package hr.fer.main;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import hr.fer.html.HtmlOutput;
import hr.fer.html.HtmlOutput.TYPE;
import hr.fer.objects.Category;
import hr.fer.objects.Post;
import hr.fer.objects.User;

@WebServlet({ "/ShowThread", "/showThread" })
public class ShowThread extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private User currentUser;
	private String tid;

	public ShowThread() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Current user
		HttpSession session = request.getSession();
		String username = (String) session.getAttribute("username");
		currentUser = new User(username);

		tid = request.getParameter("tid");
		String action = request.getParameter("action");
		if (action == null)
			action = "";

		// Check voting
		if (action.equals("upvote")) {
			int pid = Integer.parseInt(request.getParameter("pid"));
			Post post = new Post(pid);
			post.vote("upvote", username);
		}
		if (action.equals("downvote")) {
			int pid = Integer.parseInt(request.getParameter("pid"));
			Post post = new Post(pid);
			post.vote("downvote", username);
		}

		// Check delete
		if (action.equals("delete")) {
			int pid = Integer.parseInt(request.getParameter("pid"));
			Post post = new Post(pid);
			if (checkPostAccess(pid)) {
				post.delete();
			}
		}

		HtmlOutput output = new HtmlOutput(request, response, "Tema", TYPE.showThread, tid);
		output.printPage();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String text = request.getParameter("text");
		HttpSession session = request.getSession();

		String author = (String) session.getAttribute("username");
		long creationTime = System.currentTimeMillis();
		int popularity = 0;
		int main = 0;
		int tid = Integer.parseInt(request.getParameter("tid"));
		Post post = new Post(text, author, creationTime, popularity, main, tid);
		doGet(request, response);

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

}
