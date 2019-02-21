package app.mrquan.tomcat.jsp;

import app.mrquan.tomcat.http.*;
import java.io.*;
import app.mrquan.tomcat.test.pojo.Book;

public class AsJSP extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream()));
		response.setContentType("text/html;charset=UTF-8");
		out.println("<html>");
		out.println("<head>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>hello JSP</h1>");
	    Book book = new Book("Thinking in java",1);
	    out.print(book);
	    out.print(request.getParameter("name"));
		out.println("</body>");
		out.println("</html>");
		out.flush();
	}
}
