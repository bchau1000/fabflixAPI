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
            String titleQuery = "";

            if(year.isEmpty()) year = "%%";

            if(title.equals("") || title.isEmpty()) {
                title = "%%";
                titleQuery = "title LIKE ?";
            }
            else if(title.indexOf('<') != -1) {
                title = title.replace('<', '%');
                titleQuery = "title LIKE ?";
            }
            else if (title.equals("~")) {
                titleQuery = "title regexp '^[^a-z0-9A-Z]'";
            }
            else {
                title = processTitle(title);
                titleQuery = "MATCH(title) AGAINST(? IN BOOLEAN MODE)";
            }

            int pageNum = Integer.parseInt(page);
            int resultCount = Integer.parseInt(stringCount);

            String query = "SELECT *, FOUND_ROWS() as 'count'\n" +
                    "FROM movielist \n" +
                    "WHERE genre LIKE ? \n" +
                    "AND " + titleQuery + " \n" +
                    "AND director LIKE ? \n" +
                    "AND starnames LIKE ? \n" +
                    "AND year LIKE ? \n" +
                    "ORDER BY " + sort1 +  ", " + sort2 + "\n" +
                    "LIMIT " + resultCount + "\n" +
                    "OFFSET " + (pageNum - 1) * resultCount + ";";

            PreparedStatement statement = dbcon.prepareStatement(query);

            int pos = 0;

            if(!title.equals("~"))
                statement.setString(2, title);
            else
                pos = 1;

            statement.setString(1, "%" + genre + "%");
            statement.setString(3 - pos, "%" + director + "%");
            statement.setString(4 - pos, "%" + star + "%");
            statement.setString(5 - pos, year);

            System.out.println(query);

            ResultSet rs = statement.executeQuery();
            JsonArray jsonArray = new JsonArray();

            boolean firstLoop = true;

            while (rs.next()) {
                String query_count = "";

                if(firstLoop) {
                    query_count = rs.getString("count");
                    JsonObject getCount = new JsonObject();
                    getCount.addProperty("query_count", query_count);
                    jsonArray.add(getCount);

                    firstLoop = false;
                }

                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String genre_name = rs.getString("genre");
                String movie_dir = rs.getString("director");
                String movie_yr = rs.getString("year");
                String movie_rating = rs.getString("rating");
                String movie_stars = rs.getString("starids");
                String star_names = rs.getString("starnames");

                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("star_names", star_names);
                jsonObject.addProperty("star_id", movie_stars);
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("genre_name", genre_name);
                jsonObject.addProperty("movie_dir", movie_dir);
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

    public String processTitle(String title) {
        String result = "";

        if(!title.isEmpty()) {
            String[] tokens = title.split(" ");

            for (int i = 0; i < tokens.length; i++) {
                result += "+" + tokens[i] + "*";

                if (i < tokens.length - 1)
                    result += " ";
            }
        }

        return result;
    }
}
