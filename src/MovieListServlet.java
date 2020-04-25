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
import java.util.ArrayList;

@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedbexample")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        // JsonArray is converted to a string, and written out to response, which is send to a specified javascript file
        PrintWriter out = response.getWriter();

        String title = request.getParameter("title");
        if(title.indexOf('<') != -1) { title = title.replace('<','%');}
        else if (!title.equals(">")){ title = '%' + title + '%';}

        String stringYear = request.getParameter("year");
        int year = 0;
        if(!stringYear.isEmpty()) { year = Integer.parseInt(stringYear); }

        String stringCount = request.getParameter("count");
        int resultCount = Integer.parseInt(stringCount);

        String page = request.getParameter("page");
        int pageNum = Integer.parseInt(page);

        String director = request.getParameter("director");
        String star = request.getParameter("star");
        String genre = request.getParameter("genre");
        String sort1 = getSort(request.getParameter("sort1"));
        String sort2 = getSort(request.getParameter("sort2"));

        try {
            Connection dbcon = dataSource.getConnection();
            Statement statement = dbcon.createStatement();
            Statement statementC = dbcon.createStatement();

            String query = "";
            String rowCount = "";

            if(year < 1000 || year > 9999)
            {
                if(!title.equals(">")) {
                    query = "SELECT *\n" +
                            "FROM movielist\n" +
                            "WHERE genre LIKE '%" + genre + "%'\n" +
                            "AND title LIKE '" + title + "'\n" +
                            "AND director LIKE '%" + director + "%'\n" +
                            "AND stars LIKE '%" + star + "%'\n" +
                            "ORDER BY " + sort1 + ", " + sort2 + "\n" +
                            "LIMIT " + resultCount + "\n" +
                            "OFFSET " + (pageNum - 1) * resultCount + ";";

                    rowCount = "SELECT count(*) as count\n" +
                            "FROM movielist\n" +
                            "WHERE genre LIKE '%" + genre + "%'" +
                            "AND director LIKE '%" + director + "%'\n" +
                            "AND stars LIKE '%" + star + "%'\n" +
                            "AND title LIKE '" + title + "';";
                }
                else
                {
                    query = "SELECT *\n" +
                            "FROM movielist\n" +
                            "WHERE genre LIKE '%" + genre + "%'\n" +
                            "AND NOT REGEXP_LIKE(title, '^[a-z0-9A-Z]') \n" +
                            "AND director LIKE '%" + director + "%'\n" +
                            "AND stars LIKE '%" + star + "%'\n" +
                            "ORDER BY " + sort1 + ", " + sort2 + "\n" +
                            "LIMIT " + resultCount + "\n" +
                            "OFFSET " + (pageNum - 1) * resultCount + ";";

                    rowCount = "SELECT count(*) as count\n" +
                            "FROM movielist\n" +
                            "WHERE genre LIKE '%" + genre + "%'" +
                            "AND director LIKE '%" + director + "%'\n" +
                            "AND stars LIKE '%" + star + "%'\n" +
                            "AND NOT REGEXP_LIKE(title, '^[a-z0-9A-Z]');";
                }
            }
            else
            {
                if(!title.equals(">")) {
                    query = "SELECT *\n" +
                            "FROM movielist\n" +
                            "WHERE genre LIKE '%" + genre + "%'\n" +
                            "AND title LIKE '" + title + "'\n" +
                            "AND director LIKE '%" + director + "%'\n" +
                            "AND stars LIKE '%" + star + "%'\n" +
                            "AND year = " + year + "\n" +
                            "ORDER BY " + sort1 + ", " + sort2 + "\n" +
                            "LIMIT " + resultCount + "\n" +
                            "OFFSET " + (pageNum - 1) * resultCount + ";";

                    rowCount = "SELECT *\n" +
                            "FROM movielist\n" +
                            "WHERE genre LIKE '%" + genre + "%'\n" +
                            "AND title LIKE '" + title + "'\n" +
                            "AND director LIKE '%" + director + "%'\n" +
                            "AND stars LIKE '%" + star + "%'\n" +
                            "AND year = " + year + ";";
                }
                else
                {
                    query = "SELECT *\n" +
                            "FROM movielist\n" +
                            "WHERE genre LIKE '%" + genre + "%'\n" +
                            "AND NOT REGEXP_LIKE(title, '^[a-z0-9A-Z]')\n" +
                            "AND director LIKE '%" + director + "%'\n" +
                            "AND stars LIKE '%" + star + "%'\n" +
                            "AND year = " + year + "\n" +
                            "ORDER BY " + sort1 + ", " + sort2 + "\n" +
                            "LIMIT " + resultCount + "\n" +
                            "OFFSET " + (pageNum - 1) * resultCount + ";";

                    rowCount = "SELECT *\n" +
                            "FROM movielist\n" +
                            "WHERE genre LIKE '%" + genre + "%'\n" +
                            "AND NOT REGEXP_LIKE(title, '^[a-z0-9A-Z]')\n" +
                            "AND director LIKE '%" + director + "%'\n" +
                            "AND stars LIKE '%" + star + "%'\n" +
                            "AND year = " + year + ";";
                }
            }



            String matchId = "SELECT s.name FROM stars s WHERE id = ?";

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
                String movie_rating = rs.getString("rating");

                String parseId[] = rs.getString("starsId").split(", ");

                JsonObject jsonObject = new JsonObject();

                for(int i = 0; i < parseId.length; i++)
                {
                    PreparedStatement statement2 = dbcon.prepareStatement(matchId);

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

    public String getSort(String sortBy)
    {
        if(sortBy.equals("ratingA"))
            sortBy = "rating ASC";
        else if(sortBy.equals("ratingD"))
            sortBy = "rating DESC";
        else if(sortBy.equals("titleA"))
            sortBy = "title ASC";
        else if(sortBy.equals("titleD"))
            sortBy = "title DESC";

        return sortBy;
    }
}
