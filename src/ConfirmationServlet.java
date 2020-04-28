import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedbexample")
    private DataSource dataSource;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();

          try {
              Connection dbcon = dataSource.getConnection();
              Statement statement = dbcon.createStatement();

              DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
              Date dateobj = new Date();

              int lastSale = Integer.parseInt("" + session.getAttribute("mostRecentSale"));
              String custId = "" + session.getAttribute("custSessionId");

              String salesQuery = "SELECT s.id, s.customerId, m.title, s.saleDate\n" +
                      "FROM sales s JOIN movies m\n" +
                      "WHERE s.movieId = m.id && s.id > " + lastSale + "\n" +
                      "&& customerId = '" + custId + "'\n" +
                      "&& saleDate ='" + df.format(dateobj) + "'\n" +
                      "ORDER BY s.id DESC;";

              ResultSet rs = statement.executeQuery(salesQuery);
              JsonArray jsonArray = new JsonArray();

              while (rs.next()) {
                  String movie_title = rs.getString("title");
                  String sale_id = rs.getString("id");

                  JsonObject jsonObject = new JsonObject();
                  jsonObject.addProperty("movie_title", movie_title);
                  jsonObject.addProperty("sale_id", sale_id);

                  jsonArray.add(jsonObject);
              }

              ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
              previousItems.clear();

              out.write(jsonArray.toString());
              dbcon.close();
              response.setStatus(200);

          } catch (Exception e) {
              JsonObject jsonObject = new JsonObject();
              jsonObject.addProperty("errorMessage", e.getMessage());
              out.write(jsonObject.toString());

              response.setStatus(500);

        }
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
