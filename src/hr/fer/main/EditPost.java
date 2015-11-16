package hr.fer.main;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hr.fer.html.HtmlOutput;
import hr.fer.html.HtmlOutput.TYPE;
import hr.fer.objects.Post;

@WebServlet({"/EditPost","/editPost"})
public class EditPost extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public EditPost() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int pid = Integer.parseInt(request.getParameter("pid"));
		Post post = new Post(pid);
		int tid = post.getTid();
		
		HtmlOutput output = new HtmlOutput(request, response, "Uredi objavu", TYPE.editPost, pid+"");
		output.printPage();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
