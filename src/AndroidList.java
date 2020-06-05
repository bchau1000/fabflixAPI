import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.Context;
import javax.naming.InitialContext;
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

@WebServlet(name = "AndroidList", urlPatterns = "/api/androidlist")
public class AndroidList extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pageString = request.getParameter("pageNum");
        int page = Integer.parseInt(pageString);

        String title = request.getParameter("query");
        title = processTitle(title);
        System.out.println("Query: " + title);

        PrintWriter out = response.getWriter();
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");
            Connection dbcon = ds.getConnection();

            String query = "SELECT * FROM movies WHERE MATCH(title) AGAINST(? IN BOOLEAN MODE) ORDER BY id DESC LIMIT 20 OFFSET ?;";
            PreparedStatement statement = dbcon.prepareStatement(query);
            statement.setString(1, title);
            statement.setInt(2, page * 20);
            System.out.println(statement.toString());
            ResultSet rs = statement.executeQuery();

            JsonArray arr = new JsonArray();
            while(rs.next())
            {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_dir = rs.getString("director");

                JsonObject obj = new JsonObject();
                obj.addProperty("id", movie_id);
                obj.addProperty("title", movie_title);
                obj.addProperty("year", movie_year);
                obj.addProperty("director", movie_dir);

                arr.add(obj);
            }

            out.write(arr.toString());

            rs.close();
            statement.close();
            dbcon.close();
            out.close();
        } catch (Exception e) {
            out.write(e.getMessage());
            out.close();
        }
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
