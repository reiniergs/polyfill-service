package App;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Jose David Rodriguez <jodarove@gmail.com> on 9/19/16.
 */
public class PolyfillServiceServletTest {
    private HttpServletRequest request;
    private HttpServletResponse response;

    @Before
    public void setUp() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    public void testCanGetMinifyParameter() throws Exception {
        StringWriter writerOutput = setMinifiedTestPreconditions("");

        (new PolyfillServiceServlet()).doGet(request, response);

        assertMinifiedTestResults(writerOutput, true);
    }

    @Test
    public void testMinifyParameterIsFalseWhenNotPresent() throws Exception {
        StringWriter writerOutput = setMinifiedTestPreconditions(null);

        (new PolyfillServiceServlet()).doGet(request, response);

        assertMinifiedTestResults(writerOutput, false);
    }

    private StringWriter setMinifiedTestPreconditions(String minifyPresent) throws IOException {
        when(request.getParameter("minify")).thenReturn(minifyPresent);

        StringWriter writerOutput = new StringWriter();
        PrintWriter writer = new PrintWriter(writerOutput);

        when(response.getWriter()).thenReturn(writer);
        
        return writerOutput;
    }

    private void assertMinifiedTestResults(StringWriter writerOutput, boolean minify) {
        verify(request, atLeast(1)).getParameter("minify");
        assertTrue(writerOutput.toString().contains(String.format("let minified = %s;", minify)));
    }
}