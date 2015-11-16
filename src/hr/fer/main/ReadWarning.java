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

/**
 * Servlet implementation class ReadWarning
 */
@WebServlet("/ReadWarning")
public class ReadWarning extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ReadWarning() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String username = (String) session.getAttribute("username");
		User currentUser = new User(username);
		
		Warning warning = new Warning(username);
		String message = warning.getMessage();
		message+= "<br><br><a id=\"userOptionsText\" href=\""+request.getContextPath() + "/Homepage\">Razumijem.</a>";
		warning.delete();
		
		HtmlOutput output = new HtmlOutput(request, response, "Upozorenje!", TYPE.text, message);
		output.printPage();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
