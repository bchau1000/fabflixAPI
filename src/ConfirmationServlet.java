import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.Context;
import javax.naming.InitialContext;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();

          try {
              Context initCtx = new InitialContext();
              Context envCtx = (Context) initCtx.lookup("java:comp/env");
              DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");
              Connection dbcon = ds.getConnection();

              DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
              Date dateobj = new Date();

              int lastSale = Integer.parseInt("" + session.getAttribute("mostRecentSale"));
              String custId = "" + session.getAttribute("custSessionId");

              String salesQuery = "SELECT s.id, s.customerId, m.title, s.saleDate\n" +
                      "FROM sales s JOIN movies m\n" +
                      "WHERE s.movieId = m.id && s.id > ?\n" +
                      "&& customerId = ? \n" +
                      "&& saleDate = ? \n" +
                      "ORDER BY s.id DESC;";

              PreparedStatement statement = dbcon.prepareStatement(salesQuery);
              statement.setInt(1, lastSale);
              statement.setString(2, custId);
              statement.setString(3, df.format(dateobj));

              ResultSet rs = statement.executeQuery();
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
