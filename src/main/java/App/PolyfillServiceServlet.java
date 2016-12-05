package App;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Jose David Rodriguez <jodarove@gmail.com> on 9/19/16.
 */
public class PolyfillServiceServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean minified = isMinified(request);

        response.setContentType("text/javascript");
        PrintWriter out = response.getWriter();

        out.println(String.format("let minified = %s;", minified));

        out.println("console.log(\"The polyfill service with parameters:\");");
        out.println("console.log(\"Minified:\", minified);");
    }

    private boolean isMinified(HttpServletRequest request) {
        String minify = request.getParameter("minify");

        boolean minified = true;

        if (minify == null) {
            minified = false;
        }
        return minified;
    }
}
