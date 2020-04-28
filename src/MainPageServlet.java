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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "MainPageServlet", urlPatterns = "/api/mainpage")
public class MainPageServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedbexample")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();



        try {
            Connection dbcon = dataSource.getConnection();
            Statement statement = dbcon.createStatement();

            String query = "SELECT * FROM genres;";



            ResultSet rs = statement.executeQuery(query);
            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                String genre_id = rs.getString("id");
                String genre_name = rs.getString("name");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("genre_id", genre_id);
                jsonObject.addProperty("genre_name", genre_name);

                jsonArray.add(jsonObject);
            }

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
}

