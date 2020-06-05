import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@WebFilter(filterName = "MeasureFilter", urlPatterns = "/api/movielist")
public class MeasureFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

        HttpServletRequest httpRequest = (HttpServletRequest) req;

        String contextPath = httpRequest.getServletContext().getRealPath("/");
        String xmlFilePath = contextPath + "ts.txt";

        File myfile = new File(xmlFilePath);
        FileWriter out = new FileWriter(myfile, true);

        long startTime = System.nanoTime();

        chain.doFilter(req, resp);

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;

        out.write(elapsedTime + "\n");

        out.close();
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
