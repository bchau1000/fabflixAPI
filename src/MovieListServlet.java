import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedbexample")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String name = request.getParameter("genre");
        String title = request.getParameter("title");
        String page = request.getParameter("page");
        int n = Integer.parseInt(page);

        try {
            Connection dbcon = dataSource.getConnection();
            Statement statement = dbcon.createStatement();
            Statement statementC = dbcon.createStatement();

            String query = "SELECT *\n" +
                    "FROM movielist\n" +
                    "WHERE genre LIKE '%" + name + "%'\n" +
                    "AND title LIKE '%" + title + "%'\n" +
                    "LIMIT 100\n" +
                    "OFFSET " + (n - 1) * 100 + ";";

            String rowCount = "SELECT count(*) as count\n" +
                    "FROM movielist\n" +
                    "WHERE genre LIKE '%" + name + "%'" +
                    "AND title LIKE '%" + title + "%';";

            String query2 = "SELECT s.name FROM stars s WHERE id = ?";

            ResultSet queryCount = statementC.executeQuery(rowCount);
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            if(queryCount.next())
            {
                String count = queryCount.getString("count");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("query_count", count);

                jsonArray.add(jsonObject);
            }

            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String genre_name = rs.getString("genre");
                String movie_dir = rs.getString("director");
                String movie_starsId = rs.getString("starsId");
                String movie_yr = rs.getString("year");

                String parseId[] = rs.getString("starsId").split(", ");

                JsonObject jsonObject = new JsonObject();

                for(int i = 0; i < parseId.length; i++)
                {
                    PreparedStatement statement2 = dbcon.prepareStatement(query2);

                    statement2.setString(1, parseId[i]);
                    ResultSet rs2 = statement2.executeQuery();
                    rs2.next();

                    String star_name = rs2.getString("name");

                    jsonObject.addProperty("star_name" + i, star_name);
                    jsonObject.addProperty("star_id" + i, parseId[i]);
                }

                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("genre_name", genre_name);
                jsonObject.addProperty("movie_dir", movie_dir);
                jsonObject.addProperty("movie_starsId", movie_starsId);
                jsonObject.addProperty("movie_yr", movie_yr);

                jsonArray.add(jsonObject);
            }

            out.write(jsonArray.toString());
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
        }
        out.close();
    }
}
