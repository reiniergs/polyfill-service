import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.io.*;
import javax.servlet.http.*;
import org.junit.Test;

/**
 * Created by jodarove on 9/1/16.
 */
public class HelloWorldTest {
    private HttpServletRequest request;
    private HttpServletResponse response;

    @Before
    public void setUp() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    public void ShouldSayHelloToThingWhenThingIsPassed() throws Exception {

        when(request.getParameter("thing")).thenReturn("Reinier");

        StringWriter writerOutput = new StringWriter();
        PrintWriter writer = new PrintWriter(writerOutput);

        when(response.getWriter()).thenReturn(writer);

        new HelloWorld().doGet(request, response);

        verify(request, atLeast(1)).getParameter("thing");
        assertTrue(writerOutput.toString().contains("Hello Reinier"));
    }

    @Test
    public void ShouldSayHelloToWorldWhenThingIsNotPassed() throws Exception {

        when(request.getParameter("thing")).thenReturn(null);

        StringWriter writerOutput = new StringWriter();
        PrintWriter writer = new PrintWriter(writerOutput);

        when(response.getWriter()).thenReturn(writer);

        new HelloWorld().doGet(request, response);

        verify(request, atLeast(1)).getParameter("thing");
        assertTrue(writerOutput.toString().contains("Hello World"));
    }

}