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

@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedbexample")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();

        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String stringCount = request.getParameter("count");
        String page = request.getParameter("page");
        String director = request.getParameter("director");
        String star = request.getParameter("star");
        String genre = request.getParameter("genre");
        String sort1 = getSort(request.getParameter("sort1"));
        String sort2 = getSort(request.getParameter("sort2"));

        String currentURL =  "movielist.html?title=" + title +  "&director=" + director + "&star=" + star + "&genre=" +
                genre + "&year=" + year + "&page=" + page + "&count=" +
                stringCount + "&sort1=" + request.getParameter("sort1") + "&sort2=" + request.getParameter("sort2");
        session.setAttribute("currentURL", currentURL);

        try {
            Connection dbcon = dataSource.getConnection();

            if(title.indexOf('<') != -1)
                title = title.replace('<','%');
            else if (!title.equals("~"))
                title = '%' + title + '%';

            if(year.isEmpty()) year = "%%";

            int pageNum = Integer.parseInt(page);
            int resultCount = Integer.parseInt(stringCount);

            String titleQuery = "";
            if(title.equals("~"))
                titleQuery = "regexp '^[^a-z0-9A-Z]'";
            else
                titleQuery = "LIKE ?";

            String query = "SELECT *\n" +
                    "FROM movielist \n" +
                    "WHERE genre LIKE ? \n" + // 1
                    "AND title " + titleQuery + " \n" + // 2
                    "AND director LIKE ? \n" + // 3
                    "AND stars LIKE ? \n" + // 4
                    "AND year LIKE ? \n"+ // 5
                    "ORDER BY " + sort1 +  ", " + sort2 + "\n" + // 6, 7
                    "LIMIT " + resultCount + "\n" +
                    "OFFSET " + (pageNum - 1) * resultCount + ";";

            String rowCount = "SELECT count(*) as count\n" +
                    "FROM movielist\n" +
                    "WHERE genre LIKE ? \n" + // 1
                    "AND title " + titleQuery + " \n" + // 2
                    "AND director LIKE ? \n" + // 3
                    "AND stars LIKE ? \n" + // 4
                    "AND year LIKE ?;"; // 5sh

            PreparedStatement statement = dbcon.prepareStatement(query);
            PreparedStatement countStatement = dbcon.prepareStatement(rowCount);

            int pos = 0;
            if(!title.equals("~")) {
                statement.setString(2, title);
                countStatement.setString(2, title);
            }
            else
                pos = 1;

            statement.setString(1, "%" + genre + "%");
            statement.setString(3 - pos, "%" + director + "%");
            statement.setString(4 - pos, "%" + star + "%");
            statement.setString(5 - pos, year);

            countStatement.setString(1, "%" + genre + "%");
            countStatement.setString(3 - pos, "%" + director + "%");
            countStatement.setString(4 - pos, "%" + star + "%");
            countStatement.setString(5 - pos, year);

            ResultSet rs = statement.executeQuery();
            ResultSet queryCount = countStatement.executeQuery();

            JsonArray jsonArray = new JsonArray();
            String matchId = "SELECT s.name FROM stars s WHERE id = ?";

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
                    PreparedStatement starStatement = dbcon.prepareStatement(matchId);

                    starStatement.setString(1, parseId[i]);
                    ResultSet rs2 = starStatement.executeQuery();
                    rs2.next();

                    String star_name = rs2.getString("name");
                    jsonObject.addProperty("star_name" + i, star_name);
                    jsonObject.addProperty("star_id" + i, parseId[i]);
                }

                if(parseId.length == 1)
                {
                    jsonObject.addProperty("star_name1", "");
                    jsonObject.addProperty("star_id1", "");
                    jsonObject.addProperty("star_name2", "");
                    jsonObject.addProperty("star_id2", "");
                }
                else if(parseId.length == 2)
                {
                    jsonObject.addProperty("star_name2", "");
                    jsonObject.addProperty("star_id2", "");
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
