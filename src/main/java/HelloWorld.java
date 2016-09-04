import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * Created by reinier.guerra on 8/13/16.
 */
public class HelloWorld extends HttpServlet {

    private final String message = "Hello";

    public void init() throws ServletException {
        // Do required initialization
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {
        String thing = request.getParameter("thing");
        // Set response content type
        response.setContentType("text/html");

        // Actual logic goes here.
        PrintWriter out = response.getWriter();
        out.println(String.format("<h4>%s %s</h4>", message, (thing == null) ? "World" : thing));
    }

    public void destroy() {
        // do nothing.
    }
}