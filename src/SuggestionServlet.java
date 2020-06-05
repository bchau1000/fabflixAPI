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

@WebServlet(name = "SuggestionServlet", urlPatterns = "/api/suggestion")
public class SuggestionServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String title = request.getParameter("query");
        title = processTitle(title);

        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");
            Connection dbcon = ds.getConnection();

            String suggestion = "SELECT * FROM movies WHERE MATCH(title) AGAINST(? IN BOOLEAN MODE) LIMIT 10;";

            PreparedStatement statement = dbcon.prepareStatement(suggestion);
            statement.setString(1, title);
            ResultSet suggestionSet = statement.executeQuery();

            JsonArray suggestionArray = new JsonArray();
            while(suggestionSet.next())
            {
                String movie_id = suggestionSet.getString("id");
                String movie_title = suggestionSet.getString("title");

                JsonObject movieObj = new JsonObject();
                movieObj.addProperty("data" , movie_id);
                movieObj.addProperty("value" , movie_title);

                suggestionArray.add(movieObj);
            }

            out.write(suggestionArray.toString());
            response.setStatus(200);

            statement.close();
            suggestionSet.close();
            dbcon.close();
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
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
