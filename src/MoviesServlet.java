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

@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedbexample")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Connection dbcon = dataSource.getConnection();
            Statement statement = dbcon.createStatement();

            String query = "SELECT m.id as 'id', m.title as 'title', m.year as 'year', m.director as 'director', FORMAT(AVG(r.rating),1) 'rating',\n" +
                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name SEPARATOR ', '), ',',3) 'genre',\n" +
                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.name SEPARATOR ', '), ',',3) 'stars',\n" +
                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.id SEPARATOR ', '), ',',3) 'starsId'\n" +
                    "FROM movies m JOIN ratings r JOIN genres_in_movies gin\n" +
                    "    JOIN genres g JOIN stars_in_movies sim JOIN stars s\n" +
                    "WHERE m.id = r.movieId AND sim.starId = s.id AND sim.movieID =m.id\n" +
                    "    AND g.id = gin.genreId AND gin.movieId = m.id\n" +
                    "GROUP BY r.movieId\n" +
                    "ORDER BY AVG(r.rating) DESC\n" +
                    "LIMIT 20\n;";

            String query2 = "SELECT s.name FROM stars s WHERE id = ?";
            ResultSet rs = statement.executeQuery(query);
            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_genre = rs.getString("genre");
                Double movie_rating = rs.getDouble("rating");

                String parseId[] = rs.getString("starsId").split(", ");
                JsonObject jsonObject = new JsonObject();

                int i = 0;
                for(String pos: parseId)
                {
                    PreparedStatement statement2 = dbcon.prepareStatement(query2);


                    statement2.setString(1, pos);
                    ResultSet rs2 = statement2.executeQuery();
                    rs2.next();
                    String name = rs2.getString("name");

                    jsonObject.addProperty("star_name" + i, name);
                    jsonObject.addProperty("star_id" + i, pos);
                    i++;
                }

                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_genre", movie_genre);
                jsonObject.addProperty("movie_rating", movie_rating);

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
