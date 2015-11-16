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
import hr.fer.objects.User;

@WebServlet({ "/NewCategory", "/newCategory"})
public class NewCategory extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private User currentUser;
	
    public NewCategory() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String username = (String) session.getAttribute("username");
		currentUser = new User(username);

		// Thread form
		if (checkCategoryAccess()) {
			HtmlOutput output = new HtmlOutput(request, response, "Nova kategorija", TYPE.createCategory, null);
			output.printPage();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String username = (String) session.getAttribute("username");
		
		String categoryTitle = request.getParameter("categoryTitle");
		String threadTitle = request.getParameter("threadTitle");
		String threadDescription = request.getParameter("threadDescription");
		String postText = request.getParameter("postText");
		
		Category category = new Category(categoryTitle, threadTitle, threadDescription, postText, username);
		HtmlOutput output = new HtmlOutput(request, response, "Uspjeh!", TYPE.text, "Kategorija stvorena !");
		output.printPage();
	}

	public boolean checkCategoryAccess () {
		String accessLevel = currentUser.getAccessLevel();
		if (accessLevel.equals("admin"))
			return true;
		return false;
	}
}
